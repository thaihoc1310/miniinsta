package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;

public interface CommentService {
  // Create new comment
  CommentResponse createComment(UserPrincipal userPrincipal, CreateCommentRequest request);

  // Create reply to comment
  CommentResponse replyToComment(UserPrincipal userPrincipal, int parentCommentId, CreateCommentRequest request);

  // Delete comment
  void deleteComment(UserPrincipal userPrincipal, int commentId);

  // Like comment
  CommentResponse likeComment(UserPrincipal userPrincipal, int commentId);

  // Unlike comment
  CommentResponse unlikeComment(UserPrincipal userPrincipal, int commentId);

  // Check if user has liked the comment
  boolean isCommentLiked(UserPrincipal userPrincipal, int commentId);

  // Get all comments of a post
  Page<CommentResponse> getPostComments(UserPrincipal userPrincipal, int postId, Pageable pageable);

  // Get replies to a comment
  Page<CommentResponse> getCommentReplies(UserPrincipal userPrincipal, int commentId, Pageable pageable);

  // Get top comments of a post (sorted by likes)
  Page<CommentResponse> getTopComments(UserPrincipal userPrincipal, int postId, Pageable pageable);

  // Get comments from a user
  Page<CommentResponse> getUserComments(UserPrincipal userPrincipal, int profileId, Pageable pageable);

  // Get comment by ID
  CommentResponse getComment(UserPrincipal userPrincipal, int commentId);
}
