package com.thaihoc.miniinsta.service.profile;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.GetFollowerResponse;
import com.thaihoc.miniinsta.dto.profile.GetFollowingResponse;
import com.thaihoc.miniinsta.exception.InvalidInputException;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.UserFollowing;
import com.thaihoc.miniinsta.repository.FollowerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FollowerServiceImpl implements FollowerService {
  @Autowired
  private ProfileService profileService;
  @Autowired
  private FollowerRepository followerRepository;

  @Override
  public void folowUser(UserPrincipal userPrincipal, int profileId) {
    Profile profile = profileService.getUserProfile(userPrincipal);
    if (profile.getId() == profileId) {
      throw new InvalidInputException();
    }
    UserFollowing existedUserFollowing = followerRepository.findByFollowerUserIdAndFollowingUserId(profile.getId(),
        profileId);
    if (Objects.nonNull(existedUserFollowing)) {
      return;
    }

    UserFollowing userFollowing = new UserFollowing();
    userFollowing.setFollowerUserId(profile.getId());
    userFollowing.setFollowingUserId(profileId);
    userFollowing.setCreatedAt(new Date());
    followerRepository.save(userFollowing);
  }

  @Override
  public void unfolowUser(UserPrincipal userPrincipal, int profileId) {
    Profile profile = profileService.getUserProfile(userPrincipal);
    UserFollowing existedUserFollowing = followerRepository.findByFollowerUserIdAndFollowingUserId(profile.getId(),
        profileId);
    if (Objects.isNull(existedUserFollowing)) {
      return;
    }
    followerRepository.delete(existedUserFollowing);
  }

  @Override
  public GetFollowerResponse getFollowers(int profileId, Pageable pageable) {
    profileService.getUserProfile(profileId);
    // int totalFollower = followerRepository.countByFollowingUserId(profileId);
    // log.info("totalFollower={}", totalFollower);
    // int totalPage = (int) Math.ceil((double) totalFollower / limit);
    // int offset = (page - 1) * limit;
    // List<Profile> followerProfiles =
    // followerRepository.findByFollowingUserId(profileId, limit, offset).stream()
    // .map(userFollowing ->
    // profileService.getUserProfile(userFollowing.getFollowerUserId())).toList();

    Page<UserFollowing> userFollowingPage = followerRepository.findByFollowingUserId(profileId, pageable);
    List<Profile> followerProfiles = userFollowingPage.stream()
        .map(userFollowing -> profileService.getUserProfile(userFollowing.getFollowerUserId())).toList();

    return GetFollowerResponse.builder()
        .totalPage(userFollowingPage.getTotalPages())
        .followers(followerProfiles)
        .build();
  }

  @Override
  public GetFollowingResponse getFollowings(int profileId, Pageable pageable) {
    profileService.getUserProfile(profileId);
    // int totalFollowing = followerRepository.countByFollowerUserId(profileId);
    // log.info("totalFollowing={}", totalFollowing);
    // int totalPage = (int) Math.ceil((double) totalFollowing / limit);
    // int offset = (page - 1) * limit;
    // List<Profile> followingProfiles =
    // followerRepository.findByFollowerUserId(profileId, limit, offset).stream()
    // .map(userFollowing ->
    // profileService.getUserProfile(userFollowing.getFollowingUserId())).toList();
    Page<UserFollowing> userFollowingPage = followerRepository.findByFollowerUserId(profileId, pageable);
    List<Profile> followingProfiles = userFollowingPage.stream()
        .map(userFollowing -> profileService.getUserProfile(userFollowing.getFollowingUserId())).toList();
    return GetFollowingResponse.builder()
        .totalPage(userFollowingPage.getTotalPages())
        .followings(followingProfiles)
        .build();
  }

}