package com.thaihoc.miniinsta.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Profile;

public interface ProfileService {

  Profile handleGetCurrentUserProfile() throws IdInvalidException;

  Profile getProfileById(long id) throws IdInvalidException;

  Profile getProfileByUsername(String username) throws IdInvalidException;

  Profile handleUpdateProfile(Profile profile) throws IdInvalidException;

  ResultPaginationDTO handleGetAllProfiles(Specification<Profile> spec, Pageable pageable);

  boolean isFollowingProfile(long profileId) throws IdInvalidException;

  ResultPaginationDTO handleGetFollowers(long profileId, Pageable pageable, String q);

  ResultPaginationDTO handleGetFollowing(long profileId, Pageable pageable, String q);

  void followProfile(long profileId, long followerId) throws IdInvalidException;

  void unfollowProfile(long profileId, long followerId) throws IdInvalidException;

  boolean existsByUsername(String username);

  boolean existsById(long id);

  void saveProfile(Profile profile);

  ResultPaginationDTO getPostLikers(long profileId, long postId, Pageable pageable);
}