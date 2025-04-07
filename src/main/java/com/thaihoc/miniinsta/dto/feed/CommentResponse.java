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
public class CommentResponse {
    private int id;
    private String comment;
    private LocalDateTime createdAt;
    private ProfileResponse createdBy;
    private int likeCount;
    private boolean likedByCurrentUser;
    private Integer parentCommentId;
    private List<CommentResponse> replies;
}