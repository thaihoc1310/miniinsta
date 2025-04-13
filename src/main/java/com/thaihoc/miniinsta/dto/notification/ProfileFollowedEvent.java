package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class ProfileFollowedEvent extends BaseEvent {
    public ProfileFollowedEvent(long actorId, long recipientId, long entityId, String actorName) {
        super(actorId, recipientId, entityId, "NEW_FOLLOWER", "PROFILE", actorName + " started following you");
    }
}
