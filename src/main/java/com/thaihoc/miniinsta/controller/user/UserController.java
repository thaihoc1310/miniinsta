package com.thaihoc.miniinsta.controller.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.user.CreateUserRequest;
import com.thaihoc.miniinsta.dto.user.UpdateUserRequest;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.service.user.UserService;
import com.thaihoc.miniinsta.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ApiMessage("Create a user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
            throws IdInvalidException, MethodArgumentNotValidException, AlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(request));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a user by id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) throws IdInvalidException {
        return ResponseEntity.ok(this.userService.handleGetUserResponseById(id));
    }

    @GetMapping("")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.handleGetAllUsers(spec, pageable));
    }

    @PatchMapping("")
    @ApiMessage("Update a user by id")
    public ResponseEntity<UserResponse> updateUserById(@RequestBody UpdateUserRequest request)
            throws IdInvalidException {
        return ResponseEntity.ok(this.userService.handleUpdateUser(request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a user by id")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) throws IdInvalidException {
        this.userService.handleDeleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
