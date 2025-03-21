package com.thaihoc.miniinsta.service.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.notification.NotificationResponse;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.NotificationType;

public interface NotificationService {

        // Create new notification
        void createNotification(Profile recipient, Profile sender, String content,
                        NotificationType type, Integer relatedPostId, Integer relatedCommentId);

        // Get all notifications of current user
        Page<NotificationResponse> getUserNotifications(UserPrincipal userPrincipal, Pageable pageable);

        // Get unread notifications of current user
        List<NotificationResponse> getUnreadNotifications(UserPrincipal userPrincipal);

        // Mark notification as read
        void markNotificationAsRead(UserPrincipal userPrincipal, int notificationId);

        // Mark all notifications as read
        void markAllNotificationsAsRead(UserPrincipal userPrincipal);

        // Count unread notifications
        long countUnreadNotifications(UserPrincipal userPrincipal);

        // Get notifications by type
        Page<NotificationResponse> getNotificationsByType(UserPrincipal userPrincipal,
                        NotificationType type, Pageable pageable);

        // Delete notification
        void deleteNotification(UserPrincipal userPrincipal, int notificationId);
}