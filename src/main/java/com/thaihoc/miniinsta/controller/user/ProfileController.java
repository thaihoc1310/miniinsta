package com.thaihoc.miniinsta.controller.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.OptimisticLockException;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.user.GetProfileResponse;
import com.thaihoc.miniinsta.dto.user.ProfileResponse;
import com.thaihoc.miniinsta.dto.user.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.user.UpdateProfileRequest;
import com.thaihoc.miniinsta.dto.user.UpdateProfileResponse;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.service.user.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Get current user's profile information
     */
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

    /**
     * Get profile information by ID
     */
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

    /**
     * Get profile information by username
     */
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

    /**
     * Update current user's profile information
     */
    @PutMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateProfile(Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.updateProfile(userPrincipal, request);
        return ResponseEntity.ok(UpdateProfileResponse.builder().profile(profile).build());
    }

    /**
     * Update current user's profile picture
     */
    @PutMapping("/me/image")
    public ResponseEntity<UpdateProfileResponse> updateProfileImage(Authentication authentication,
            @Valid @RequestBody UpdateProfileImageRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.updateProfileImage(userPrincipal, request);
        return ResponseEntity.ok(UpdateProfileResponse.builder().profile(profile).build());
    }

    /**
     * Search profiles by name or username
     */
    @GetMapping
    public ResponseEntity<Page<ProfileResponse>> searchProfiles(@RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(profileService.searchProfiles(q, pageable));
    }

    /**
     * Toggle private/public mode for profile
     */
    @PutMapping("/me/privacy")
    public ResponseEntity<Profile> togglePrivateProfile(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.togglePrivateProfile(userPrincipal));
    }

    /**
     * Get list of suggested profiles to follow
     */
    @GetMapping("/suggested")
    public ResponseEntity<List<ProfileResponse>> getSuggestedProfiles(Authentication authentication,
            @RequestParam(defaultValue = "5") int limit) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(profileService.getSuggestedProfiles(userPrincipal, limit));
    }

    /**
     * Get list of followers for a profile
     */
    @GetMapping("/{id}/followers")
    public ResponseEntity<Page<ProfileResponse>> getFollowers(@PathVariable int id, Pageable pageable) {
        return ResponseEntity.ok(profileService.getFollowers(id, pageable));
    }

    /**
     * Get list of profiles being followed by a profile
     */
    @GetMapping("/{id}/following")
    public ResponseEntity<Page<ProfileResponse>> getFollowing(@PathVariable int id, Pageable pageable) {
        return ResponseEntity.ok(profileService.getFollowing(id, pageable));
    }

    /**
     * Follow a user
     */
    @PostMapping("/{id}/followers")
    public ResponseEntity<Void> followProfile(Authentication authentication, @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        profileService.followProfile(userPrincipal, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Unfollow a user
     */
    @DeleteMapping("/{id}/followers")
    public ResponseEntity<Void> unfollowProfile(Authentication authentication, @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        profileService.unfollowProfile(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update profile information (Admin permission)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateProfileResponse> updateProfileByAdmin(Authentication authentication,
            @PathVariable int id, @Valid @RequestBody UpdateProfileRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.updateProfileByAdmin(userPrincipal, id, request);
        return ResponseEntity.ok(UpdateProfileResponse.builder().profile(profile).build());
    }

    /**
     * Soft delete a profile
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(Authentication authentication, @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        profileService.softDeleteProfile(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restore a soft-deleted profile (admin only)
     */
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> restoreProfile(Authentication authentication, @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        profileService.restoreProfile(userPrincipal, id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get profile by ID including deleted ones (admin only)
     */
    @GetMapping("/{id}/including-deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetProfileResponse> getProfileByIdIncludingDeleted(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Profile profile = profileService.getProfileByIdIncludingDeleted(userPrincipal, id);
        return ResponseEntity.ok(GetProfileResponse.builder()
                .profile(profile)
                .numberOfPost(profile.getPostsCount())
                .numberOfFollower(profile.getFollowersCount())
                .numberOfFollowing(profile.getFollowingCount())
                .build());
    }
}
