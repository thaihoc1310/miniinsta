package com.thaihoc.miniinsta.controller.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.FollowUserRequest;
import com.thaihoc.miniinsta.dto.profile.FollowUserResponse;
import com.thaihoc.miniinsta.dto.profile.GetFollowerResponse;
import com.thaihoc.miniinsta.dto.profile.GetFollowingResponse;
import com.thaihoc.miniinsta.dto.profile.UnFollowUserResponse;
import com.thaihoc.miniinsta.dto.profile.UnfollowUserRequest;
import com.thaihoc.miniinsta.service.profile.FollowerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/follow")
@Validated
public class FollowerController {
  @Autowired
  FollowerService followerService;

  @GetMapping("/user/followers/{id}")
  public ResponseEntity<GetFollowerResponse> getFollowers(@PathVariable int id,
      Pageable pageable) {
    log.info("userId={}, page={}, limit={}", id, pageable.getPageNumber() + 1, pageable.getPageSize());
    return ResponseEntity.ok().body(followerService.getFollowers(id, pageable));
  }

  @GetMapping("/user/followings/{id}")
  public ResponseEntity<GetFollowingResponse> getFollowing(@PathVariable int id, Pageable pageable) {
    log.info("userId={}, page={}, limit={}", id, pageable.getPageNumber() + 1, pageable.getPageSize());
    return ResponseEntity.ok().body(followerService.getFollowings(id, pageable));
  }

  @PostMapping()
  public ResponseEntity<FollowUserResponse> folowUser(
      @Valid @RequestBody FollowUserRequest request, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    followerService.folowUser(userPrincipal, request.getProfileId());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping()
  public ResponseEntity<UnFollowUserResponse> unfolowUser(
      @Valid @RequestBody UnfollowUserRequest request, Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    followerService.unfolowUser(userPrincipal, request.getProfileId());
    return ResponseEntity.ok().build();
  }
}
