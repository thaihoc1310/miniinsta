package com.thaihoc.miniinsta.service.profile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileRequest;
import com.thaihoc.miniinsta.exception.ProfileNotFoundException;
import com.thaihoc.miniinsta.exception.UserNotFoundException;
import com.thaihoc.miniinsta.exception.UsernameAlreadyExistsException;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.repository.ProfileRepository;
import com.thaihoc.miniinsta.repository.UserRepository;
import com.thaihoc.miniinsta.service.FileService;

@Service
public class ProfileServiceImpl implements ProfileService {
  @Autowired
  private FileService fileService;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Profile getCurrentUserProfile(UserPrincipal userPrincipal) {
    User user = userRepository.findById(userPrincipal.getId())
        .orElseThrow(UserNotFoundException::new);

    return profileRepository.findByUser(user)
        .orElseGet(() -> {
          Profile newProfile = Profile.builder()
              .user(user)
              .username(user.getUsername())
              .displayName(user.getName())
              .profilePictureUrl(user.getPicture())
              .isPrivate(false)
              .isVerified(false)
              .build();
          return profileRepository.save(newProfile);
        });
  }

  @Override
  public Profile getProfileById(int id) {
    return profileRepository.findById(id)
        .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + id));
  }

  @Override
  public Profile getProfileByUsername(String username) {
    return profileRepository.findByUsername(username)
        .orElseThrow(() -> new ProfileNotFoundException("Profile not found with username: " + username));
  }

  @Override
  public Profile getProfileByUserId(UUID userId) {
    return profileRepository.findByUserId(userId)
        .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user id: " + userId));
  }

  @Override
  @Transactional
  public Profile updateProfile(UserPrincipal userPrincipal, UpdateProfileRequest request) {
    Profile profile = getCurrentUserProfile(userPrincipal);

    // Kiểm tra username đã tồn tại chưa
    if (!profile.getUsername().equals(request.getUsername()) &&
        profileRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new UsernameAlreadyExistsException("Username is already taken");
    }

    profile.setBio(request.getBio());
    profile.setDisplayName(request.getDisplayName());
    profile.setUsername(request.getUsername());
    profile.setWebsite(request.getWebsite());
    profile.setPhoneNumber(request.getPhoneNumber());

    return profileRepository.save(profile);
  }

  @Override
  @Transactional
  public Profile updateProfileImage(UserPrincipal userPrincipal, UpdateProfileImageRequest request) {
    String url = fileService.uploadImage(request.getBase64ImageString());
    Profile profile = getCurrentUserProfile(userPrincipal);
    profile.setProfilePictureUrl(url);
    return profileRepository.save(profile);
  }

  @Override
  public Page<ProfileResponse> searchProfiles(String searchTerm, Pageable pageable) {
    Page<Profile> profiles = profileRepository.searchProfiles(searchTerm, pageable);
    return profiles.map(this::convertToProfileResponse);
  }

  @Override
  @Transactional
  public Profile togglePrivateProfile(UserPrincipal userPrincipal) {
    Profile profile = getCurrentUserProfile(userPrincipal);
    profile.setPrivate(!profile.isPrivate());
    return profileRepository.save(profile);
  }

  @Override
  public List<ProfileResponse> getSuggestedProfiles(UserPrincipal userPrincipal, int limit) {
    Profile currentProfile = getCurrentUserProfile(userPrincipal);

    List<Profile> popularProfiles = profileRepository.findPopularProfiles(limit);

    // Loại bỏ những profile đã follow và chính profile người dùng
    return popularProfiles.stream()
        .filter(p -> !Integer.valueOf(p.getId()).equals(currentProfile.getId()) &&
            !isFollowingProfile(userPrincipal, p.getId()))
        .map(this::convertToProfileResponse)
        .limit(limit)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isFollowingProfile(UserPrincipal userPrincipal, int profileId) {
    Profile currentProfile = getCurrentUserProfile(userPrincipal);
    return profileRepository.isFollowing(profileId, currentProfile.getId());
  }

  @Override
  public Page<ProfileResponse> getFollowers(int profileId, Pageable pageable) {
    Page<Profile> followers = profileRepository.findFollowerProfiles(profileId, pageable);
    return followers.map(this::convertToProfileResponse);
  }

  @Override
  public Page<ProfileResponse> getFollowing(int profileId, Pageable pageable) {
    Page<Profile> following = profileRepository.findFollowingProfiles(profileId, pageable);
    return following.map(this::convertToProfileResponse);
  }

  @Override
  @Transactional
  public void followProfile(UserPrincipal userPrincipal, int profileId) {
    Profile currentProfile = getCurrentUserProfile(userPrincipal);
    Profile toFollow = getProfileById(profileId);

    if (!currentProfile.getFollowing().contains(toFollow)) {
      currentProfile.getFollowing().add(toFollow);
      currentProfile.setFollowingCount(currentProfile.getFollowingCount() + 1);

      toFollow.getFollowers().add(currentProfile);
      toFollow.setFollowersCount(toFollow.getFollowersCount() + 1);

      profileRepository.save(currentProfile);
      profileRepository.save(toFollow);
    }
  }

  @Override
  @Transactional
  public void unfollowProfile(UserPrincipal userPrincipal, int profileId) {
    Profile currentProfile = getCurrentUserProfile(userPrincipal);
    Profile toUnfollow = getProfileById(profileId);

    if (currentProfile.getFollowing().contains(toUnfollow)) {
      currentProfile.getFollowing().remove(toUnfollow);
      currentProfile.setFollowingCount(Math.max(0, currentProfile.getFollowingCount() - 1));

      toUnfollow.getFollowers().remove(currentProfile);
      toUnfollow.setFollowersCount(Math.max(0, toUnfollow.getFollowersCount() - 1));

      profileRepository.save(currentProfile);
      profileRepository.save(toUnfollow);
    }
  }

  // Helper method để chuyển đổi Profile thành ProfileResponse
  private ProfileResponse convertToProfileResponse(Profile profile) {
    return ProfileResponse.builder()
        .id(profile.getId())
        .username(profile.getUsername())
        .displayName(profile.getDisplayName())
        .bio(profile.getBio())
        .profilePictureUrl(profile.getProfilePictureUrl())
        .website(profile.getWebsite())
        .isPrivate(profile.isPrivate())
        .isVerified(profile.isVerified())
        .followersCount(profile.getFollowersCount())
        .followingCount(profile.getFollowingCount())
        .postsCount(profile.getPostsCount())
        .build();
  }
}