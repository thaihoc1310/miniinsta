package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CommentResponse;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;

public interface CommentService {
  // Tạo comment mới
  CommentResponse createComment(UserPrincipal userPrincipal, CreateCommentRequest request);

  // Tạo reply cho comment
  CommentResponse replyToComment(UserPrincipal userPrincipal, int parentCommentId, CreateCommentRequest request);

  // Xóa comment
  void deleteComment(UserPrincipal userPrincipal, int commentId);

  // Like comment
  CommentResponse likeComment(UserPrincipal userPrincipal, int commentId);

  // Unlike comment
  CommentResponse unlikeComment(UserPrincipal userPrincipal, int commentId);

  // Kiểm tra người dùng đã like comment hay chưa
  boolean isCommentLiked(UserPrincipal userPrincipal, int commentId);

  // Lấy tất cả comment của bài viết
  Page<CommentResponse> getPostComments(UserPrincipal userPrincipal, int postId, Pageable pageable);

  // Lấy replies của một comment
  Page<CommentResponse> getCommentReplies(UserPrincipal userPrincipal, int commentId, Pageable pageable);

  // Lấy top comments của bài viết (sắp xếp theo lượt like)
  Page<CommentResponse> getTopComments(UserPrincipal userPrincipal, int postId, Pageable pageable);

  // Lấy comments của một người dùng
  Page<CommentResponse> getUserComments(UserPrincipal userPrincipal, int profileId, Pageable pageable);

  // Lấy comment theo ID
  CommentResponse getComment(UserPrincipal userPrincipal, int commentId);
}
