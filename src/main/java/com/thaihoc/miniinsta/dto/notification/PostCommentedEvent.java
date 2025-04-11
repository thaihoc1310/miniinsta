package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class PostCommentedEvent extends CreateNotificationRequest {
    private final String type = "POST_COMMENT";
    private final String entityType = "POST";
}
