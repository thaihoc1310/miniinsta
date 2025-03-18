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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Tạo bài đăng mới
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
     * Cập nhật bài đăng
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
     * Lấy chi tiết bài đăng theo ID
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
     * Xóa bài đăng
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
     * Thích bài đăng
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
     * Bỏ thích bài đăng
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
     * Kiểm tra bài đăng đã được thích chưa
     */
    @GetMapping("/{id}/likes/status")
    public ResponseEntity<Boolean> isPostLiked(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.isPostLiked(userPrincipal, id));
    }

    /**
     * Lấy bài đăng của người dùng theo ID
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
     * Lấy bài đăng của người dùng hiện tại
     */
    @GetMapping("/users/me")
    public ResponseEntity<Page<PostResponse>> getCurrentUserPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getCurrentUserPosts(userPrincipal, pageable));
    }

    /**
     * Lấy danh sách bài đăng đã thích của người dùng hiện tại
     */
    @GetMapping("/users/me/likes")
    public ResponseEntity<Page<PostResponse>> getLikedPosts(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getLikedPosts(userPrincipal, pageable));
    }

    /**
     * Tìm kiếm bài đăng
     */
    @GetMapping("/search")
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
     * Lấy bài đăng theo hashtag
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
     * Lấy bài đăng theo vị trí
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
     * Lấy bài đăng phổ biến
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
     * Lấy danh sách người dùng đã thích bài đăng
     */
    @GetMapping("/{id}/likes")
    public ResponseEntity<List<Integer>> getPostLikers(
            @PathVariable int id,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getPostLikers(id, limit));
    }
}
