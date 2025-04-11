package com.thaihoc.miniinsta.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BaseEvent {
    private long actorId;

    private long recipientId;

    private long entityId;

    private String type;

    private String entityType;

    private String content;
}
