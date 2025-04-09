package com.thaihoc.miniinsta.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.auth.ReqLoginDTO;
import com.thaihoc.miniinsta.dto.auth.RestLoginDTO;
import com.thaihoc.miniinsta.dto.user.CreateUserRequest;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.service.auth.AuthService;
import com.thaihoc.miniinsta.service.user.UserService;
import com.thaihoc.miniinsta.util.annotation.ApiMessage;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
  private AuthService authService;
  private UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  @PostMapping("/login")
  @ApiMessage("Login successfully")
  public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO)
      throws MethodArgumentNotValidException {
    return this.authService.loginAndGenerateTokens(loginDTO);
  }

  @GetMapping("/account")
  @ApiMessage("Fetch account")
  public ResponseEntity<UserResponse> getAccount() throws IdInvalidException {
    return ResponseEntity.ok(this.authService.getUserAccount());
  }

  @PostMapping("/logout")
  @ApiMessage("Logout successfully")
  public ResponseEntity<Void> logout() {
    return this.authService.handleLogout();
  }

  @PostMapping("/register")
  @ApiMessage("Register a new account")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest registerUser)
      throws MethodArgumentNotValidException, IdInvalidException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.userService.handleCreateUser(registerUser));
  }

  @GetMapping("/refresh")
  @ApiMessage("Get user by refresh token")
  public ResponseEntity<RestLoginDTO> getRefreshToken(
      @CookieValue(name = "refresh_token", defaultValue = "nullval") String refreshToken)
      throws IdInvalidException {
    return this.authService.createNewAccessToken(refreshToken);
  }
}
