package com.thaihoc.miniinsta.dto.chat;

import java.util.List;

import com.thaihoc.miniinsta.model.enums.ConversationType;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateConversationRequest {
    private String name;

    @NotBlank(message = "Profile IDs are required")
    private List<Long> profileIds;

    @NotBlank(message = "Conversation type is required")
    private ConversationType type;

}
