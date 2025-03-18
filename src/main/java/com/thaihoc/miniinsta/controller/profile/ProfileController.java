package com.thaihoc.miniinsta.controller.profile;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.GetProfileResponse;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileResponse;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.service.profile.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<GetProfileResponse> getCurrentUserProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        return ResponseEntity.ok(GetProfileResponse.builder()
                .profile(profile)
                .numberOfPost(profile.getPostsCount())
                .numberOfFollower(profile.getFollowersCount())
                .numberOfFollowing(profile.getFollowingCount())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProfileResponse> getProfileById(@PathVariable int id) {
        Profile profile = profileService.getProfileById(id);
        return ResponseEntity.ok(GetProfileResponse.builder()
                .profile(profile)
                .numberOfPost(profile.getPostsCount())
                .numberOfFollower(profile.getFollowersCount())
                .numberOfFollowing(profile.getFollowingCount())
                .build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<GetProfileResponse> getProfileByUsername(@PathVariable String username) {
        Profile profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(GetProfileResponse.builder()
                .profile(profile)
                .numberOfPost(profile.getPostsCount())
                .numberOfFollower(profile.getFollowersCount())
                .numberOfFollowing(profile.getFollowingCount())
                .build());
    }

    @PutMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateProfile(Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.updateProfile(userPrincipal, request);
        return ResponseEntity.ok(UpdateProfileResponse.builder().profile(profile).build());
    }

    @PutMapping("/me/image")
    public ResponseEntity<UpdateProfileResponse> updateProfileImage(Authentication authentication,
            @Valid @RequestBody UpdateProfileImageRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.updateProfileImage(userPrincipal, request);
        return ResponseEntity.ok(UpdateProfileResponse.builder().profile(profile).build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProfileResponse>> searchProfiles(@RequestParam String searchTerm,
            Pageable pageable) {
        return ResponseEntity.ok(profileService.searchProfiles(searchTerm, pageable));
    }

    @PutMapping("/me/private")
    public ResponseEntity<Profile> togglePrivateProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.togglePrivateProfile(userPrincipal));
    }

    @GetMapping("/suggested")
    public ResponseEntity<List<ProfileResponse>> getSuggestedProfiles(Authentication authentication,
            @RequestParam(defaultValue = "5") int limit) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.getSuggestedProfiles(userPrincipal, limit));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Page<ProfileResponse>> getFollowers(@PathVariable int id, Pageable pageable) {
        return ResponseEntity.ok(profileService.getFollowers(id, pageable));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<Page<ProfileResponse>> getFollowing(@PathVariable int id, Pageable pageable) {
        return ResponseEntity.ok(profileService.getFollowing(id, pageable));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followProfile(Authentication authentication, @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        profileService.followProfile(userPrincipal, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowProfile(Authentication authentication, @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        profileService.unfollowProfile(userPrincipal, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateProfileResponse> updateProfileByAdmin(Authentication authentication,
            @PathVariable int id, @Valid @RequestBody UpdateProfileRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.updateProfileByAdmin(userPrincipal, id, request);
        return ResponseEntity.ok(UpdateProfileResponse.builder().profile(profile).build());
    }
}
