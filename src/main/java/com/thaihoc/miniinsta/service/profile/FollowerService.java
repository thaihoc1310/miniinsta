package com.thaihoc.miniinsta.service.profile;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.GetFollowerResponse;
import com.thaihoc.miniinsta.dto.profile.GetFollowingResponse;

public interface FollowerService {
  void folowUser(UserPrincipal userPrincipal, int profileId);

  void unfolowUser(UserPrincipal userPrincipal, int profileId);

  GetFollowerResponse getFollowers(int profileId, int page, int limit);

  GetFollowingResponse getFollowings(int profileId, int page, int limit);

}