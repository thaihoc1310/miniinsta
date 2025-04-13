package com.thaihoc.miniinsta.dto.chat;

import com.thaihoc.miniinsta.model.enums.MessageType;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateMessageRequest {
    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Sender ID is required")
    private Long senderId;

    @NotBlank(message = "Type is required")
    private MessageType type;
}
