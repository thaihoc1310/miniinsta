package com.thaihoc.miniinsta.event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
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
        log.info("[PushFeed] Bắt đầu phân phối bài đăng {} đến feed người dùng", postId);

        try {
            // Lấy post từ repository
            Post post = postRepository.findById(postId).orElse(null);

            if (post == null) {
                log.error("[PushFeed] Không tìm thấy bài đăng với ID: {}", postId);
                return;
            }

            // Thêm post vào feed của người tạo
            Profile creator = post.getCreatedBy();
            if (creator != null) {
                feedRepository.addPostToFeed(postId, creator.getId());
                log.info("[PushFeed] Đã thêm bài đăng {} vào feed của người tạo (ID: {})", postId, creator.getId());

                // Lấy danh sách người theo dõi và thêm post vào feed của họ
                Set<Profile> followers = creator.getFollowers();
                if (followers != null && !followers.isEmpty()) {
                    log.info("[PushFeed] Tìm thấy {} người theo dõi cho người dùng ID {}", followers.size(),
                            creator.getId());

                    // Tối ưu: thêm post vào nhiều feed cùng một lúc
                    List<Integer> followerIds = followers.stream()
                            .map(Profile::getId)
                            .collect(Collectors.toList());

                    feedRepository.addPostToMultipleFeeds(postId, followerIds);

                    log.info("[PushFeed] Phân phối thành công bài đăng {} đến {} feed", postId, followers.size() + 1);
                } else {
                    log.info("[PushFeed] Không tìm thấy người theo dõi cho người dùng ID {}", creator.getId());
                }
            } else {
                log.error("[PushFeed] Bài đăng {} không có thông tin người tạo", postId);
            }
        } catch (Exception e) {
            log.error("[PushFeed] Lỗi khi phân phối bài đăng {}: {}", postId, e.getMessage(), e);
            // Thông báo cho RabbitMQ rằng chúng ta không thể xử lý message này và
            // không muốn nó được gửi lại (nếu bị lỗi nghiêm trọng)
            throw new AmqpRejectAndDontRequeueException("Lỗi khi phân phối feed", e);
        }
    }
}
