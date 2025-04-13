package com.thaihoc.miniinsta.dto.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {
    @NotBlank(message = "Actor ID is required")
    private long actorId;

    @NotBlank(message = "Entity ID is required")
    private long entityId;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    @NotBlank(message = "Content is required")
    private String content;
}
