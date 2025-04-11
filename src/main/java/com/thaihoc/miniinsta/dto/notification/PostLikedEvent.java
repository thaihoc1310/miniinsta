package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class PostLikedEvent extends CreateNotificationRequest {
    private final String type = "POST_LIKE";
    private final String entityType = "POST";
}
