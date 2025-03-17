package com.thaihoc.miniinsta.controller.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.GetProfileResponse;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileImageResponse;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileResponse;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.service.profile.ProfileService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "api/v1/profiles")
public class ProfileController {
  @Autowired
  ProfileService profileService;

  @PostMapping("/profile-image")
  public ResponseEntity<UpdateProfileImageResponse> updateProfileImage(
      @Valid @RequestBody UpdateProfileImageRequest request, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Profile profile = profileService.updateProfileImage(userPrincipal, request);
    return ResponseEntity.ok().body(UpdateProfileImageResponse.builder().url(profile.getProfilePicture()).build());
  }

  @PostMapping()
  public ResponseEntity<UpdateProfileResponse> updateProfile(
      @Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Profile profile = profileService.updateProfile(userPrincipal, request);
    return ResponseEntity.ok().body(UpdateProfileResponse.builder().profile(profile).build());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetProfileResponse> getProfile(@PathVariable int id) {
    Profile profile = profileService.getUserProfile(id);
    return ResponseEntity.ok().body(GetProfileResponse.builder().profile(profile).build());
  }

  @GetMapping("/me")
  public ResponseEntity<GetProfileResponse> getProfile(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Profile profile = profileService.getUserProfile(userPrincipal);
    return ResponseEntity.ok().body(GetProfileResponse.builder().profile(profile).build());
  }
}
