package com.thaihoc.miniinsta.service.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.user.CreateUserRequest;
import com.thaihoc.miniinsta.dto.user.UpdateUserRequest;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.User;

public interface UserService {
    UserResponse handleCreateUser(CreateUserRequest request) throws IdInvalidException;

    User getUserById(UUID id) throws IdInvalidException;

    User getUserByEmail(String email);

    UserResponse handleGetUserByEmail(String email) throws IdInvalidException;

    UserResponse handleUpdateUser(UpdateUserRequest request) throws IdInvalidException;

    ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable);

    UserResponse handleGetUserResponseById(UUID id) throws IdInvalidException;

    void handleUpdateUserToken(String email, String token);

    void handleDeleteUserById(UUID id) throws IdInvalidException;

    User getUserByRefreshTokenAndEmail(String token, String email);

    boolean existsByEmail(String email);
}
