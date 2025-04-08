package com.thaihoc.miniinsta.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Profile;

public interface ProfileService {
  /**
   * Get profile of current user
   * 
   * @throws IdInvalidException
   */
  Profile handleGetCurrentUserProfile() throws IdInvalidException;

  /**
   * Get profile by ID
   */
  Profile getProfileById(long id) throws IdInvalidException;

  /**
   * Get profile by username
   */
  Profile getProfileByUsername(String username) throws IdInvalidException;

  // // Get profile by user ID
  // Profile getProfileByUserId(UUID userId);

  /**
   * Update profile information
   * 
   * @throws IdInvalidException
   */
  Profile handleUpdateProfile(Profile profile) throws IdInvalidException;

  /**
   * Update profile image
   */
  // Profile updateProfileImage(UpdateProfileImageRequest request);

  /**
   * Get all profiles
   */
  ResultPaginationDTO handleGetAllProfiles(Specification<Profile> spec, Pageable pageable);

  /**
   * Mark profile as private/public
   */
  // Profile togglePrivateProfile(UserPrincipal userPrincipal);

  /**
   * Find popular profiles to suggest following
   */
  // List<ProfileResponse> handleGetSuggestedProfiles(UserPrincipal userPrincipal,
  // long limit);

  /**
   * Check if current user is following a profile
   * 
   * @throws IdInvalidException
   */
  boolean isFollowingProfile(long profileId) throws IdInvalidException;

  /**
   * Get followers list
   */
  ResultPaginationDTO handleGetFollowers(long profileId, Pageable pageable, String q);

  /**
   * Get following list
   */
  ResultPaginationDTO handleGetFollowing(long profileId, Pageable pageable, String q);

  /**
   * Follow a profile
   * 
   * @throws IdInvalidException
   */
  void followProfile(long profileId, long followerId) throws IdInvalidException;

  /**
   * Unfollow a profile
   * 
   * @throws IdInvalidException
   */
  void unfollowProfile(long profileId, long followerId) throws IdInvalidException;

  // /**
  // * Soft delete a profile
  // */
  // void softDeleteProfile(UserPrincipal userPrincipal, long profileId);

  // /**
  // * Restore a soft-deleted profile
  // */
  // void restoreProfile(UserPrincipal userPrincipal, long profileId);

  boolean existsByUsername(String username);

  boolean existsById(long id);

}