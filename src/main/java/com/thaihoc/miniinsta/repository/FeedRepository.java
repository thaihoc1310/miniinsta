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
        // Giữ feed trong khoảng 1000 bài đăng
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
        // Giữ explore trong khoảng 5000 bài đăng
        if (redisTemplate.opsForZSet().size(EXPLORE_KEY) > 5000) {
            redisTemplate.opsForZSet().removeRange(EXPLORE_KEY, 0, 99); // Xóa 100 bài cũ nhất
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
        // Xóa khỏi tất cả các feed khi post bị xóa
        // Đây là một cách đơn giản, trong thực tế có thể cần phức tạp hơn
        // Xóa khỏi explore
        redisTemplate.opsForZSet().remove(EXPLORE_KEY, postId);

        // TODO: Hiện thực việc xóa post khỏi feed của từng user và hashtag feeds
        // Điều này đòi hỏi một công cụ tìm kiếm ngược (reverse lookup)
        // hoặc lưu trữ thêm thông tin về vị trí của mỗi post
    }
}