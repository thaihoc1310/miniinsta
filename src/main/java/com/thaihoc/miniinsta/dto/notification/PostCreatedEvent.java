package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class PostCreatedEvent extends BaseEvent {
    public PostCreatedEvent(long actorId, long recipientId, long entityId, String actorName) {
        super(actorId, recipientId, entityId, "NEW_POST", "POST", actorName + " posted a new photo");
    }
}
