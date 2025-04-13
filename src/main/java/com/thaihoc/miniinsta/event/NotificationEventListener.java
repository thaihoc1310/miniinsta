package com.thaihoc.miniinsta.event;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.thaihoc.miniinsta.dto.notification.BaseEvent;
import com.thaihoc.miniinsta.dto.notification.CommentLikedEvent;
import com.thaihoc.miniinsta.dto.notification.CommentRepliedEvent;
import com.thaihoc.miniinsta.dto.notification.CreateNotificationRequest;
import com.thaihoc.miniinsta.dto.notification.PostCommentedEvent;
import com.thaihoc.miniinsta.dto.notification.PostCreatedEvent;
import com.thaihoc.miniinsta.dto.notification.PostLikedEvent;
import com.thaihoc.miniinsta.dto.notification.ProfileFollowedEvent;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.service.notification.NotificationFanoutService;
import com.thaihoc.miniinsta.service.notification.NotificationService;
import com.thaihoc.miniinsta.service.user.ProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationEventListener {
    private final NotificationService notificationService;
    private final ProfileService profileService;
    private final NotificationFanoutService notificationFanoutService;

    public NotificationEventListener(NotificationService notificationService, ProfileService profileService,
            NotificationFanoutService notificationFanoutService) {
        this.notificationService = notificationService;
        this.profileService = profileService;
        this.notificationFanoutService = notificationFanoutService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handlePostLikedEvent(PostLikedEvent event) throws IdInvalidException {
        log.info("Received PostLikedEvent: {}", event);
        sendNotification(event.getRecipientId(), event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handlePostCommentedEvent(PostCommentedEvent event) throws IdInvalidException {
        log.info("Received PostCommentedEvent: {}", event);
        sendNotification(event.getRecipientId(), event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handleCommentRepliedEvent(CommentRepliedEvent event) throws IdInvalidException {
        log.info("Received CommentRepliedEvent: {}", event);
        sendNotification(event.getRecipientId(), event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handleCommentLikedEvent(CommentLikedEvent event) throws IdInvalidException {
        log.info("Received CommentLikedEvent: {}", event);
        sendNotification(event.getRecipientId(), event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handleProfileFollowedEvent(ProfileFollowedEvent event) throws IdInvalidException {
        log.info("Received ProfileFollowedEvent: {}", event);
        sendNotification(event.getRecipientId(), event);
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handlePostCreatedEvent(PostCreatedEvent event) throws IdInvalidException {
        log.info("Received PostCreatedEvent: {}", event);
        int pageNumber = 0;
        final int BATCH_SIZE = 1000;
        Pageable pageable;
        Page<Profile> followersPage;

        do {
            pageable = PageRequest.of(pageNumber, BATCH_SIZE);
            followersPage = profileService.getFollowersProfiles(event.getActorId(), pageable);

            if (followersPage.hasContent()) {
                List<Profile> followersBatch = followersPage.getContent();
                notificationFanoutService.processNotificationBatch(followersBatch, event);
            }

            pageNumber++;

        } while (followersPage.hasNext());

    }

    private void sendNotification(long recipientId, BaseEvent event) throws IdInvalidException {
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .actorId(event.getActorId())
                .entityId(event.getEntityId())
                .type(event.getType())
                .entityType(event.getEntityType())
                .content(event.getContent())
                .build();
        notificationService.createNotification(recipientId, request);
    }
}
