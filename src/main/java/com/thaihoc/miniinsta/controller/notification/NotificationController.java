package com.thaihoc.miniinsta.controller.notification;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.notification.CreateNotificationRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Notification;
import com.thaihoc.miniinsta.service.notification.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("profiles/{profileId}/notifications")
    public ResponseEntity<Notification> createNotification(
            @PathVariable long profileId,
            @Valid @RequestBody CreateNotificationRequest request) throws IdInvalidException {
        Notification createdNotification = notificationService.createNotification(profileId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @GetMapping("profiles/{profileId}/notifications")
    public ResponseEntity<ResultPaginationDTO> getAllNotifications(@PathVariable long profileId,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(notificationService.getAllNotifications(profileId, pageable));
    }

    @PatchMapping("profiles/{profileId}/notifications/all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable long profileId) {
        notificationService.markAllAsRead(profileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("profiles/{profileId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable long profileId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(profileId));
    }

    @DeleteMapping("profiles/{profileId}/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable long profileId, @PathVariable Long id)
            throws IdInvalidException {
        notificationService.deleteNotification(profileId, id);
        return ResponseEntity.noContent().build();
    }

}