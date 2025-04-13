package com.thaihoc.miniinsta.service.feed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.dto.notification.PostCreatedEvent;
import com.thaihoc.miniinsta.dto.notification.PostLikedEvent;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.FileService;
import com.thaihoc.miniinsta.service.user.ProfileService;

@Service
public class PostServiceImpl implements PostService {
  private FileService fileService;

  private HashtagService hashtagService;

  private PostRepository postRepository;

  private ProfileService profileService;

  private FeedRepository feedRepository;

  private RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchange.name}")
  private String notificationExchange;

  private final String RK_POST_CREATED = "post.notification.created";
  private final String RK_POST_LIKED = "post.notification.liked";

  public PostServiceImpl(FileService fileService, HashtagService hashtagService, PostRepository postRepository,
      ProfileService profileService, FeedRepository feedRepository, RabbitTemplate rabbitTemplate) {
    this.fileService = fileService;
    this.hashtagService = hashtagService;
    this.postRepository = postRepository;
    this.profileService = profileService;
    this.feedRepository = feedRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  private Post handleGetPostByIdAndProfileId(long postId, long profileId) throws IdInvalidException {
    Profile profile = profileService.getProfileById(profileId);
    return postRepository.findByIdAndAuthor(postId, profile)
        .orElseThrow(() -> new IdInvalidException("Post not found"));
  }

  @Override
  public Post handleGetPostById(long postId) throws IdInvalidException {
    return postRepository.findById(postId)
        .orElseThrow(() -> new IdInvalidException("Post not found"));
  }

  @Override
  public PostResponse convertToPostResponse(Post post) throws IdInvalidException {
    Profile profile = profileService.handleGetCurrentUserProfile();
    boolean likedByCurrentUser = profile == null ? false
        : this.postRepository.isPostLikedByProfile(post.getId(),
            profile.getId()) > 0;
    return PostResponse.builder()
        .post(post)
        .likedByCurrentUser(likedByCurrentUser)
        .build();
  }

  @Override
  @Transactional
  public Post createPost(long profileId, CreatePostRequest request)
      throws IdInvalidException, AlreadyExistsException {
    Profile profile = profileService.getProfileById(profileId);
    profile.setPostsCount(profile.getPostsCount() + 1);
    this.profileService.saveProfile(profile);

    String imageUrl = fileService.uploadImage(request.getBase64ImageString());

    Post post = Post.builder().author(profile)
        .imageUrl(imageUrl)
        .content(request.getCaption())
        .likeCount(0)
        .commentCount(0)
        .userLikes(new ArrayList<>())
        .build();

    if (request.getCaption() != null && !request.getCaption().isEmpty()) {
      post.setHashtags(hashtagService.extractHashtagsFromText(request.getCaption()));
      this.hashtagService.updateHashtagPostCount(post.getHashtags(), 1);
    }

    addPostToFollowersFeeds(post.getId(), profileId);

    PostCreatedEvent postCreatedEvent = new PostCreatedEvent(profileId, profileId, post.getId(),
        profile.getDisplayName());
    rabbitTemplate.convertAndSend(notificationExchange, RK_POST_CREATED, postCreatedEvent);

    return this.postRepository.save(post);
  }

  private void addPostToFollowersFeeds(long postId, long profileId) throws IdInvalidException {
    List<Profile> followers = profileService.getProfileById(profileId).getFollowers();
    for (Profile follower : followers) {
      feedRepository.addPostToFeed(postId, follower.getId());
    }
    feedRepository.addPostToFeed(postId, profileId);
  }

  @Override
  @Transactional
  public Post updatePost(long profileId, long postId, UpdatePostRequest request)
      throws IdInvalidException, AlreadyExistsException {
    Post post = handleGetPostByIdAndProfileId(postId, profileId);

    if (request.getContent() != null && !request.getContent().equals(post.getContent())) {
      post.setContent(request.getContent());

      List<Hashtag> oldHashtags = post.getHashtags();
      List<Hashtag> newHashtags = hashtagService.extractHashtagsFromText(request.getContent());
      post.setHashtags(newHashtags);
      this.hashtagService.updateHashtagPostCount(oldHashtags, -1);
      this.hashtagService.updateHashtagPostCount(newHashtags, 1);

      this.postRepository.save(post);
    }

    return post;
  }

  @Override
  public PostResponse getPostById(long postId, long profileId) throws IdInvalidException {
    Post post = handleGetPostByIdAndProfileId(postId, profileId);

    return convertToPostResponse(post);
  }

  @Override
  @Transactional
  public void deletePostById(long profileId, long postId) throws IdInvalidException {
    Post post = handleGetPostByIdAndProfileId(postId, profileId);
    Profile profile = post.getAuthor();
    // Update profile's post count
    profile.setPostsCount(Math.max(0, profile.getPostsCount() - 1));
    this.profileService.saveProfile(profile);

    // Remove post from all feeds
    // feedRepository.removePostFromFeeds(postId);

    postRepository.delete(post);
  }

  @Override
  @Transactional
  public void likePost(long postId, long likerId) throws IdInvalidException {
    Profile liker = profileService.getProfileById(likerId);

    Post post = handleGetPostById(postId);

    if (this.postRepository.isPostLikedByProfile(postId, likerId) == 0) {
      post.getUserLikes().add(liker);
      post.setLikeCount(post.getLikeCount() + 1);
      this.postRepository.save(post);

      PostLikedEvent postLikedEvent = new PostLikedEvent(liker.getId(), post.getAuthor().getId(), postId,
          liker.getDisplayName());
      rabbitTemplate.convertAndSend(notificationExchange, RK_POST_LIKED, postLikedEvent);
    }
  }

  @Override
  @Transactional
  public void unlikePost(long postId, long likerId) throws IdInvalidException {
    Profile liker = profileService.getProfileById(likerId);

    Post post = handleGetPostById(postId);

    if (this.postRepository.isPostLikedByProfile(postId, likerId) > 0) {
      post.getUserLikes().remove(liker);
      post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
      this.postRepository.save(post);
    }
  }

  @Override
  public ResultPaginationDTO getAllPosts(Specification<Post> spec, Pageable pageable) {
    Page<Post> posts = postRepository.findAll(spec, pageable);

    return createPaginationResult(posts, pageable);
  }

  @Override
  public ResultPaginationDTO getAllPostsByProfileId(long profileId, Pageable pageable) throws IdInvalidException {
    Profile profile = profileService.getProfileById(profileId);
    Page<Post> posts = postRepository.findByAuthor(profile, pageable);
    return createPaginationResult(posts, pageable);
  }

  private ResultPaginationDTO createPaginationResult(Page<Post> page, Pageable pageable) {
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());
    mt.setPages(page.getTotalPages());
    mt.setTotal(page.getTotalElements());
    rs.setMeta(mt);
    List<Post> listPost = page.getContent();
    rs.setResult(listPost);
    return rs;
  }

  @Override
  public List<Post> getPostsByIds(List<Long> postIds) {
    return postRepository.findByIdIn(postIds);
  }

  @Override
  public ResultPaginationDTO getLikedPostsByProfileId(long profileId, Pageable pageable) {
    Page<Post> likedPosts = postRepository.findLikedPosts(profileId, pageable);
    return createPaginationResult(likedPosts, pageable);
  }

  @Override
  public ResultPaginationDTO getPostsByHashtag(String hashtag, Pageable pageable) throws IdInvalidException {
    Hashtag hashtagEntity = hashtagService.getHashtagByName(hashtag);
    Page<Post> posts = postRepository.findByHashtag(hashtagEntity, pageable);
    return createPaginationResult(posts, pageable);
  }

  @Override
  public void savePost(Post post) {
    this.postRepository.save(post);
  }
}
