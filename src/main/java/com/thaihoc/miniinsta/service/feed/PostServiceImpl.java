package com.thaihoc.miniinsta.service.feed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.config.MessageQueueConfig;
import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.exception.NoPermissionException;
import com.thaihoc.miniinsta.exception.PostNotFoundException;
import com.thaihoc.miniinsta.model.Comment;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.CommentRepository;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.repository.ProfileRepository;
import com.thaihoc.miniinsta.service.FileService;
import com.thaihoc.miniinsta.service.notification.NotificationService;
import com.thaihoc.miniinsta.service.profile.ProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostServiceImpl implements PostService {
  @Autowired
  private ProfileService profileService;

  @Autowired
  private FileService fileService;

  @Autowired
  private HashtagService hashtagService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  // @Autowired
  // private ObjectMapper objectMapper;

  @Override
  @Transactional
  public Post createPost(UserPrincipal userPrincipal, CreatePostRequest request) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    String url = fileService.uploadImage(request.getBase64ImageString());

    Post post = new Post();
    post.setCaption(request.getCaption());
    post.setCreatedAt(LocalDateTime.now());
    post.setCreatedBy(profile);
    post.setImageUrl(url);
    post.setLocation(request.getLocation());

    // Xử lý hashtags
    Set<Hashtag> hashtags = new HashSet<>();

    // Trích xuất hashtags từ caption
    if (request.getCaption() != null && !request.getCaption().isEmpty()) {
      hashtags.addAll(hashtagService.extractHashtagsFromText(request.getCaption()));
    }

    // Thêm các hashtags được cung cấp trực tiếp
    if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
      for (String tag : request.getHashtags()) {
        hashtags.add(hashtagService.createHashtagIfNotExists(tag));
      }
    }

    // Lưu post trước để có ID
    Post savedPost = postRepository.save(post);

    // Liên kết hashtags với post
    if (!hashtags.isEmpty()) {
      hashtagService.linkPostWithHashtags(savedPost, hashtags);
    }

    // Cập nhật số bài viết của profile
    profile.setPostsCount(profile.getPostsCount() + 1);
    profileRepository.save(profile);

    // Chỉ giữ lại việc thêm vào explore nếu profile không private
    if (!profile.isPrivate()) {
      feedRepository.addPostToExplore(savedPost.getId());
    }

    // Thêm vào feed theo hashtag
    for (Hashtag hashtag : hashtags) {
      feedRepository.addPostToHashtagFeed(savedPost.getId(), hashtag.getName());
    }

    // Gửi thông báo đến RabbitMQ để xử lý bất đồng bộ việc cập nhật feed của
    // followers
    rabbitTemplate.convertAndSend(MessageQueueConfig.AFTER_CREATE_POST_QUEUE, savedPost.getId());

    return savedPost;
  }

  @Override
  @Transactional
  public Post updatePost(UserPrincipal userPrincipal, int postId, UpdatePostRequest request) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

    if (post.getCreatedBy().getId() != profile.getId()) {
      throw new NoPermissionException("You do not have permission to update this post");
    }

    post.setCaption(request.getCaption());
    post.setLocation(request.getLocation());

    // Xử lý hashtags mới
    Set<Hashtag> hashtags = new HashSet<>();

    // Trích xuất hashtags từ caption
    if (request.getCaption() != null && !request.getCaption().isEmpty()) {
      hashtags.addAll(hashtagService.extractHashtagsFromText(request.getCaption()));
    }

    // Thêm các hashtags được cung cấp trực tiếp
    if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
      for (String tag : request.getHashtags()) {
        hashtags.add(hashtagService.createHashtagIfNotExists(tag));
      }
    }

    // Cập nhật liên kết hashtags với post
    hashtagService.linkPostWithHashtags(post, hashtags);

    return postRepository.save(post);
  }

  @Override
  public PostResponse getPost(UserPrincipal userPrincipal, int postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

    Profile currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    boolean isLiked = isPostLiked(userPrincipal, postId);

    // Lấy top comments
    List<Comment> comments = commentRepository.findTop5ByPostOrderByLikeCountDesc(post);

    return buildPostResponse(post, currentProfile, isLiked, comments);
  }

  @Override
  public PostResponse getPost(int postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

    // Lấy top comments
    List<Comment> comments = commentRepository.findTop5ByPostOrderByLikeCountDesc(post);

    return buildPostResponse(post, null, false, comments);
  }

  @Override
  @Transactional
  public void deletePost(UserPrincipal userPrincipal, int postId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

    if (post.getCreatedBy().getId() != profile.getId()) {
      throw new NoPermissionException("You do not have permission to delete this post");
    }

    // Cập nhật số bài viết của profile
    profile.setPostsCount(Math.max(0, profile.getPostsCount() - 1));
    profileRepository.save(profile);

    // Xóa bài đăng khỏi tất cả feed
    feedRepository.removePostFromFeeds(postId);

    // Xóa post (và các liên kết với hashtag, like, comments theo cascade)
    postRepository.delete(post);
  }

  @Override
  @Transactional
  public Post likePost(UserPrincipal userPrincipal, int postId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Post post = getPostEntity(postId);

    if (!post.getUserLikes().contains(profile)) {
      post.getUserLikes().add(profile);
      post.setLikeCount(post.getLikeCount() + 1);
      postRepository.save(post);

      // Thông báo cho chủ bài viết
      notificationService.createNotification(
          post.getCreatedBy(),
          profile,
          profile.getUsername() + " liked your post",
          com.thaihoc.miniinsta.model.enums.NotificationType.LIKE,
          postId,
          null);
    }

    return post;
  }

  @Override
  @Transactional
  public Post unlikePost(UserPrincipal userPrincipal, int postId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Post post = getPostEntity(postId);

    if (post.getUserLikes().contains(profile)) {
      post.getUserLikes().remove(profile);
      post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
      postRepository.save(post);
    }

    return post;
  }

  @Override
  public boolean isPostLiked(UserPrincipal userPrincipal, int postId) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    return postRepository.isPostLikedByProfile(postId, profile.getId()) > 0;
  }

  @Override
  public Page<PostResponse> getUserPosts(UserPrincipal currentUser, int profileId, Pageable pageable) {
    Profile targetProfile = profileService.getProfileById(profileId);
    Profile currentProfile = null;

    if (currentUser != null) {
      currentProfile = profileService.getCurrentUserProfile(currentUser);
    }

    if (targetProfile.isPrivate() &&
        (currentProfile == null ||
            (!targetProfile.getFollowers().contains(currentProfile) &&
                targetProfile.getId() != currentProfile.getId()))) {
      return Page.empty(pageable);
    }

    Page<Post> posts = postRepository.findByCreatedBy(targetProfile, pageable);
    return mapToPostResponsePage(posts, currentProfile);
  }

  @Override
  public Page<PostResponse> getCurrentUserPosts(UserPrincipal userPrincipal, Pageable pageable) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Page<Post> posts = postRepository.findByCreatedBy(profile, pageable);
    return mapToPostResponsePage(posts, profile);
  }

  @Override
  public Page<PostResponse> getLikedPosts(UserPrincipal userPrincipal, Pageable pageable) {
    Profile profile = profileService.getCurrentUserProfile(userPrincipal);
    Page<Post> likedPosts = postRepository.findLikedPosts(profile.getId(), pageable);
    return mapToPostResponsePage(likedPosts, profile);
  }

  @Override
  public Page<PostResponse> searchPosts(UserPrincipal userPrincipal, String searchTerm, Pageable pageable) {
    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    Page<Post> posts = postRepository.searchPosts(searchTerm, pageable);
    return mapToPostResponsePage(posts, currentProfile);
  }

  @Override
  public Page<PostResponse> getPostsByHashtag(UserPrincipal userPrincipal, String hashtag, Pageable pageable) {
    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    Hashtag hashtagEntity = hashtagService.getHashtagByName(hashtag);
    Page<Post> posts = postRepository.findByHashtags(hashtagEntity, pageable);
    return mapToPostResponsePage(posts, currentProfile);
  }

  @Override
  public Page<PostResponse> getPostsByLocation(UserPrincipal userPrincipal, String location, Pageable pageable) {
    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    Page<Post> posts = postRepository.findPostsByLocation(location, pageable);
    return mapToPostResponsePage(posts, currentProfile);
  }

  @Override
  public Page<PostResponse> getPopularPosts(UserPrincipal userPrincipal, Pageable pageable) {
    Profile currentProfile = null;
    if (userPrincipal != null) {
      currentProfile = profileService.getCurrentUserProfile(userPrincipal);
    }

    Page<Post> popularPosts = postRepository.findPopularPosts(pageable);
    return mapToPostResponsePage(popularPosts, currentProfile);
  }

  @Override
  public List<Integer> getPostLikers(int postId, int limit) {
    Post post = getPostEntity(postId);
    return post.getUserLikes().stream()
        .limit(limit)
        .map(Profile::getId)
        .collect(Collectors.toList());
  }

  // Helper methods

  private Post getPostEntity(int postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
  }

  private Page<PostResponse> mapToPostResponsePage(Page<Post> posts, Profile currentProfile) {
    return posts.map(post -> {
      boolean isLiked = false;

      if (currentProfile != null) {
        isLiked = post.getUserLikes().contains(currentProfile);
      }

      List<Comment> comments = commentRepository.findTop5ByPostOrderByLikeCountDesc(post);

      return buildPostResponse(post, currentProfile, isLiked, comments);
    });
  }

  private PostResponse buildPostResponse(Post post, Profile currentProfile, boolean isLiked, List<Comment> comments) {
    List<CommentResponse> commentResponses = new ArrayList<>();

    if (comments != null && !comments.isEmpty()) {
      commentResponses = comments.stream()
          .map(comment -> buildCommentResponse(comment, currentProfile))
          .collect(Collectors.toList());
    }

    List<String> hashtags = post.getHashtags().stream()
        .map(h -> "#" + h.getName())
        .collect(Collectors.toList());

    ProfileResponse profileResponse = ProfileResponse.builder()
        .id(post.getCreatedBy().getId())
        .username(post.getCreatedBy().getUsername())
        .displayName(post.getCreatedBy().getDisplayName())
        .profilePictureUrl(post.getCreatedBy().getProfilePictureUrl())
        .isVerified(post.getCreatedBy().isVerified())
        .build();

    return PostResponse.builder()
        .id(post.getId())
        .imageUrl(post.getImageUrl())
        .caption(post.getCaption())
        .createdAt(post.getCreatedAt())
        .location(post.getLocation())
        .createdBy(profileResponse)
        .comments(commentResponses)
        .commentCount(post.getCommentCount())
        .likeCount(post.getLikeCount())
        .likedByCurrentUser(isLiked)
        .hashtags(hashtags)
        .build();
  }

  private CommentResponse buildCommentResponse(Comment comment, Profile currentProfile) {
    boolean isLiked = false;

    if (currentProfile != null) {
      isLiked = comment.getLikes().contains(currentProfile);
    }

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
}
