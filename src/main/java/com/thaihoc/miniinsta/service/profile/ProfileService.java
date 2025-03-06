package com.thaihoc.miniinsta.service.profile;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileRequest;
import com.thaihoc.miniinsta.model.Profile;

public interface ProfileService {
  Profile getUserProfile(UserPrincipal userPrincipal);

  Profile getUserProfile(int id);

  Profile updateProfile(UserPrincipal userPrincipal, UpdateProfileRequest request);

  Profile updateProfileImage(UserPrincipal userPrincipal, UpdateProfileImageRequest request);
}