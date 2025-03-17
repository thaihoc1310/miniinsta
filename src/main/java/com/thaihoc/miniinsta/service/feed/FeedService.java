package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.PostResponse;

public interface FeedService {
  // Lấy feed cho người dùng đăng nhập
  Page<PostResponse> getFeed(UserPrincipal userPrincipal, Pageable pageable);

  // Lấy feed explore (khám phá) cho người dùng đăng nhập
  Page<PostResponse> getExploreFeed(UserPrincipal userPrincipal, Pageable pageable);

  // Lấy feed theo hashtag
  Page<PostResponse> getHashtagFeed(UserPrincipal userPrincipal, String hashtag, Pageable pageable);

  // Lấy feed người dùng theo vị trí
  Page<PostResponse> getLocationFeed(UserPrincipal userPrincipal, String location, Pageable pageable);

  // Cập nhật feed khi có bài đăng mới (được gọi bởi job bất đồng bộ)
  void updateFeedsWithNewPost(int postId);

  // Cập nhật feed khi bài đăng bị xóa (được gọi bởi job bất đồng bộ)
  void removePostFromFeeds(int postId);

  // Xây dựng lại feed người dùng (dùng cho jobs)
  void rebuildUserFeed(int profileId);
}
