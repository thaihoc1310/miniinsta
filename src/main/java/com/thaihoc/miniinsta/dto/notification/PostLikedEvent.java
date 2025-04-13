package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class PostLikedEvent extends BaseEvent {
    public PostLikedEvent(long actorId, long recipientId, long entityId, String actorName) {
        super(actorId, recipientId, entityId, "POST_LIKE", "POST", actorName + " liked your photo");
    }
}
