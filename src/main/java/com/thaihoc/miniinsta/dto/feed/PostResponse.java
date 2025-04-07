package com.thaihoc.miniinsta.dto.feed;

import java.time.LocalDateTime;
import java.util.List;

import com.thaihoc.miniinsta.dto.user.ProfileResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private int id;
    private String imageUrl;
    private String caption;
    private LocalDateTime createdAt;
    private String location;
    private ProfileResponse createdBy;
    private List<CommentResponse> comments;
    private int commentCount;
    private int likeCount;
    private boolean likedByCurrentUser;
    private List<String> hashtags;
}