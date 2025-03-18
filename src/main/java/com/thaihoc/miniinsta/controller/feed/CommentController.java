package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.service.feed.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            Authentication authentication,
            @Valid @RequestBody CreateCommentRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.createComment(userPrincipal, request));
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<CommentResponse> replyToComment(
            Authentication authentication,
            @PathVariable int commentId,
            @Valid @RequestBody CreateCommentRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.replyToComment(userPrincipal, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            Authentication authentication,
            @PathVariable int commentId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        commentService.deleteComment(userPrincipal, commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentResponse> likeComment(
            Authentication authentication,
            @PathVariable int commentId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.likeComment(userPrincipal, commentId));
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<CommentResponse> unlikeComment(
            Authentication authentication,
            @PathVariable int commentId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.unlikeComment(userPrincipal, commentId));
    }

    @GetMapping("/{commentId}/isLiked")
    public ResponseEntity<Boolean> isCommentLiked(
            Authentication authentication,
            @PathVariable int commentId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.isCommentLiked(userPrincipal, commentId));
    }

    @GetMapping("/post/{postId}")
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

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponse>> getCommentReplies(
            Authentication authentication,
            @PathVariable int commentId,
            Pageable pageable) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getCommentReplies(userPrincipal, commentId, pageable));
    }

    @GetMapping("/post/{postId}/top")
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

    @GetMapping("/user/{profileId}")
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

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(
            Authentication authentication,
            @PathVariable int commentId) {
        UserPrincipal userPrincipal = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userPrincipal = (UserPrincipal) authentication.getPrincipal();
        }
        return ResponseEntity.ok(commentService.getComment(userPrincipal, commentId));
    }
}
