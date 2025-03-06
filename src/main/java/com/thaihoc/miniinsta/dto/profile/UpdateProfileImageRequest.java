package com.thaihoc.miniinsta.dto.profile;

import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateProfileImageRequest {
  @Length(min = 1)
  private String base64ImageString;
}
