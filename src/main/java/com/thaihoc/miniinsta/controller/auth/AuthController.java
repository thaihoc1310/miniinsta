package com.thaihoc.miniinsta.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  /**
   * Lấy thông tin người dùng hiện tại
   */
  @GetMapping("/me")
  public ResponseEntity<UserPrincipal> getCurrentUser(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    log.info("User authenticated: {}", userPrincipal.getUsername());
    return ResponseEntity.ok(userPrincipal);
  }

  /**
   * Lấy thông tin về quyền của người dùng hiện tại
   * 
   * @param check Nếu được chỉ định, kiểm tra quyền cụ thể (admin, user)
   */
  @GetMapping("/roles")
  public ResponseEntity<Map<String, Object>> getUserRoles(
      Authentication authentication,
      @RequestParam(required = false) String check) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    Map<String, Object> response = new HashMap<>();

    // Kiểm tra role cụ thể nếu có tham số check
    if (check != null) {
      boolean hasRole = false;
      if ("admin".equalsIgnoreCase(check)) {
        hasRole = userPrincipal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        response.put("role", "ADMIN");
      } else if ("user".equalsIgnoreCase(check)) {
        hasRole = userPrincipal.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        response.put("role", "USER");
      }
      response.put("status", hasRole ? "success" : "failure");
      return ResponseEntity.ok(response);
    }

    // Mặc định trả về tất cả role
    Map<String, Boolean> roles = new HashMap<>();
    roles.put("isAdmin", userPrincipal.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    roles.put("isUser", userPrincipal.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

    response.put("roles", roles);
    return ResponseEntity.ok(response);
  }

  /**
   * API được bảo vệ chỉ dành cho Admin - để kiểm tra quyền Admin
   */
  @GetMapping("/admin-only")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> adminOnlyEndpoint() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "You have admin access");
    return ResponseEntity.ok(response);
  }
}
