package com.thaihoc.miniinsta.dto.notification;

import java.time.LocalDateTime;

import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.model.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private int id;
    private ProfileResponse sender;
    private String content;
    private NotificationType type;
    private Integer relatedPostId;
    private Integer relatedCommentId;
    private boolean isRead;
    private LocalDateTime createdAt;
}