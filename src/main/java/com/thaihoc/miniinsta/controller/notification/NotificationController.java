package com.thaihoc.miniinsta.controller.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.notification.NotificationResponse;
import com.thaihoc.miniinsta.model.enums.NotificationType;
import com.thaihoc.miniinsta.service.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lấy tất cả thông báo của người dùng hiện tại
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getUserNotifications(userPrincipal, pageable));
    }

    /**
     * Lấy danh sách thông báo chưa đọc
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userPrincipal));
    }

    /**
     * Đếm số thông báo chưa đọc
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userPrincipal));
    }

    /**
     * Lấy thông báo theo loại
     */
    @GetMapping("/types/{type}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByType(
            Authentication authentication,
            @PathVariable NotificationType type,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getNotificationsByType(userPrincipal, type, pageable));
    }

    /**
     * Đánh dấu thông báo đã đọc
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        notificationService.markNotificationAsRead(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        notificationService.markAllNotificationsAsRead(userPrincipal);
        return ResponseEntity.noContent().build();
    }

    /**
     * Xóa một thông báo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        notificationService.deleteNotification(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }
}