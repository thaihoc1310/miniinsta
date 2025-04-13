package com.thaihoc.miniinsta.dto.notification;

import lombok.Getter;

@Getter
public class CommentRepliedEvent extends BaseEvent {
    public CommentRepliedEvent(long actorId, long recipientId, long entityId, String actorName) {
        super(actorId, recipientId, entityId, "COMMENT_REPLY", "COMMENT", actorName + " replied to your comment");
    }
}
