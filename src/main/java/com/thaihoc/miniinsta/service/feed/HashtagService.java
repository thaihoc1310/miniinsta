package com.thaihoc.miniinsta.service.feed;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.feed.HashtagResponse;
import com.thaihoc.miniinsta.model.Hashtag;
import com.thaihoc.miniinsta.model.Post;

public interface HashtagService {

    /**
     * Get hashtag by name
     */
    Hashtag getHashtagByName(String name);

    /**
     * Create new hashtag (if it doesn't exist)
     */
    Hashtag createHashtagIfNotExists(String name);

    /**
     * Process hashtags from text
     */
    Set<Hashtag> extractHashtagsFromText(String text);

    /**
     * Link post with hashtags
     */
    void linkPostWithHashtags(Post post, Set<Hashtag> hashtags);

    /**
     * Search hashtags by keyword
     */
    Page<HashtagResponse> searchHashtags(String q, Pageable pageable);

    /**
     * Get list of trending hashtags
     */
    Page<HashtagResponse> getTrendingHashtags(Pageable pageable);

    /**
     * Get posts by hashtag
     */
    List<Post> getPostsByHashtag(String hashtagName, int limit);

    /**
     * Get list of hashtags for a post
     */
    List<HashtagResponse> getHashtagsByPost(int postId);

    /**
     * Remove hashtag from post
     */
    void removeHashtagFromPost(int postId, String hashtagName);

    /**
     * Update hashtag post count
     */
    void updateHashtagPostCount();
}