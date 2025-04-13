package com.thaihoc.miniinsta.dto.feed;

import com.thaihoc.miniinsta.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Comment comment;
    private boolean likedByCurrentUser;
}