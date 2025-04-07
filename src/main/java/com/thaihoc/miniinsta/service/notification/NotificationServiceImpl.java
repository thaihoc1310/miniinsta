package com.thaihoc.miniinsta.service.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.notification.NotificationResponse;
import com.thaihoc.miniinsta.dto.user.ProfileResponse;
import com.thaihoc.miniinsta.exception.NotificationNotFoundException;
import com.thaihoc.miniinsta.model.Notification;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.NotificationType;
import com.thaihoc.miniinsta.repository.NotificationRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProfileService profileService;

    @Override
    @Transactional
    public void createNotification(Profile recipient, Profile sender, String content,
            NotificationType type, Integer relatedPostId, Integer relatedCommentId) {
        // Don't send notifications to yourself
        if (recipient.getId() == sender.getId()) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedPostId(relatedPostId);
        notification.setRelatedCommentId(relatedCommentId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(UserPrincipal userPrincipal, Pageable pageable) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        Page<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(profile,
                pageable);
        return notifications.map(this::convertToNotificationResponse);
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(UserPrincipal userPrincipal) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        List<Notification> unreadNotifications = notificationRepository
                .findTop10ByRecipientAndIsReadFalseOrderByCreatedAtDesc(profile);
        return unreadNotifications.stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(UserPrincipal userPrincipal, int notificationId) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        // Check if notification belongs to current user
        if (notification.getRecipient().getId() != profile.getId()) {
            throw new RuntimeException("You don't have permission to mark this notification as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(UserPrincipal userPrincipal) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        notificationRepository.markAllAsRead(profile.getId());
    }

    @Override
    public long countUnreadNotifications(UserPrincipal userPrincipal) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        return notificationRepository.countUnreadNotifications(profile.getId());
    }

    @Override
    public Page<NotificationResponse> getNotificationsByType(UserPrincipal userPrincipal,
            NotificationType type, Pageable pageable) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        Page<Notification> notifications = notificationRepository.findByRecipientAndType(profile, type, pageable);
        return notifications.map(this::convertToNotificationResponse);
    }

    @Override
    @Transactional
    public void deleteNotification(UserPrincipal userPrincipal, int notificationId) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        // Only allow deleting your own notifications
        if (notification.getRecipient().getId() != profile.getId()) {
            throw new RuntimeException("You don't have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    // Helper method to convert Notification to NotificationResponse
    private NotificationResponse convertToNotificationResponse(Notification notification) {
        ProfileResponse senderProfile = ProfileResponse.builder()
                .id(notification.getSender().getId())
                .username(notification.getSender().getUsername())
                .displayName(notification.getSender().getDisplayName())
                .profilePictureUrl(notification.getSender().getProfilePictureUrl())
                .isVerified(notification.getSender().isVerified())
                .build();

        return NotificationResponse.builder()
                .id(notification.getId())
                .sender(senderProfile)
                .content(notification.getContent())
                .type(notification.getType())
                .relatedPostId(notification.getRelatedPostId())
                .relatedCommentId(notification.getRelatedCommentId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}