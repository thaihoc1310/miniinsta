package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class PostCreatedEvent extends CreateNotificationRequest {
    private final String type = "NEW_POST";
    private final String entityType = "POST";
}
