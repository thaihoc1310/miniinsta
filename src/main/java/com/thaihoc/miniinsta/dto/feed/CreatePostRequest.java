package com.thaihoc.miniinsta.dto.feed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
  @NotBlank(message = "Image is required")
  private String base64ImageString;

  @Size(max = 2200, message = "Caption cannot exceed 2200 characters")
  private String caption;

}
