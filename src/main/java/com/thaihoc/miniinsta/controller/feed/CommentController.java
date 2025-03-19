package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.service.feed.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comments")

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Tạo bình luận mới
     */
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            Authentication authentication,
            @Valid @RequestBody CreateCommentRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        CommentResponse comment = commentService.createComment(userPrincipal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    /**
     * Trả lời một bình luận
     */
    @PostMapping("/{id}/replies")
    public ResponseEntity<CommentResponse> replyToComment(
            Authentication authentication,
            @PathVariable int id,
            @Valid @RequestBody CreateCommentRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        CommentResponse comment = commentService.replyToComment(userPrincipal, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    /**
     * Xóa một bình luận
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.deleteComment(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Thích một bình luận
     */
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeComment(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.likeComment(userPrincipal, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Bỏ thích một bình luận
     */
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeComment(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.unlikeComment(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Kiểm tra người dùng đã thích bình luận chưa
     */
    @GetMapping("/{id}/likes/status")
    public ResponseEntity<Boolean> isCommentLiked(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.isCommentLiked(userPrincipal, id));
    }

    /**
     * Lấy tất cả bình luận của một bài đăng
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<CommentResponse>> getPostComments(
            Authentication authentication,
            @PathVariable int postId,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getPostComments(userPrincipal, postId, pageable));
    }

    /**
     * Lấy các trả lời cho một bình luận
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<Page<CommentResponse>> getCommentReplies(
            Authentication authentication,
            @PathVariable int id,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getCommentReplies(userPrincipal, id, pageable));
    }

    /**
     * Lấy các bình luận hàng đầu của một bài đăng
     */
    @GetMapping("/posts/{postId}/top")
    public ResponseEntity<Page<CommentResponse>> getTopComments(
            Authentication authentication,
            @PathVariable int postId,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getTopComments(userPrincipal, postId, pageable));
    }

    /**
     * Lấy tất cả bình luận của một người dùng
     */
    @GetMapping("/users/{profileId}")
    public ResponseEntity<Page<CommentResponse>> getUserComments(
            Authentication authentication,
            @PathVariable int profileId,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getUserComments(userPrincipal, profileId, pageable));
    }

    /**
     * Lấy chi tiết một bình luận
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getComment(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getComment(userPrincipal, id));
    }
}
