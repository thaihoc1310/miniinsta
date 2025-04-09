package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Post;

public interface PostService {
  Post createPost(long profileId, CreatePostRequest request) throws IdInvalidException;

  Post updatePost(long profileId, long postId, UpdatePostRequest request) throws IdInvalidException;

  PostResponse getPostById(long postId, long profileId) throws IdInvalidException;

  void deletePostById(long profileId, long postId) throws IdInvalidException;

  void likePost(long profileId, long postId, long likerId) throws IdInvalidException;

  void unlikePost(long profileId, long postId, long likerId) throws IdInvalidException;

  ResultPaginationDTO getAllPosts(Specification<Post> spec, Pageable pageable);

  ResultPaginationDTO getAllPostsByProfileId(long profileId, Pageable pageable) throws IdInvalidException;

  ResultPaginationDTO getLikedPostsByProfileId(long profileId, Pageable pageable);

  ResultPaginationDTO getPostsByHashtag(String hashtag, Pageable pageable);

}
