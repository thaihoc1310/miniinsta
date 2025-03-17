package com.thaihoc.miniinsta.service.feed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.exception.CommentNotFoundException;
import com.thaihoc.miniinsta.exception.NoPermissionException;
import com.thaihoc.miniinsta.exception.PostNotFoundException;
import com.thaihoc.miniinsta.model.Comment;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.NotificationType;
import com.thaihoc.miniinsta.repository.CommentRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.notification.NotificationService;
import com.thaihoc.miniinsta.service.profile.ProfileService;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired
  private ProfileService profileService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private NotificationService notificationService;

  @Override
  @Transactional
  public CommentResponse createComment(UserPrincipal userPrincipal, CreateCommentRequest request) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Post post = postRepository.findById(request.getPostId())
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + request.getPostId()));

    Comment comment = new Comment();
    comment.setComment(request.getComment());
    comment.setCreatedAt(LocalDateTime.now());
    comment.setCreatedBy(profile);
    comment.setPost(post);

    Comment savedComment = commentRepository.save(comment);

    // Thêm comment vào post và cập nhật count
    post.getComments().add(savedComment);
    post.setCommentCount(post.getCommentCount() + 1);
    postRepository.save(post);

    // Gửi thông báo cho người tạo bài viết
    if (post.getCreatedBy().getId() != profile.getId()) {
      notificationService.createNotification(
          post.getCreatedBy(),
          profile,
          profile.getUsername() + " commented on your post: " + truncateComment(request.getComment()),
          NotificationType.COMMENT,
          post.getId(),
          savedComment.getId());
    }

    return convertToCommentResponse(savedComment, profile);
  }

  @Override
  @Transactional
  public CommentResponse replyToComment(UserPrincipal userPrincipal, int parentCommentId,
      CreateCommentRequest request) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Comment parentComment = commentRepository.findById(parentCommentId)
        .orElseThrow(() -> new CommentNotFoundException("Parent comment not found with id: " + parentCommentId));

    Post post = parentComment.getPost();

    Comment reply = new Comment();
    reply.setComment(request.getComment());
    reply.setCreatedAt(LocalDateTime.now());
    reply.setCreatedBy(profile);
    reply.setPost(post);
    reply.setParentComment(parentComment);

    Comment savedReply = commentRepository.save(reply);

    // Thêm comment vào post và cập nhật count
    post.getComments().add(savedReply);
    post.setCommentCount(post.getCommentCount() + 1);
    postRepository.save(post);

    // Gửi thông báo cho người comment gốc
    if (parentComment.getCreatedBy().getId() != profile.getId()) {
      notificationService.createNotification(
          parentComment.getCreatedBy(),
          profile,
          profile.getUsername() + " replied to your comment: " + truncateComment(request.getComment()),
          NotificationType.COMMENT,
          post.getId(),
          savedReply.getId());
    }

    return convertToCommentResponse(savedReply, profile);
  }

  @Override
  @Transactional
  public void deleteComment(UserPrincipal userPrincipal, int commentId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

    // Chỉ chủ sở hữu comment hoặc chủ bài viết mới có thể xóa
    if (comment.getCreatedBy().getId() != profile.getId() &&
        comment.getPost().getCreatedBy().getId() != profile.getId()) {
      throw new NoPermissionException("You don't have permission to delete this comment");
    }

    Post post = comment.getPost();
    post.getComments().remove(comment);
    post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
    postRepository.save(post);

    commentRepository.delete(comment);
  }

  @Override
  @Transactional
  public CommentResponse likeComment(UserPrincipal userPrincipal, int commentId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

    if (!comment.getLikes().contains(profile)) {
      comment.getLikes().add(profile);
      comment.setLikeCount(comment.getLikeCount() + 1);
      Comment savedComment = commentRepository.save(comment);

      // Gửi thông báo cho người viết comment
      if (comment.getCreatedBy().getId() != profile.getId()) {
        notificationService.createNotification(
            comment.getCreatedBy(),
            profile,
            profile.getUsername() + " liked your comment: " + truncateComment(comment.getComment()),
            NotificationType.COMMENT_LIKE,
            comment.getPost().getId(),
            comment.getId());
      }

      return convertToCommentResponse(savedComment, profile);
    }

    return convertToCommentResponse(comment, profile);
  }

  @Override
  @Transactional
  public CommentResponse unlikeComment(UserPrincipal userPrincipal, int commentId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

    if (comment.getLikes().contains(profile)) {
      comment.getLikes().remove(profile);
      comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
      Comment savedComment = commentRepository.save(comment);
      return convertToCommentResponse(savedComment, profile);
    }

    return convertToCommentResponse(comment, profile);
  }

  @Override
  public boolean isCommentLiked(UserPrincipal userPrincipal, int commentId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    return commentRepository.isCommentLikedByProfile(commentId, profile.getId()) > 0;
  }

  @Override
  public Page<CommentResponse> getPostComments(UserPrincipal userPrincipal, int postId, Pageable pageable) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    final Profile profile = currentProfile;

    // Chỉ lấy top-level comments (không phải replies)
    Page<Comment> comments = commentRepository.findByPostAndParentCommentIsNull(post, pageable);

    return comments.map(comment -> {
      boolean isLiked = profile != null && comment.getLikes().contains(profile);
      return convertToCommentResponseWithReplies(comment, profile, isLiked);
    });
  }

  @Override
  public Page<CommentResponse> getCommentReplies(UserPrincipal userPrincipal, int commentId, Pageable pageable) {
    Comment parentComment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    final Profile profile = currentProfile;

    Page<Comment> replies = commentRepository.findByParentComment(parentComment, pageable);

    return replies.map(reply -> {
      boolean isLiked = profile != null && reply.getLikes().contains(profile);
      return convertToCommentResponse(reply, profile, isLiked);
    });
  }

  @Override
  public Page<CommentResponse> getTopComments(UserPrincipal userPrincipal, int postId, Pageable pageable) {
    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    final Profile profile = currentProfile;

    Page<Comment> topComments = commentRepository.findMostLikedComments(postId, pageable);

    return topComments.map(comment -> {
      boolean isLiked = profile != null && comment.getLikes().contains(profile);
      return convertToCommentResponse(comment, profile, isLiked);
    });
  }

  @Override
  public Page<CommentResponse> getUserComments(UserPrincipal userPrincipal, int profileId, Pageable pageable) {
    Profile targetProfile = profileService.getProfileById(profileId);

    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    final Profile profile = currentProfile;

    Page<Comment> userComments = commentRepository.findByCreatedBy(targetProfile, pageable);

    return userComments.map(comment -> {
      boolean isLiked = profile != null && comment.getLikes().contains(profile);
      return convertToCommentResponse(comment, profile, isLiked);
    });
  }

  @Override
  public CommentResponse getComment(UserPrincipal userPrincipal, int commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    boolean isLiked = currentProfile != null && comment.getLikes().contains(currentProfile);
    return convertToCommentResponseWithReplies(comment, currentProfile, isLiked);
  }

  // Helper methods

  private CommentResponse convertToCommentResponse(Comment comment, Profile currentProfile) {
    boolean isLiked = currentProfile != null && comment.getLikes().contains(currentProfile);
    return convertToCommentResponse(comment, currentProfile, isLiked);
  }

  private CommentResponse convertToCommentResponse(Comment comment, Profile currentProfile, boolean isLiked) {
    ProfileResponse profileResponse = ProfileResponse.builder()
        .id(comment.getCreatedBy().getId())
        .username(comment.getCreatedBy().getUsername())
        .displayName(comment.getCreatedBy().getDisplayName())
        .profilePictureUrl(comment.getCreatedBy().getProfilePictureUrl())
        .isVerified(comment.getCreatedBy().isVerified())
        .build();

    return CommentResponse.builder()
        .id(comment.getId())
        .comment(comment.getComment())
        .createdAt(comment.getCreatedAt())
        .createdBy(profileResponse)
        .likeCount(comment.getLikeCount())
        .likedByCurrentUser(isLiked)
        .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
        .replies(Collections.emptyList()) // Không load replies ở đây để tránh quá nhiều dữ liệu
        .build();
  }

  private CommentResponse convertToCommentResponseWithReplies(Comment comment, Profile currentProfile,
      boolean isLiked) {
    ProfileResponse profileResponse = ProfileResponse.builder()
        .id(comment.getCreatedBy().getId())
        .username(comment.getCreatedBy().getUsername())
        .displayName(comment.getCreatedBy().getDisplayName())
        .profilePictureUrl(comment.getCreatedBy().getProfilePictureUrl())
        .isVerified(comment.getCreatedBy().isVerified())
        .build();

    // Lấy top 3 replies
    List<Comment> replies = commentRepository.findByParentComment(comment, Pageable.ofSize(3)).getContent();
    List<CommentResponse> replyResponses = new ArrayList<>();

    if (!replies.isEmpty()) {
      replyResponses = replies.stream()
          .map(reply -> convertToCommentResponse(reply, currentProfile))
          .collect(Collectors.toList());
    }

    return CommentResponse.builder()
        .id(comment.getId())
        .comment(comment.getComment())
        .createdAt(comment.getCreatedAt())
        .createdBy(profileResponse)
        .likeCount(comment.getLikeCount())
        .likedByCurrentUser(isLiked)
        .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
        .replies(replyResponses)
        .build();
  }

  private String truncateComment(String comment) {
    if (comment.length() <= 30) {
      return comment;
    }
    return comment.substring(0, 27) + "...";
  }
}
