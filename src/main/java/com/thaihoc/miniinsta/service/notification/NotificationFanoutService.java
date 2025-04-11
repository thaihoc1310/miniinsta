package com.thaihoc.miniinsta.service.notification;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.notification.CreateNotificationRequest;
import com.thaihoc.miniinsta.dto.notification.PostCreatedEvent;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Profile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationFanoutService {
    private NotificationService notificationService;

    public NotificationFanoutService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("notificationFanoutExecutor")
    public void processNotificationBatch(List<Profile> recipients, PostCreatedEvent event) {
        log.info("Processing notification batch for {} recipients. Event Type: {}, Actor: {}, PostID: {}",
                recipients.size(), event.getType(), event.getActorId(), event.getEntityId());

        for (Profile recipient : recipients) {
            try {
                CreateNotificationRequest request = CreateNotificationRequest.builder()
                        .actorId(event.getActorId())
                        .entityId(event.getEntityId())
                        .type(event.getType())
                        .entityType(event.getEntityType())
                        .content(event.getContent())
                        .build();
                notificationService.createNotification(recipient.getId(), request);
            } catch (IdInvalidException e) {
                log.warn("Skipping notification for recipientId {}: {}", recipient.getId(), e.getMessage());
            }
        }
        log.info("Finished processing batch for event type {}.", event.getType());
    }
}