package com.thaihoc.miniinsta.dto.notification;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProfileFollowedEvent extends CreateNotificationRequest {
    private final String type = "NEW_FOLLOWER";
    private final String entityType = "PROFILE";
}
