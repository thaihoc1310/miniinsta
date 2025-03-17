package com.thaihoc.miniinsta.dto.feed;

import java.util.Date;
import java.util.List;

import com.thaihoc.miniinsta.dto.profile.ProfileResponse;

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
    private Date createdAt;
    private ProfileResponse createdBy;
    private int likeCount;
    private boolean likedByCurrentUser;
    private Integer parentCommentId;
    private List<CommentResponse> replies;
}