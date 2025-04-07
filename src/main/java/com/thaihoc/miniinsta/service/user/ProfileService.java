package com.thaihoc.miniinsta.service.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.user.ProfileResponse;
import com.thaihoc.miniinsta.dto.user.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.user.UpdateProfileRequest;
import com.thaihoc.miniinsta.model.Profile;

public interface ProfileService {
  /**
   * Get profile of current user
   */
  Profile getCurrentUserProfile(UserPrincipal userPrincipal);

  /**
   * Get profile by ID
   */
  Profile getProfileById(int id);

  /**
   * Get profile by username
   */
  Profile getProfileByUsername(String username);

  // // Get profile by user ID
  // Profile getProfileByUserId(UUID userId);

  /**
   * Update profile information
   */
  Profile updateProfile(UserPrincipal userPrincipal, UpdateProfileRequest request);

  /**
   * Update profile image
   */
  Profile updateProfileImage(UserPrincipal userPrincipal, UpdateProfileImageRequest request);

  /**
   * Search profile by keyword
   */
  Page<ProfileResponse> searchProfiles(String q, Pageable pageable);

  /**
   * Mark profile as private/public
   */
  Profile togglePrivateProfile(UserPrincipal userPrincipal);

  /**
   * Find popular profiles to suggest following
   */
  List<ProfileResponse> getSuggestedProfiles(UserPrincipal userPrincipal, int limit);

  /**
   * Check if current user is following a profile
   */
  boolean isFollowingProfile(UserPrincipal userPrincipal, int profileId);

  /**
   * Get followers list
   */
  Page<ProfileResponse> getFollowers(int profileId, Pageable pageable);

  /**
   * Get following list
   */
  Page<ProfileResponse> getFollowing(int profileId, Pageable pageable);

  /**
   * Follow a profile
   */
  void followProfile(UserPrincipal userPrincipal, int profileId);

  /**
   * Unfollow a profile
   */
  void unfollowProfile(UserPrincipal userPrincipal, int profileId);

  /**
   * Update profile of another user (admin only)
   */
  Profile updateProfileByAdmin(UserPrincipal userPrincipal, int profileId, UpdateProfileRequest request);

  /**
   * Soft delete a profile
   */
  void softDeleteProfile(UserPrincipal userPrincipal, int profileId);

  /**
   * Restore a soft-deleted profile
   */
  void restoreProfile(UserPrincipal userPrincipal, int profileId);

  /**
   * Get profile by ID including deleted ones (admin only)
   */
  Profile getProfileByIdIncludingDeleted(UserPrincipal userPrincipal, int profileId);
}