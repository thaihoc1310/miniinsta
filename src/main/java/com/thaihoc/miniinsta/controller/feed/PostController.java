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
@RequestMapping("/api/v1/posts")

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Create new post
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.createPost(userPrincipal, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.getPost(userPrincipal, post.getId()));
    }

    /**
     * Update post
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            Authentication authentication,
            @PathVariable int id,
            @Valid @RequestBody UpdatePostRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.updatePost(userPrincipal, id, request);
        return ResponseEntity.ok(postService.getPost(userPrincipal, post.getId()));
    }

    /**
     * Get post details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(
            Authentication authentication,
            @PathVariable int id) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return ResponseEntity.ok(postService.getPost(userPrincipal, id));
        }
        return ResponseEntity.ok(postService.getPost(id));
    }

    /**
     * Delete post
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        postService.deletePost(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Like post
     */
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likePost(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        postService.likePost(userPrincipal, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Unlike post
     */
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikePost(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        postService.unlikePost(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if post is liked
     */
    @GetMapping("/{id}/likes/status")
    public ResponseEntity<Boolean> isPostLiked(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.isPostLiked(userPrincipal, id));
    }

    /**
     * Get posts by user ID
     */
    @GetMapping("/users/{profileId}")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            Authentication authentication,
            @PathVariable int profileId,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(postService.getUserPosts(userPrincipal, profileId, pageable));
    }

    /**
     * Get current user's posts
     */
    @GetMapping("/users/me")
    public ResponseEntity<Page<PostResponse>> getCurrentUserPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getCurrentUserPosts(userPrincipal, pageable));
    }

    /**
     * Get posts liked by current user
     */
    @GetMapping("/users/me/likes")
    public ResponseEntity<Page<PostResponse>> getLikedPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getLikedPosts(userPrincipal, pageable));
    }

    /**
     * Search posts
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> searchPosts(
            Authentication authentication,
            @RequestParam String q,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(postService.searchPosts(userPrincipal, q, pageable));
    }

    /**
     * Get posts by hashtag
     */
    @GetMapping("/hashtags/{hashtag}")
    public ResponseEntity<Page<PostResponse>> getPostsByHashtag(
            Authentication authentication,
            @PathVariable String hashtag,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(postService.getPostsByHashtag(userPrincipal, hashtag, pageable));
    }

    /**
     * Get posts by location
     */
    @GetMapping("/locations/{location}")
    public ResponseEntity<Page<PostResponse>> getPostsByLocation(
            Authentication authentication,
            @PathVariable String location,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(postService.getPostsByLocation(userPrincipal, location, pageable));
    }

    /**
     * Get popular posts
     */
    @GetMapping("/popular")
    public ResponseEntity<Page<PostResponse>> getPopularPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(postService.getPopularPosts(userPrincipal, pageable));
    }

    /**
     * Get list of users who liked the post
     */
    @GetMapping("/{id}/likes")
    public ResponseEntity<List<Integer>> getPostLikers(
            @PathVariable int id,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getPostLikers(id, limit));
    }
}
