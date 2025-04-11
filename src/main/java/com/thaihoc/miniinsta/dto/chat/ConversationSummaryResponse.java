package com.thaihoc.miniinsta.dto.chat;

import java.time.Instant;

import com.thaihoc.miniinsta.model.enums.ConversationType;

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
public class ConversationSummaryResponse {
    private long conversationId;
    private String displayName;
    private String profileImageUrl;
    private ConversationType type;
    private String lastSenderDisplayName;
    private String lastMessageContent;
    private Instant lastMessageTimestamp;
}
