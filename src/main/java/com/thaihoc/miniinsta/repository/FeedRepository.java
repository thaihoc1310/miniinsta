package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FeedRepository {
    private static final String FEED_KEY_PREFIX = "feed:";

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    public Long getFeedSize(long profileId) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        return redisTemplate.opsForList().size(feedKey);
    }

    public void addPostToFeed(long postId, long profileId) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        redisTemplate.opsForList().leftPush(feedKey, postId);
        redisTemplate.opsForList().trim(feedKey, 0, 999);
    }

    public List<Long> getFeed(long profileId, int limit, int page) {
        String feedKey = FEED_KEY_PREFIX + profileId;
        int start = (page - 1) * limit;
        int end = start + limit - 1;
        return redisTemplate.opsForList().range(feedKey, start, end);
    }

}