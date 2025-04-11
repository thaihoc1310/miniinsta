package com.thaihoc.miniinsta.dto.chat;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateParticipantRequest {
    @NotBlank(message = "Participant ID is required")
    private long participantId;

    private String nickname;

    @NotBlank(message = "Last read timestamp is required")
    private Instant lastReadTimestamp;
}
