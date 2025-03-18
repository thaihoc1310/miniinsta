package com.thaihoc.miniinsta.controller.feed;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.CreatePostResponse;
import com.thaihoc.miniinsta.dto.feed.GetPostResponse;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.service.feed.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.createPost(userPrincipal, request);
        return ResponseEntity.ok(CreatePostResponse.builder().post(post).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetPostResponse> updatePost(
            Authentication authentication,
            @PathVariable int id,
            @Valid @RequestBody UpdatePostRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.updatePost(userPrincipal, id, request);
        return ResponseEntity.ok(GetPostResponse.builder().post(post).build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        postService.deletePost(userPrincipal, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<GetPostResponse> likePost(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.likePost(userPrincipal, id);
        return ResponseEntity.ok(GetPostResponse.builder().post(post).build());
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<GetPostResponse> unlikePost(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Post post = postService.unlikePost(userPrincipal, id);
        return ResponseEntity.ok(GetPostResponse.builder().post(post).build());
    }

    @GetMapping("/{id}/isLiked")
    public ResponseEntity<Boolean> isPostLiked(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.isPostLiked(userPrincipal, id));
    }

    @GetMapping("/user/{profileId}")
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

    @GetMapping("/me")
    public ResponseEntity<Page<PostResponse>> getCurrentUserPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getCurrentUserPosts(userPrincipal, pageable));
    }

    @GetMapping("/me/liked")
    public ResponseEntity<Page<PostResponse>> getLikedPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getLikedPosts(userPrincipal, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            Authentication authentication,
            @RequestParam String searchTerm,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(postService.searchPosts(userPrincipal, searchTerm, pageable));
    }

    @GetMapping("/hashtag/{hashtag}")
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

    @GetMapping("/location/{location}")
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

    @GetMapping("/{id}/likers")
    public ResponseEntity<List<Integer>> getPostLikers(
            @PathVariable int id,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getPostLikers(id, limit));
    }
}
