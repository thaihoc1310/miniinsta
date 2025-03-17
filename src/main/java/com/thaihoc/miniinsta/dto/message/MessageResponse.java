package com.thaihoc.miniinsta.dto.message;

import java.util.Date;

import com.thaihoc.miniinsta.dto.profile.ProfileResponse;

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
    private Date createdAt;
    private boolean isRead;
    private String imageUrl;
}