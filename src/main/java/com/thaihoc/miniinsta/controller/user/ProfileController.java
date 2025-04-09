package com.thaihoc.miniinsta.controller.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.user.FollowProfileRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.service.user.ProfileService;
import com.thaihoc.miniinsta.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    @ApiMessage("Get current user's profile")
    public ResponseEntity<Profile> getCurrentUserProfile() throws IdInvalidException {
        Profile profile = profileService.handleGetCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a profile by id")
    public ResponseEntity<Profile> getProfileById(@PathVariable int id) throws IdInvalidException {
        Profile profile = profileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/username/{username}")
    @ApiMessage("Get a profile by username")
    public ResponseEntity<Profile> getProfileByUsername(@PathVariable String username) throws IdInvalidException {
        Profile profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping
    @ApiMessage("Update a profile")
    public ResponseEntity<Profile> updateProfile(@Valid @RequestBody Profile profile)
            throws IdInvalidException, MethodArgumentNotValidException {
        Profile updatedProfile = profileService.handleUpdateProfile(profile);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("")
    @ApiMessage("Get all profiles")
    public ResponseEntity<ResultPaginationDTO> getAllProfiles(@Filter Specification<Profile> spec,
            Pageable pageable) {
        return ResponseEntity.ok(profileService.handleGetAllProfiles(spec, pageable));
    }

    // /**
    // * Get list of suggested profiles to follow
    // */
    // @GetMapping("/suggested")
    // public ResponseEntity<List<ProfileResponse>>
    // getSuggestedProfiles(Authentication authentication,
    // @RequestParam(defaultValue = "5") int limit) {
    // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    // return ResponseEntity.ok(profileService.getSuggestedProfiles(userPrincipal,
    // limit));
    // }

    @GetMapping("/{id}/followers")
    @ApiMessage("Get followers of a profile")
    public ResponseEntity<ResultPaginationDTO> getFollowers(@PathVariable int id, Pageable pageable,
            @RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(profileService.handleGetFollowers(id, pageable, q));
    }

    @GetMapping("/{id}/following")
    @ApiMessage("Get following of a profile")
    public ResponseEntity<ResultPaginationDTO> getFollowing(@PathVariable int id, Pageable pageable,
            @RequestParam(defaultValue = "") String q) {
        return ResponseEntity.ok(profileService.handleGetFollowing(id, pageable, q));
    }

    @PostMapping("/followers")
    @ApiMessage("Follow a profile")
    public ResponseEntity<Void> followProfile(@Valid @RequestBody FollowProfileRequest request)
            throws IdInvalidException, MethodArgumentNotValidException {
        profileService.followProfile(request.getProfileId(), request.getFollowerId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/followers")
    @ApiMessage("Unfollow a profile")
    public ResponseEntity<Void> unfollowProfile(@Valid @RequestBody FollowProfileRequest request)
            throws IdInvalidException, MethodArgumentNotValidException {
        profileService.unfollowProfile(request.getProfileId(), request.getFollowerId());
        return ResponseEntity.noContent().build();
    }
}
