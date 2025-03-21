package com.thaihoc.miniinsta.repository;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FeedRepository {
    private static final String FEED_KEY_PREFIX = "feed:";
    private static final String EXPLORE_KEY = "explore:popular";
    private static final String HASHTAG_FEED_PREFIX = "hashtag:";

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    public Long getFeedSize(int profileId) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        return redisTemplate.opsForList().size(feedKey);
    }

    public void addPostToFeed(int postId, int profileId) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        redisTemplate.opsForList().leftPush(feedKey, postId);
        // Keep feed limited to around 1000 posts
        redisTemplate.opsForList().trim(feedKey, 0, 999);
    }

    public void addPostToMultipleFeeds(int postId, List<Integer> profileIds) {
        for (Integer profileId : profileIds) {
            addPostToFeed(postId, profileId);
        }
    }

    public List<Integer> getFeed(int profileId, int limit, int page) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        int start = (page - 1) * limit;
        int end = start + limit - 1;
        return redisTemplate.opsForList().range(feedKey, start, end);
    }

    public void clearUserFeed(int profileId) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        redisTemplate.delete(feedKey);
    }

    public void addPostToExplore(int postId) {
        redisTemplate.opsForZSet().add(EXPLORE_KEY, postId, System.currentTimeMillis());
        // Keep explore limited to around 5000 posts
        if (redisTemplate.opsForZSet().size(EXPLORE_KEY) > 5000) {
            redisTemplate.opsForZSet().removeRange(EXPLORE_KEY, 0, 99); // Remove 100 oldest posts
        }
    }

    public List<Integer> getExplore(int limit, int page) {
        int start = (page - 1) * limit;
        int end = start + limit - 1;
        return new ArrayList<>(redisTemplate.opsForZSet().reverseRange(EXPLORE_KEY, start, end));
    }

    public void addPostToHashtagFeed(int postId, String hashtag) {
        String hashtagKey = HASHTAG_FEED_PREFIX + hashtag;
        redisTemplate.opsForZSet().add(hashtagKey, postId, System.currentTimeMillis());
    }

    public List<Integer> getHashtagFeed(String hashtag, int limit, int page) {
        String hashtagKey = HASHTAG_FEED_PREFIX + hashtag;
        int start = (page - 1) * limit;
        int end = start + limit - 1;
        return new ArrayList<>(redisTemplate.opsForZSet().reverseRange(hashtagKey, start, end));
    }

    public void removePostFromFeeds(int postId) {
        // Remove from all feeds when a post is deleted
        // This is a simple approach, in practice it may need to be more complex
        // Remove from explore
        redisTemplate.opsForZSet().remove(EXPLORE_KEY, postId);

        // TODO: Implement removal of posts from each user's feed and hashtag feeds
        // This requires a reverse lookup mechanism
        // or storing additional information about the position of each post
    }
}