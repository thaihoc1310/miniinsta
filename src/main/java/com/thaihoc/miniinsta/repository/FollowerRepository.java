package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.UserFollowing;

@Repository
public interface FollowerRepository extends JpaRepository<UserFollowing, Integer> {

        // @Query(value = "select * from user_following where follower_user_id =
        // :followerUserId LIMIT :limit OFFSET :offset", nativeQuery = true)
        // List<UserFollowing> findByFollowerUserId(@Param(value = "followerUserId") int
        // followerUserId,
        // @Param(value = "limit") int limit, @Param(value = "offset") int offset);

        Page<UserFollowing> findByFollowerUserId(long followerUserId, Pageable pageable);

        int countByFollowerUserId(long followerUserId);

        List<UserFollowing> findByFollowerUserId(long followerUserId);

        List<UserFollowing> findByFollowingUserId(long followingUserId);

        // @Query(value = "select * from user_following where following_user_id =
        // :followingUserId LIMIT :limit OFFSET :offset", nativeQuery = true)
        // List<UserFollowing> findByFollowingUserId(@Param(value = "followingUserId")
        // int followingUserId,
        // @Param(value = "limit") int limit,
        // @Param(value = "offset") int offset);

        Page<UserFollowing> findByFollowingUserId(long followingUserId, Pageable pageable);

        // int countByFollowingUserId(int followingUserId);

        UserFollowing findByFollowerUserIdAndFollowingUserId(long followerUserId, long followingUserId);

        void deleteByFollowerUserIdAndFollowingUserId(long followerUserId, long followingUserId);
}
