package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class CommentRepliedEvent extends CreateNotificationRequest {
    private final String type = "COMMENT_REPLY";
    private final String entityType = "COMMENT";
}
