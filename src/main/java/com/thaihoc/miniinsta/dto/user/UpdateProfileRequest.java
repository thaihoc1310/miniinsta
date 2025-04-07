package com.thaihoc.miniinsta.dto.user;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
  @Size(min = 3, max = 50)
  private String username;

  @Size(max = 100)
  private String displayName;

  @Size(max = 150)
  private String bio;

  @Size(max = 100)
  private String website;

  @Size(max = 20)
  private String phoneNumber;
}
