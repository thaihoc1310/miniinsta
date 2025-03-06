package com.thaihoc.miniinsta.service.feed;

import java.util.List;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.model.Post;

public interface PostService {
  Post createPost(UserPrincipal userPrincipal, CreatePostRequest request);

  Post getPost(int postId);

  void deletePost(UserPrincipal userPrincipal, int postId);

  Post likePost(UserPrincipal userPrincipal, int postId);

  Post unlikePost(UserPrincipal userPrincipal, int postId);

  List<Post> getUserPosts(int userId);
}
