package com.thaihoc.miniinsta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.User;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

  Optional<Profile> findByUser(User user);

  Optional<Profile> findByUsername(String username);

  // Optional<Profile> findByUserId(UUID userId);

  /**
   * Tìm kiếm profile theo username hoặc displayName
   */
  @Query("SELECT p FROM Profile p WHERE " +
      "LOWER(p.username) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
      "LOWER(p.displayName) LIKE LOWER(CONCAT('%', :q, '%'))")
  Page<Profile> searchProfiles(@Param("q") String q, Pageable pageable);

  /**
   * Lấy danh sách người đang theo dõi
   */
  @Query("SELECT p FROM Profile p JOIN p.followers f WHERE f.id = :profileId")
  Page<Profile> findFollowingProfiles(@Param("profileId") Integer profileId, Pageable pageable);

  /**
   * Lấy danh sách người theo dõi
   */
  @Query("SELECT p FROM Profile p JOIN p.following f WHERE f.id = :profileId")
  Page<Profile> findFollowerProfiles(@Param("profileId") Integer profileId, Pageable pageable);

  /**
   * Kiểm tra xem profile có đang theo dõi profile khác không
   */
  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Profile p JOIN p.followers f WHERE p.id = :profileId AND f.id = :followerId")
  boolean isFollowing(@Param("profileId") Integer profileId, @Param("followerId") Integer followerId);

  /**
   * Lấy danh sách profile phổ biến dựa trên số lượng bài đăng
   */
  @Query(value = "SELECT p.* FROM profile p " +
      "LEFT JOIN post ps ON p.id = ps.profile_id " +
      "GROUP BY p.id " +
      "ORDER BY COUNT(ps.id) DESC", nativeQuery = true)
  List<Profile> findPopularProfiles(@Param("limit") int limit);
}
