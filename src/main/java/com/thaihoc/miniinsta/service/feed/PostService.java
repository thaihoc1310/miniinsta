package com.thaihoc.miniinsta.service.feed;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.CreatePostRequest;
import com.thaihoc.miniinsta.dto.feed.PostResponse;
import com.thaihoc.miniinsta.dto.feed.UpdatePostRequest;
import com.thaihoc.miniinsta.model.Post;

public interface PostService {
  // Tạo bài đăng mới
  Post createPost(UserPrincipal userPrincipal, CreatePostRequest request);

  // Cập nhật bài đăng
  Post updatePost(UserPrincipal userPrincipal, int postId, UpdatePostRequest request);

  // Lấy bài đăng theo ID
  PostResponse getPost(UserPrincipal userPrincipal, int postId);

  // Lấy bài đăng theo ID (cho người dùng không đăng nhập)
  PostResponse getPost(int postId);

  // Xóa bài đăng
  void deletePost(UserPrincipal userPrincipal, int postId);

  // Thích bài đăng
  Post likePost(UserPrincipal userPrincipal, int postId);

  // Bỏ thích bài đăng
  Post unlikePost(UserPrincipal userPrincipal, int postId);

  // Kiểm tra người dùng đã thích bài đăng chưa
  boolean isPostLiked(UserPrincipal userPrincipal, int postId);

  // Lấy bài đăng của một người dùng (theo profile ID)
  Page<PostResponse> getUserPosts(UserPrincipal currentUser, int profileId, Pageable pageable);

  // Lấy bài đăng của người dùng hiện tại
  Page<PostResponse> getCurrentUserPosts(UserPrincipal userPrincipal, Pageable pageable);

  // Lấy bài đăng được người dùng hiện tại thích
  Page<PostResponse> getLikedPosts(UserPrincipal userPrincipal, Pageable pageable);

  // Tìm kiếm bài đăng theo caption
  Page<PostResponse> searchPosts(UserPrincipal userPrincipal, String q, Pageable pageable);

  // Tìm kiếm bài đăng theo hashtag
  Page<PostResponse> getPostsByHashtag(UserPrincipal userPrincipal, String hashtag, Pageable pageable);

  // Tìm kiếm bài đăng theo vị trí
  Page<PostResponse> getPostsByLocation(UserPrincipal userPrincipal, String location, Pageable pageable);

  // Lấy bài đăng phổ biến (explore)
  Page<PostResponse> getPopularPosts(UserPrincipal userPrincipal, Pageable pageable);

  // Lấy những người thích một bài đăng
  List<Integer> getPostLikers(int postId, int limit);
}
