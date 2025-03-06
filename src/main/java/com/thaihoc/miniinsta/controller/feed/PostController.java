package com.thaihoc.miniinsta.controller.feed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.CreatePostResponse;
import com.thaihoc.miniinsta.dto.feed.DeletePostResponse;
import com.thaihoc.miniinsta.dto.feed.GetPostResponse;
import com.thaihoc.miniinsta.dto.feed.GetUserPostResponse;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.service.feed.PostService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/posts")
public class PostController {
  @Autowired
  private PostService postService;

  @PostMapping()
  public ResponseEntity<CreatePostResponse> createPost(
      @Valid @RequestBody CreatePostRequest request, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Post post = postService.createPost(userPrincipal, request);
    return ResponseEntity.ok().body(CreatePostResponse.builder().post(post).build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetPostResponse> getPost(@PathVariable int id) {
    Post post = postService.getPost(id);
    return ResponseEntity.ok().body(GetPostResponse.builder().post(post).build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<DeletePostResponse> deletePost(@PathVariable int id, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    postService.deletePost(userPrincipal, id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/like/{id}")
  public ResponseEntity<GetPostResponse> likePost(@PathVariable int id, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Post post = postService.likePost(userPrincipal, id);
    return ResponseEntity.ok().body(GetPostResponse.builder().post(post).build());
  }

  @DeleteMapping("/like/{id}")
  public ResponseEntity<GetPostResponse> unlikePost(@PathVariable int id, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Post post = postService.unlikePost(userPrincipal, id);
    return ResponseEntity.ok().body(GetPostResponse.builder().post(post).build());
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<GetUserPostResponse> getUserPosts(@PathVariable int id) {
    List<Post> posts = postService.getUserPosts(id);
    return ResponseEntity.ok().body(GetUserPostResponse.builder().posts(posts).build());
  }
}
