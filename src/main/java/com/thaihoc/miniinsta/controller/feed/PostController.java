package com.thaihoc.miniinsta.controller.feed;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.service.feed.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/")

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("profiles/{profileId}/posts")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request) {
        Post post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body());
    }

    @PutMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @Valid @RequestBody UpdatePostRequest request) {
        Post post = postService.updatePost(id, request);
        return ResponseEntity.ok(postService.getPost(post.getId()));
    }

    @GetMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable int id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @DeleteMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable int id) {
        postService.deletePostById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("profiles/{profileId}/posts/{postId}/likes")
    public ResponseEntity<Void> likePost(
            @PathVariable int id) {
        postService.likePost(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("profiles/{profileId}/posts/{postId}/likes")
    public ResponseEntity<Void> unlikePost(
            @PathVariable int id) {
        postService.unlikePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("profiles/{profileId}/posts")
    public ResponseEntity<Page<PostResponse>> getAllPostsByProfileId(
            @PathVariable int profileId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPostsByProfileId(profileId, pageable));
    }

    @GetMapping("profiles/{profileId}/liked_posts")
    public ResponseEntity<Page<PostResponse>> getLikedPosts(
            Pageable pageable) {
        return ResponseEntity.ok(postService.getLikedPosts(pageable));
    }

    @GetMapping("hashtags/{hashtag}/posts")
    public ResponseEntity<Page<PostResponse>> getAllPostsByHashtag(
            @PathVariable String hashtag,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByHashtag(hashtag, pageable));
    }

    // /**
    // * Get popular posts
    // */
    // @GetMapping("/popular")
    // public ResponseEntity<Page<PostResponse>> getPopularPosts(
    // Authentication authentication,
    // Pageable pageable) {
    // UserPrincipal userPrincipal = null;
    // if (authentication != null && authentication.isAuthenticated()) {
    // userPrincipal = (UserPrincipal) authentication.getPrincipal();
    // }
    // return ResponseEntity.ok(postService.getPopularPosts(userPrincipal,
    // pageable));
    // }

    @GetMapping("profiles/{profileId}/posts/{postId}/likes")
    public ResponseEntity<List<Integer>> getPostLikers(
            @PathVariable int id,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getPostLikers(id, limit));
    }
}
