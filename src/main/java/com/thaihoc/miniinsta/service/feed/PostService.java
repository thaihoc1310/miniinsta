package com.thaihoc.miniinsta.service.feed;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.model.Post;

public interface PostService {
  // Create new post
  Post createPost(UserPrincipal userPrincipal, CreatePostRequest request);

  // Update post
  Post updatePost(UserPrincipal userPrincipal, int postId, UpdatePostRequest request);

  // Get post by ID
  PostResponse getPost(UserPrincipal userPrincipal, int postId);

  // Get post by ID (for non-logged-in users)
  PostResponse getPost(int postId);

  // Delete post
  void deletePost(UserPrincipal userPrincipal, int postId);

  // Like post
  Post likePost(UserPrincipal userPrincipal, int postId);

  // Unlike post
  Post unlikePost(UserPrincipal userPrincipal, int postId);

  // Check if user has liked the post
  boolean isPostLiked(UserPrincipal userPrincipal, int postId);

  // Get posts by a user (by profile ID)
  Page<PostResponse> getUserPosts(UserPrincipal currentUser, int profileId, Pageable pageable);

  // Get current user's posts
  Page<PostResponse> getCurrentUserPosts(UserPrincipal userPrincipal, Pageable pageable);

  // Get posts liked by current user
  Page<PostResponse> getLikedPosts(UserPrincipal userPrincipal, Pageable pageable);

  // Search posts by caption
  Page<PostResponse> searchPosts(UserPrincipal userPrincipal, String q, Pageable pageable);

  // Search posts by hashtag
  Page<PostResponse> getPostsByHashtag(UserPrincipal userPrincipal, String hashtag, Pageable pageable);

  // Search posts by location
  Page<PostResponse> getPostsByLocation(UserPrincipal userPrincipal, String location, Pageable pageable);

  // Get popular posts (explore)
  Page<PostResponse> getPopularPosts(UserPrincipal userPrincipal, Pageable pageable);

  // Get users who liked a post
  List<Integer> getPostLikers(int postId, int limit);
}
