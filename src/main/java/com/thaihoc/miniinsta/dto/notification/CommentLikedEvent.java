package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class CommentLikedEvent extends BaseEvent {
    public CommentLikedEvent(long actorId, long recipientId, long entityId, String actorName) {
        super(actorId, recipientId, entityId, "COMMENT_LIKE", "COMMENT", actorName + " liked your comment");
    }
}
