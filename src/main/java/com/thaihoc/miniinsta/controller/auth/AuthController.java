package com.thaihoc.miniinsta.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/auth")
public class AuthController {
  @GetMapping("/me")
  public ResponseEntity<UserPrincipal> getCurrentUser(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.status(401).build();
    }
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    log.info("User authenticated: {}", userPrincipal.getUsername());
    return ResponseEntity.ok(userPrincipal);
  }

  @GetMapping("/roles")
  public ResponseEntity<Map<String, Boolean>> getUserRoles(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Map<String, Boolean> roles = new HashMap<>();

    roles.put("isAdmin", userPrincipal.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    roles.put("isUser", userPrincipal.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

    return ResponseEntity.ok(roles);
  }

  @GetMapping("/check-admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> checkAdminRole() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "success");
    response.put("role", "ADMIN");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/check-user")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Map<String, String>> checkUserRole() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "success");
    response.put("role", "USER");
    return ResponseEntity.ok(response);
  }
}
