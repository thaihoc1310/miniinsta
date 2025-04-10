package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Comment;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.CommentRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final PostService postService;
  private final ProfileService profileService;

  public CommentServiceImpl(CommentRepository commentRepository,
      PostService postService,
      ProfileService profileService) {
    this.commentRepository = commentRepository;
    this.postService = postService;
    this.profileService = profileService;
  }

  private Comment handleGetCommentById(long commentId) throws IdInvalidException {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new IdInvalidException("Comment not found"));
  }

  private CommentResponse convertToCommentResponse(Comment comment) {
    Profile currentUser = profileService.handleGetCurrentUserProfile();
    boolean likedByCurrentUser = currentUser == null ? false
        : commentRepository.isCommentLikedByProfile(comment.getId(), currentUser.getId()) > 0;

    return CommentResponse.builder()
        .comment(comment)
        .likedByCurrentUser(likedByCurrentUser)
        .build();
  }

  @Override
  @Transactional
  public Comment createComment(long postId, CreateCommentRequest request) throws IdInvalidException {
    Post post = postService.handleGetPostById(postId);

    Profile profile = profileService.getProfileById(request.getProfileId());

    Comment comment = Comment.builder()
        .author(profile)
        .post(post)
        .comment(request.getComment())
        .likes(new HashSet<>())
        .likeCount(0)
        .replies(new ArrayList<>())
        .build();

    post.setCommentCount(post.getCommentCount() + 1);
    postService.savePost(post);

    return commentRepository.save(comment);
  }

  @Override
  @Transactional
  public Comment replyToComment(long commentId, CreateCommentRequest request) throws IdInvalidException {
    Comment parentComment = handleGetCommentById(commentId);
    Profile profile = profileService.getProfileById(request.getProfileId());

    Comment reply = Comment.builder()
        .author(profile)
        .post(parentComment.getPost())
        .parentComment(parentComment)
        .comment(request.getComment())
        .likes(new HashSet<>())
        .likeCount(0)
        .replies(new ArrayList<>())
        .build();

    // Update post's comment count
    Post post = parentComment.getPost();
    post.setCommentCount(post.getCommentCount() + 1);
    postService.savePost(post);

    return commentRepository.save(reply);
  }

  @Override
  @Transactional
  public void deleteComment(long postId, long commentId) throws IdInvalidException {
    Comment comment = handleGetCommentById(commentId);

    if (comment.getPost().getId() != postId) {
      throw new IdInvalidException("Comment does not belong to the specified post");
    }

    Post post = comment.getPost();
    int totalComments = 1 + comment.getReplies().size();
    post.setCommentCount(Math.max(0, post.getCommentCount() - totalComments));
    postService.savePost(post);

    commentRepository.delete(comment);
  }

  @Override
  @Transactional
  public void deleteReply(long commentId, long replyId) throws IdInvalidException {
    Comment reply = handleGetCommentById(replyId);

    // Verify this is actually a reply to the specified comment
    if (reply.getParentComment() == null || reply.getParentComment().getId() != commentId) {
      throw new IdInvalidException("Reply does not belong to the specified comment");
    }

    // Update post's comment count
    Post post = reply.getPost();
    post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
    postService.savePost(post);

    commentRepository.delete(reply);
  }

  @Override
  @Transactional
  public void likeComment(long commentId, long likerId) throws IdInvalidException {
    Comment comment = handleGetCommentById(commentId);
    Profile liker = profileService.getProfileById(likerId);

    if (commentRepository.isCommentLikedByProfile(commentId, likerId) == 0) {
      comment.getLikes().add(liker);
      comment.setLikeCount(comment.getLikeCount() + 1);
      commentRepository.save(comment);
    }
  }

  @Override
  @Transactional
  public void unlikeComment(long commentId, long likerId) throws IdInvalidException {
    Comment comment = handleGetCommentById(commentId);
    Profile liker = profileService.getProfileById(likerId);

    if (commentRepository.isCommentLikedByProfile(commentId, likerId) > 0) {
      comment.getLikes().remove(liker);
      comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
      commentRepository.save(comment);
    }
  }

  @Override
  public ResultPaginationDTO getAllComments(long postId, Pageable pageable) throws IdInvalidException {
    // Post post = postService.handleGetPostById(postId);
    Page<Comment> comments = commentRepository.findMostLikedComments(postId, pageable);
    return createPaginationResult(comments, pageable);
  }

  @Override
  public ResultPaginationDTO getAllCommentReplies(long commentId, Pageable pageable) throws IdInvalidException {
    // Comment comment = handleGetCommentById(commentId);
    Page<Comment> replies = commentRepository.findByParentCommentOrderByCreatedAtDesc(commentId, pageable);
    return createPaginationResult(replies, pageable);
  }

  private ResultPaginationDTO createPaginationResult(Page<Comment> page, Pageable pageable) {
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());
    mt.setPages(page.getTotalPages());
    mt.setTotal(page.getTotalElements());
    rs.setMeta(mt);
    List<CommentResponse> comments = page.getContent().stream()
        .map(this::convertToCommentResponse)
        .toList();
    rs.setResult(comments);
    return rs;
  }
}
