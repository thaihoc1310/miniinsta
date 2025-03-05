package com.thaihoc.miniinsta.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thaihoc.miniinsta.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // @EntityGraph(attributePaths = "authorities")
    Optional<User> findByUsername(String username);
}
