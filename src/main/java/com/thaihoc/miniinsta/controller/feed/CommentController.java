package com.thaihoc.miniinsta.controller.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.dto.feed.GetPostResponse;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.service.feed.CommentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/comments")
public class CommentController {
  @Autowired
  CommentService commentService;

  @PostMapping()
  public ResponseEntity<GetPostResponse> createComment(
      @Valid @RequestBody CreateCommentRequest request, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Post post = commentService.createComment(userPrincipal, request);
    return ResponseEntity.ok().body(GetPostResponse.builder().post(post).build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GetPostResponse> deleteComment(@PathVariable int id, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Post post = commentService.deleteComment(userPrincipal, id);
    return ResponseEntity.ok().body(GetPostResponse.builder().post(post).build());
  }
}
