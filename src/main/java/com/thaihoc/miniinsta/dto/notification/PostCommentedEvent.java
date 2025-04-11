package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class PostCommentedEvent extends BaseEvent {
    public PostCommentedEvent(long actorId, long recipientId, long entityId, String actorName) {
        super(actorId, recipientId, entityId, "POST_COMMENT", "POST", actorName + " commented on your photo");
    }
}
