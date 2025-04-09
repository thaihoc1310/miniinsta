package com.thaihoc.miniinsta.service.feed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.FileService;
import com.thaihoc.miniinsta.service.user.ProfileService;

@Service
public class PostServiceImpl implements PostService {
  private FileService fileService;

  private HashtagService hashtagService;

  private PostRepository postRepository;

  private ProfileService profileService;

  public PostServiceImpl(FileService fileService, HashtagService hashtagService, PostRepository postRepository,
      ProfileService profileService) {
    this.fileService = fileService;
    this.hashtagService = hashtagService;
    this.postRepository = postRepository;
    this.profileService = profileService;
  }

  private Post handleGetPostByIdAndProfileId(long postId, long profileId) throws IdInvalidException {
    Profile profile = profileService.getProfileById(profileId);
    return postRepository.findByIdAndAuthor(postId, profile)
        .orElseThrow(() -> new IdInvalidException("Post not found"));
  }

  private PostResponse convertToPostResponse(Post post) throws IdInvalidException {
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
  public Post createPost(long profileId, CreatePostRequest request) throws IdInvalidException {
    Profile profile = profileService.getProfileById(profileId);
    profile.setPostsCount(profile.getPostsCount() + 1);
    this.profileService.saveProfile(profile);

    String imageUrl = fileService.uploadImage(request.getBase64ImageString());

    Post post = Post.builder().author(profile)
        .imageUrl(imageUrl)
        .content(request.getCaption())
        .likeCount(0)
        .commentCount(0)
        .userLikes(new HashSet<>())
        .build();

    List<Hashtag> hashtags = new ArrayList<>();
    if (request.getCaption() != null && !request.getCaption().isEmpty()) {
      hashtags.addAll(hashtagService.extractHashtagsFromText(request.getCaption()));
    }
    post.setHashtags(hashtags);

    // // Add to explore if profile is not private
    // if (!profile.isPrivate()) {
    // feedRepository.addPostToExplore((long) savedPost.getId());
    // }

    // // Send message to RabbitMQ to process feed updates
    // rabbitTemplate.convertAndSend(MessageQueueConfig.AFTER_CREATE_POST_QUEUE,
    // savedPost.getId());

    return this.postRepository.save(post);
  }

  @Override
  @Transactional
  public Post updatePost(long profileId, long postId, UpdatePostRequest request) throws IdInvalidException {
    Post post = handleGetPostByIdAndProfileId(postId, profileId);

    if (request.getContent() != null && !request.getContent().equals(post.getContent())) {
      post.setContent(request.getContent());

      List<Hashtag> hashtags = new ArrayList<>();
      if (request.getContent() != null && !request.getContent().isEmpty()) {
        hashtags.addAll(hashtagService.extractHashtagsFromText(request.getContent()));
      }

      post.setHashtags(hashtags);

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
  public void likePost(long profileId, long postId, long likerId) throws IdInvalidException {
    Profile liker = profileService.getProfileById(likerId);

    Post post = handleGetPostByIdAndProfileId(postId, profileId);

    if (this.postRepository.isPostLikedByProfile(postId, likerId) == 0) {
      post.getUserLikes().add(liker);
      post.setLikeCount(post.getLikeCount() + 1);
      this.postRepository.save(post);

      // // Send notification to post author
      // if (profileId != likerId) {
      // log.info("Like notification would be sent here");
      // }
    }
  }

  @Override
  @Transactional
  public void unlikePost(long profileId, long postId, long likerId) throws IdInvalidException {
    Profile liker = profileService.getProfileById(likerId);

    Post post = handleGetPostByIdAndProfileId(postId, profileId);

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
  public ResultPaginationDTO getLikedPostsByProfileId(long profileId, Pageable pageable) {
    Page<Post> likedPosts = postRepository.findLikedPosts(profileId, pageable);
    return createPaginationResult(likedPosts, pageable);
  }

  @Override
  public ResultPaginationDTO getPostsByHashtag(String hashtag, Pageable pageable) {
    Hashtag hashtagEntity = hashtagService.getHashtagByName(hashtag);
    Page<Post> posts = postRepository.findByHashtag(hashtagEntity, pageable);
    return createPaginationResult(posts, pageable);
  }

}
