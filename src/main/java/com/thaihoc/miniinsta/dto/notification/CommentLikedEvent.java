package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class CommentLikedEvent extends CreateNotificationRequest {
    private final String type = "COMMENT_LIKE";
    private final String entityType = "COMMENT";
}
