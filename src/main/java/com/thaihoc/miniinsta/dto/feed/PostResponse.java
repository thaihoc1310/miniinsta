package com.thaihoc.miniinsta.dto.feed;

import com.thaihoc.miniinsta.model.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Post post;
    private boolean likedByCurrentUser;
}