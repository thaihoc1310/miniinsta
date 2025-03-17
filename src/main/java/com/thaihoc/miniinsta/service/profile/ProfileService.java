package com.thaihoc.miniinsta.service.profile;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileImageRequest;
import com.thaihoc.miniinsta.dto.profile.UpdateProfileRequest;
import com.thaihoc.miniinsta.model.Profile;

public interface ProfileService {
  // Lấy profile của user hiện tại
  Profile getCurrentUserProfile(UserPrincipal userPrincipal);

  // Lấy profile theo ID
  Profile getProfileById(int id);

  // Lấy profile theo username
  Profile getProfileByUsername(String username);

  // Lấy profile theo user ID
  Profile getProfileByUserId(UUID userId);

  // Cập nhật thông tin profile
  Profile updateProfile(UserPrincipal userPrincipal, UpdateProfileRequest request);

  // Cập nhật ảnh profile
  Profile updateProfileImage(UserPrincipal userPrincipal, UpdateProfileImageRequest request);

  // Tìm kiếm profile theo từ khóa
  Page<ProfileResponse> searchProfiles(String searchTerm, Pageable pageable);

  // Đánh dấu profile là private/public
  Profile togglePrivateProfile(UserPrincipal userPrincipal);

  // Tìm các profile phổ biến để gợi ý theo dõi
  List<ProfileResponse> getSuggestedProfiles(UserPrincipal userPrincipal, int limit);

  // Kiểm tra nếu người dùng hiện tại đang theo dõi một profile
  boolean isFollowingProfile(UserPrincipal userPrincipal, int profileId);

  // Lấy danh sách followers
  Page<ProfileResponse> getFollowers(int profileId, Pageable pageable);

  // Lấy danh sách đang theo dõi
  Page<ProfileResponse> getFollowing(int profileId, Pageable pageable);

  // Theo dõi một profile
  void followProfile(UserPrincipal userPrincipal, int profileId);

  // Bỏ theo dõi một profile
  void unfollowProfile(UserPrincipal userPrincipal, int profileId);
}