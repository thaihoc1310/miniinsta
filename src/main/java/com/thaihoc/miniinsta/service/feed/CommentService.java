package com.thaihoc.miniinsta.service.feed;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.model.Post;

public interface CommentService {
  Post createComment(UserPrincipal userPrincipal, CreateCommentRequest request);

  Post deleteComment(UserPrincipal userPrincipal, int commentId);
}
