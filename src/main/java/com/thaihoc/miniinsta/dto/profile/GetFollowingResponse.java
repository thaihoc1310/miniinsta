package com.thaihoc.miniinsta.dto.profile;

import java.util.List;

import com.thaihoc.miniinsta.model.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GetFollowingResponse {
  private List<Profile> followings;
  private int totalPage;
}
