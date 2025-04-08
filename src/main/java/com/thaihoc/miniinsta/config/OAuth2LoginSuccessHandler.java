package com.thaihoc.miniinsta.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.auth.RestLoginDTO;
import com.thaihoc.miniinsta.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SecurityUtil securityUtil;

    @Value("${thaihoc.oauth2.redirect-uri:http://localhost:3000}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // Xử lý thông tin người dùng OAuth2
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String email = userPrincipal.getUsername(); // Email được lưu trong username

        // Tạo DTO để tạo JWT token
        RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();
        userLogin.setId(userPrincipal.getId().getMostSignificantBits() & Long.MAX_VALUE); // Chuyển UUID sang long an
                                                                                          // toàn
        userLogin.setEmail(email);
        userLogin.setName(userPrincipal.getName());

        RestLoginDTO loginDTO = new RestLoginDTO();
        loginDTO.setUser(userLogin);

        // Tạo access và refresh token
        String accessToken = securityUtil.createAccessToken(email, loginDTO);
        String refreshToken = securityUtil.createRefreshToken(email, loginDTO);

        // Chuyển hướng về frontend với token
        String redirectUrl = redirectUri +
                "?access_token=" + accessToken +
                "&refresh_token=" + refreshToken;

        log.info("OAuth2 login successful, redirecting to: {}", redirectUri);
        response.sendRedirect(redirectUrl);
    }
}