package com.thaihoc.miniinsta.controller.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.notification.NotificationResponse;
import com.thaihoc.miniinsta.model.enums.NotificationType;
import com.thaihoc.miniinsta.service.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            Authentication authentication,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getUserNotifications(userPrincipal, pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userPrincipal));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userPrincipal));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByType(
            Authentication authentication,
            @PathVariable NotificationType type,
            Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getNotificationsByType(userPrincipal, type, pageable));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            Authentication authentication,
            @PathVariable int notificationId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        notificationService.markNotificationAsRead(userPrincipal, notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        notificationService.markAllNotificationsAsRead(userPrincipal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            Authentication authentication,
            @PathVariable int notificationId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        notificationService.deleteNotification(userPrincipal, notificationId);
        return ResponseEntity.ok().build();
    }
}