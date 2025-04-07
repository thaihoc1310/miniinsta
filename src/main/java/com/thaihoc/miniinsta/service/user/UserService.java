package com.thaihoc.miniinsta.service.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.User;

public interface UserService {
    UserResponse handleCreateUser(User user) throws IdInvalidException;

    User getUserById(UUID id) throws IdInvalidException;

    User getUserByUsername(String username) throws IdInvalidException;

    User getUserByEmail(String email) throws IdInvalidException;

    UserResponse handleUpdateUser(User user) throws IdInvalidException;

    ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable);

    UserResponse handleGetUserResponseById(UUID id) throws IdInvalidException;

    void handleUpdateUserToken(String email, String token) throws IdInvalidException;

    void handleDeleteUserById(UUID id, boolean permanent) throws IdInvalidException;

    void handleRestoreUserById(UUID id) throws IdInvalidException;

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
