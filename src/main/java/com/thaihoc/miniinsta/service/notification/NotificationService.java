package com.thaihoc.miniinsta.service.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.notification.NotificationResponse;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.NotificationType;

public interface NotificationService {

    // Tạo thông báo mới
    void createNotification(Profile recipient, Profile sender, String content,
            NotificationType type, Integer relatedPostId, Integer relatedCommentId);

    // Lấy tất cả thông báo của người dùng hiện tại
    Page<NotificationResponse> getUserNotifications(UserPrincipal userPrincipal, Pageable pageable);

    // Lấy thông báo chưa đọc của người dùng hiện tại
    List<NotificationResponse> getUnreadNotifications(UserPrincipal userPrincipal);

    // Đánh dấu thông báo đã đọc
    void markNotificationAsRead(UserPrincipal userPrincipal, int notificationId);

    // Đánh dấu tất cả thông báo đã đọc
    void markAllNotificationsAsRead(UserPrincipal userPrincipal);

    // Đếm số thông báo chưa đọc
    long countUnreadNotifications(UserPrincipal userPrincipal);

    // Lấy thông báo theo loại
    Page<NotificationResponse> getNotificationsByType(UserPrincipal userPrincipal,
            NotificationType type, Pageable pageable);

    // Xóa thông báo
    void deleteNotification(UserPrincipal userPrincipal, int notificationId);
}