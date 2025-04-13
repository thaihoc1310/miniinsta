package com.thaihoc.miniinsta.dto.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String sender;
    private String receiver;
    private String content;
    private String imageUrl;
    private LocalDateTime timestamp;
    private boolean isRead;
}