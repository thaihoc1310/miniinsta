package com.thaihoc.miniinsta.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateParticipantRequest {

    @NotBlank(message = "Profile ID is required")
    private long profileId;

    @NotBlank(message = "Conversation ID is required")
    private long conversationId;
}
