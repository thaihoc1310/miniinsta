package com.thaihoc.miniinsta.dto.message;

import java.time.LocalDateTime;

import com.thaihoc.miniinsta.dto.user.ProfileResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {

    private int id;
    private String content;
    private ProfileResponse sender;
    private ProfileResponse recipient;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String imageUrl;
}