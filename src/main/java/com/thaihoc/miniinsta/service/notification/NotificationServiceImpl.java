package com.thaihoc.miniinsta.service.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.notification.CreateNotificationRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Notification;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.EntityType;
import com.thaihoc.miniinsta.model.enums.NotificationType;
import com.thaihoc.miniinsta.repository.NotificationRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProfileService profileService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ProfileService profileService,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.profileService = profileService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Notification createNotification(long profileId, CreateNotificationRequest request)
            throws IdInvalidException {
        Profile actor = profileService.getProfileById(request.getActorId());
        Profile recipient = profileService.getProfileById(profileId);
        Notification notification = Notification.builder()
                .actor(actor)
                .recipient(recipient)
                .content(request.getContent())
                .type(NotificationType.valueOf(request.getType()))
                .entityId(request.getEntityId())
                .entityType(EntityType.valueOf(request.getEntityType()))
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        int unreadCount = getUnreadCount(recipient.getId());
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/unread-notifications",
                unreadCount);

        return savedNotification;
    }

    private Notification getNotificationById(long id) throws IdInvalidException {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Notification not found"));
    }

    @Override
    public ResultPaginationDTO getAllNotifications(long profileId,
            Pageable pageable) throws IdInvalidException {
        Profile recipient = profileService.getProfileById(profileId);
        Page<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient,
                pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(notifications.getTotalPages());
        mt.setTotal(notifications.getTotalElements());
        rs.setMeta(mt);
        List<Notification> listNotification = notifications.getContent();
        rs.setResult(listNotification);
        return rs;
    }

    @Override
    public void deleteNotification(long profileId, Long id) throws IdInvalidException {
        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);
    }

    @Override
    public void markAllAsRead(long profileId) {
        notificationRepository.markAllAsRead(profileId);
    }

    @Override
    public int getUnreadCount(long profileId) {
        return notificationRepository.countUnreadNotifications(profileId);
    }
}