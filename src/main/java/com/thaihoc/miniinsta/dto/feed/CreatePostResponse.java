package com.thaihoc.miniinsta.dto.feed;

import com.thaihoc.miniinsta.model.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CreatePostResponse {
  private Post post;
}
