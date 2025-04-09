package com.thaihoc.miniinsta.service.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.exception.UsernameAlreadyExistsException;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.repository.ProfileRepository;
import com.thaihoc.miniinsta.service.FileService;
import com.thaihoc.miniinsta.util.SecurityUtil;

@Service
public class ProfileServiceImpl implements ProfileService {
  private FileService fileService;
  private UserService userService;
  private ProfileRepository profileRepository;

  public ProfileServiceImpl(UserService userService, ProfileRepository profileRepository) {
    this.userService = userService;
    this.profileRepository = profileRepository;
  }

  @Override
  public Profile handleGetCurrentUserProfile() {
    String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
    User currentUser = this.userService.getUserByEmail(email);
    return currentUser != null ? currentUser.getProfile() : null;
  }

  @Override
  public Profile getProfileById(long id) throws IdInvalidException {
    return this.getProfileFromOptional(this.profileRepository.findById(id));
  }

  private Profile getProfileFromOptional(Optional<Profile> profile) throws IdInvalidException {
    if (profile.isPresent()) {
      return profile.get();
    }
    throw new IdInvalidException("Profile not found");
  }

  @Override
  public Profile getProfileByUsername(String username) throws IdInvalidException {
    return this.getProfileFromOptional(this.profileRepository.findByUsername(username));
  }

  // @Override
  // public Profile getProfileByUserId(UUID userId) {
  // return profileRepository.findByUserId(userId)
  // .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user
  // id: " + userId));
  // }

  @Override
  public Profile handleUpdateProfile(Profile profile) throws IdInvalidException {
    Profile updatedProfile = this.getProfileById(profile.getId());
    if (updatedProfile == null) {
      throw new IdInvalidException("Profile not found");
    }
    // Check if username already exists
    if (!profile.getUsername().equals(updatedProfile.getUsername()) &&
        profileRepository.findByUsername(profile.getUsername()).isPresent()) {
      throw new UsernameAlreadyExistsException("Username is already taken");
    }

    if (profile.getBio() != null && !profile.getBio().equals(updatedProfile.getBio())) {
      updatedProfile.setBio(profile.getBio());
    }
    if (profile.getDisplayName() != null && !profile.getDisplayName().equals(updatedProfile.getDisplayName())) {
      updatedProfile.setDisplayName(profile.getDisplayName());
    }
    if (profile.getGender() != null && !profile.getGender().equals(updatedProfile.getGender())) {
      updatedProfile.setGender(profile.getGender());
    }
    if (profile.getProfilePictureUrl() != null
        && !profile.getProfilePictureUrl().equals(updatedProfile.getProfilePictureUrl())) {
      String url = fileService.uploadImage(profile.getProfilePictureUrl());
      updatedProfile.setProfilePictureUrl(url);
    }
    if (profile.getUsername() != null && !profile.getUsername().equals(updatedProfile.getUsername())) {
      updatedProfile.setUsername(profile.getUsername());
    }
    if (profile.isPrivate() != updatedProfile.isPrivate()) {
      updatedProfile.setPrivate(profile.isPrivate());
    }

    return profileRepository.save(updatedProfile);
  }

  // @Override
  // public Page<ProfileResponse> searchProfiles(String q, Pageable pageable) {
  // Page<Profile> profiles = profileRepository.searchProfiles(q, pageable);
  // return profiles.map(this::convertToProfileResponse);
  // }

  @Override
  public ResultPaginationDTO handleGetAllProfiles(Specification<Profile> spec, Pageable pageable) {
    Page<Profile> pageProfile = this.profileRepository.findAll(spec, pageable);
    return createPaginationResult(pageProfile, pageable);
  }

  @Override
  public boolean existsByUsername(String username) {
    return this.profileRepository.findByUsername(username).isPresent();
  }

  @Override
  public boolean existsById(long id) {
    return this.profileRepository.findById(id).isPresent();
  }

  @Override
  public boolean isFollowingProfile(long profileId) throws IdInvalidException {
    Profile currentProfile = this.handleGetCurrentUserProfile();
    return this.profileRepository.isFollowing(profileId, currentProfile.getId());
  }

  // @Override
  // public List<ProfileResponse> getSuggestedProfiles(UserPrincipal
  // userPrincipal, int limit) {
  // Profile currentProfile = getCurrentUserProfile(userPrincipal);

  // List<Profile> popularProfiles = profileRepository.findPopularProfiles(limit);

  // // Remove profiles that are already followed and the user's own profile
  // return popularProfiles.stream()
  // .filter(p -> p.getId() != currentProfile.getId() &&
  // !isFollowingProfile(userPrincipal, p.getId()))
  // .map(this::convertToProfileResponse)
  // .limit(limit)
  // .collect(Collectors.toList());
  // }

  @Override
  public ResultPaginationDTO handleGetFollowers(long profileId, Pageable pageable, String q) {
    Page<Profile> followers = profileRepository.findFollowerProfiles(q, profileId, pageable);
    return createPaginationResult(followers, pageable);
  }

  @Override
  public ResultPaginationDTO handleGetFollowing(long profileId, Pageable pageable, String q) {
    Page<Profile> following = profileRepository.findFollowingProfiles(q, profileId, pageable);
    return createPaginationResult(following, pageable);
  }

  private ResultPaginationDTO createPaginationResult(Page<Profile> page, Pageable pageable) {
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());
    mt.setPages(page.getTotalPages());
    mt.setTotal(page.getTotalElements());
    rs.setMeta(mt);
    List<Profile> listProfile = page.getContent();
    rs.setResult(listProfile);
    return rs;
  }

  @Override
  @Transactional
  public void followProfile(long profileId, long followerId) throws IdInvalidException {
    Profile profileToFollow = this.getProfileById(profileId);
    Profile follower = this.getProfileById(followerId);

    if (!profileToFollow.getFollowers().contains(follower)) {
      profileToFollow.getFollowers().add(follower);
      profileToFollow.setFollowersCount(profileToFollow.getFollowersCount() + 1);

      follower.getFollowing().add(profileToFollow);
      follower.setFollowingCount(follower.getFollowingCount() + 1);

      this.profileRepository.save(profileToFollow);
      this.profileRepository.save(follower);
    }
  }

  @Override
  @Transactional
  public void unfollowProfile(long profileId, long followerId) throws IdInvalidException {
    Profile profileToUnfollow = this.getProfileById(profileId);
    Profile follower = this.getProfileById(followerId);

    if (profileToUnfollow.getFollowers().contains(follower)) {
      profileToUnfollow.getFollowers().remove(follower);
      profileToUnfollow.setFollowersCount(Math.max(0, profileToUnfollow.getFollowersCount() - 1));

      follower.getFollowing().remove(profileToUnfollow);
      follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));

      this.profileRepository.save(profileToUnfollow);
      this.profileRepository.save(follower);
    }
  }

  @Override
  public void saveProfile(Profile profile) {
    this.profileRepository.save(profile);
  }

  @Override
  public ResultPaginationDTO getPostLikers(long postId, Pageable pageable) {
    Page<Profile> postLikers = this.profileRepository.findPostLikers(postId, pageable);
    return createPaginationResult(postLikers, pageable);
  }

}