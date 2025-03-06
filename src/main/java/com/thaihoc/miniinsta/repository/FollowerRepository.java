package com.thaihoc.miniinsta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.UserFollowing;

@Repository
public interface FollowerRepository extends JpaRepository<UserFollowing, Integer> {
}
