package com.thaihoc.miniinsta.event;

import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thaihoc.miniinsta.config.MessageQueueConfig;
import com.thaihoc.miniinsta.model.Post;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.FeedRepository;
import com.thaihoc.miniinsta.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RabbitListener(queues = MessageQueueConfig.AFTER_CREATE_POST_QUEUE)
public class PushFeedConsumer {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FeedRepository feedRepository;

    @RabbitHandler
    public void receive(Integer postId) throws JsonMappingException, JsonProcessingException {
        log.info("[PushFeed] Received post ID '{}' for feed distribution", postId);

        // Lấy post trực tiếp từ repository thay vì qua service để tránh lỗi type
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            log.error("[PushFeed] Cannot find post with ID: {}", postId);
            return;
        }

        // Thêm post vào feed của chính người tạo post
        Profile creator = post.getCreatedBy();
        if (creator != null) {
            feedRepository.addPostToFeed(postId, creator.getId());
            log.info("[PushFeed] Added post {} to creator's feed (user ID: {})", postId, creator.getId());

            // Thêm post vào feed của tất cả người theo dõi
            Set<Profile> followers = creator.getFollowers();
            if (followers != null && !followers.isEmpty()) {
                log.info("[PushFeed] Found {} followers for user ID {}", followers.size(), creator.getId());

                for (Profile follower : followers) {
                    feedRepository.addPostToFeed(postId, follower.getId());
                    log.debug("[PushFeed] Added post {} to follower's feed (user ID: {})", postId, follower.getId());
                }

                log.info("[PushFeed] Successfully distributed post {} to {} feeds", postId, followers.size() + 1);
            } else {
                log.info("[PushFeed] No followers found for user ID {}", creator.getId());
            }
        } else {
            log.error("[PushFeed] Post {} has no creator information", postId);
        }
    }
}
