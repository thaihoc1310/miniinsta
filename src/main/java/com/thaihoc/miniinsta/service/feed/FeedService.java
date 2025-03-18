package com.thaihoc.miniinsta.service.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.feed.PostResponse;

public interface FeedService {
  /**
   * Lấy feed chính cho người dùng hiện tại (bài đăng từ người dùng đang theo dõi)
   */
  Page<PostResponse> getFeed(UserPrincipal userPrincipal, Pageable pageable);

  /**
   * Lấy explore feed (bài đăng được đề xuất để khám phá)
   */
  Page<PostResponse> getExploreFeed(UserPrincipal userPrincipal, Pageable pageable);

  /**
   * Xóa một bài đăng khỏi tất cả feed (khi bài đăng bị xóa)
   */
  void removePostFromFeeds(int postId);

  /**
   * Xây dựng lại feed cho một người dùng (API dành cho admin)
   */
  void rebuildUserFeed(int profileId);
}
