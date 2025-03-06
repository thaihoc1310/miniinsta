package com.thaihoc.miniinsta.dto.profile;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FollowUserRequest {
  @Positive
  private int profileId;
}
