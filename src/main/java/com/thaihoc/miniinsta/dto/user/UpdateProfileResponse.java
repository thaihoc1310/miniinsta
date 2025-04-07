package com.thaihoc.miniinsta.dto.user;

import com.thaihoc.miniinsta.model.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateProfileResponse {
  private Profile profile;
}
