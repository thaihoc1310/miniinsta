package com.thaihoc.miniinsta.service.notification;

import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.notification.CreateNotificationRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Notification;

public interface NotificationService {
        Notification createNotification(long profileId, CreateNotificationRequest request) throws IdInvalidException;

        ResultPaginationDTO getAllNotifications(long profileId, Pageable pageable) throws IdInvalidException;

        void deleteNotification(long profileId, Long id) throws IdInvalidException;

        void markAllAsRead(long profileId);

        int getUnreadCount(long profileId);
}