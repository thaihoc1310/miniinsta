package com.thaihoc.miniinsta.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateConversationRequest {
    private String name;
}
