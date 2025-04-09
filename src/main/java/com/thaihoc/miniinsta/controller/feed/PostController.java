package com.thaihoc.miniinsta.controller.feed;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import com.turkraft.springfilter.boot.Filter;

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
            @PathVariable int profileId,
            @Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(profileId, request));
    }

    @PutMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable int profileId,
            @PathVariable int postId,
            @Valid @RequestBody UpdatePostRequest request) {
        return ResponseEntity.ok(postService.updatePost(profileId, postId, request));
    }

    @GetMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable int profileId,
            @PathVariable int postId) {
        return ResponseEntity.ok(postService.getPostById(postId, profileId));
    }

    @DeleteMapping("profiles/{profileId}/posts/{postId}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable int profileId,
            @PathVariable int postId) {
        postService.deletePostById(profileId, postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("profiles/{profileId}/posts/{postId}/likes")
    public ResponseEntity<Void> likePost(
            @PathVariable int profileId,
            @PathVariable int postId,
            @Valid @RequestBody LikePostRequest request) {
        postService.likePost(profileId, postId, request.getLikerId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("profiles/{profileId}/posts/{postId}/likes/{likerId}")
    public ResponseEntity<Void> unlikePost(
            @PathVariable int profileId,
            @PathVariable int postId,
            @PathVariable int likerId) {
        postService.unlikePost(profileId, postId, likerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("profiles/{profileId}/posts")
    public ResponseEntity<Page<PostResponse>> getAllPostsByProfileId(
            @PathVariable int profileId,
            @Filter Specification<Post> spec,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPostsByProfileId(profileId, spec, pageable));
    }

    @GetMapping("profiles/{profileId}/liked_posts")
    public ResponseEntity<Page<PostResponse>> getLikedPosts(
            @PathVariable int profileId,
            @Filter Specification<Post> spec,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getLikedPostsByProfileId(profileId, spec, pageable));
    }

    @GetMapping("hashtags/{hashtag}/posts")
    public ResponseEntity<Page<PostResponse>> getAllPostsByHashtag(
            @PathVariable String hashtag,
            @Filter Specification<Post> spec,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByHashtag(hashtag, spec, pageable));
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
            @PathVariable int profileId,
            @PathVariable int postId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getPostLikers(profileId, postId, limit));
    }
}
