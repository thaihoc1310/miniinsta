package com.thaihoc.miniinsta.controller.feed;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.dto.feed.LikeCommentRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.service.feed.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/")

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable long postId,
            @Valid @RequestBody CreateCommentRequest request) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentService.createComment(postId,
                request));
    }

    @PostMapping("comments/{commentId}/replies")
    public ResponseEntity<CommentResponse> replyToComment(
            @PathVariable long commentId,
            @Valid @RequestBody CreateCommentRequest request) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentService.replyToComment(commentId,
                request));
    }

    @DeleteMapping("posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable long postId,
            @PathVariable long commentId) throws IdInvalidException {
        this.commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("comments/{commentId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable long commentId,
            @PathVariable long replyId) throws IdInvalidException {
        this.commentService.deleteReply(commentId, replyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("comments/{commentId}/likes")
    public ResponseEntity<Void> likeComment(
            @PathVariable long commentId,
            @Valid @RequestBody LikeCommentRequest request) throws IdInvalidException {
        this.commentService.likeComment(commentId, request.getLikerId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("comments/{commentId}/likes/{likerId}")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable long commentId,
            @PathVariable long likerId) throws IdInvalidException {
        this.commentService.unlikeComment(commentId, likerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all comments of a post
     */
    @GetMapping("posts/{postId}/comments")
    public ResponseEntity<ResultPaginationDTO> getAllComments(
            @PathVariable long postId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllComments(postId, pageable));
    }

    /**
     * Get replies for a comment
     */
    @GetMapping("comments/{commentId}/replies")
    public ResponseEntity<ResultPaginationDTO> getAllReplies(
            @PathVariable long commentId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllCommentReplies(commentId, pageable));
    }

    // /**
    // * Get top comments of a post
    // */
    // @GetMapping("/posts/{postId}/top")
    // public ResponseEntity<Page<CommentResponse>> getTopComments(
    // Authentication authentication,
    // @PathVariable int postId,
    // Pageable pageable) {
    // UserPrincipal userPrincipal = null;
    // if (authentication != null && authentication.isAuthenticated()) {
    // userPrincipal = (UserPrincipal) authentication.getPrincipal();
    // }
    // return ResponseEntity.ok(commentService.getTopComments(userPrincipal, postId,
    // pageable));
    // }

}
