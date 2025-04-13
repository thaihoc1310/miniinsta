package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Comment;

public interface CommentService {
  Comment createComment(long postId, CreateCommentRequest request) throws IdInvalidException;

  Comment replyToComment(long commentId, CreateCommentRequest request) throws IdInvalidException;

  void deleteComment(long postId, long commentId) throws IdInvalidException;

  void deleteReply(long commentId, long replyId) throws IdInvalidException;

  void likeComment(long commentId, long likerId) throws IdInvalidException;

  void unlikeComment(long commentId, long likerId) throws IdInvalidException;

  ResultPaginationDTO getAllComments(long postId, Pageable pageable) throws IdInvalidException;

  ResultPaginationDTO getAllCommentReplies(long commentId, Pageable pageable) throws IdInvalidException;

}
