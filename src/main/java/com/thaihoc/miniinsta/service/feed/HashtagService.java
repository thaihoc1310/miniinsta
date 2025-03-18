package com.thaihoc.miniinsta.service.feed;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.hashtag.HashtagResponse;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;

public interface HashtagService {

    /**
     * Lấy hashtag theo name
     */
    Hashtag getHashtagByName(String name);

    /**
     * Tạo hashtag mới (nếu chưa tồn tại)
     */
    Hashtag createHashtagIfNotExists(String name);

    /**
     * Xử lý các hashtag từ caption
     */
    Set<Hashtag> extractHashtagsFromText(String text);

    /**
     * Liên kết post với các hashtag
     */
    void linkPostWithHashtags(Post post, Set<Hashtag> hashtags);

    /**
     * Tìm kiếm hashtag theo từ khóa
     */
    Page<HashtagResponse> searchHashtags(String q, Pageable pageable);

    /**
     * Lấy danh sách hashtag phổ biến
     */
    Page<HashtagResponse> getTrendingHashtags(Pageable pageable);

    /**
     * Lấy các post theo hashtag
     */
    List<Post> getPostsByHashtag(String hashtagName, int limit);

    /**
     * Lấy danh sách các hashtag của một post
     */
    List<HashtagResponse> getHashtagsByPost(int postId);

    /**
     * Gỡ bỏ hashtag khỏi post
     */
    void removeHashtagFromPost(int postId, String hashtagName);

    /**
     * Cập nhật postCount của hashtag
     */
    void updateHashtagPostCount();
}