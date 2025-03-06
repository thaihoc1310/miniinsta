package com.thaihoc.miniinsta.service.feed;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreateCommentRequest;
import com.thaihoc.miniinsta.exception.CommentNotFoundException;
import com.thaihoc.miniinsta.exception.NoPermissionException;
import com.thaihoc.miniinsta.exception.PostNotFoundException;
import com.thaihoc.miniinsta.model.Comment;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.CommentRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.thaihoc.miniinsta.service.profile.ProfileService;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired
  private ProfileService profileService;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Override
  public Post createComment(UserPrincipal userPrincipal, CreateCommentRequest request) {
    Profile profile = profileService.getUserProfile(userPrincipal);
    Post post = postRepository.findById(request.getPostId()).orElseThrow(PostNotFoundException::new);
    Comment comment = new Comment();
    comment.setComment(request.getComment());
    comment.setCreatedAt(new Date());
    comment.setCreatedBy(profile);
    comment.setPost(post);
    commentRepository.save(comment);
    return post;
  }

  @Override
  public Post deleteComment(UserPrincipal userPrincipal, int commentId) {
    Profile profile = profileService.getUserProfile(userPrincipal);
    Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
    if (comment.getCreatedBy().getId() != profile.getId()) {
      throw new NoPermissionException();
    }
    commentRepository.delete(comment);
    return comment.getPost();
  }

}
