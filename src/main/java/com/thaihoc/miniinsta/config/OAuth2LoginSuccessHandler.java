package com.thaihoc.miniinsta.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.thaihoc.miniinsta.dto.auth.RestLoginDTO;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.service.user.UserService;
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

    private final UserService userService;
    private final SecurityUtil securityUtil;

    @Value("${thaihoc.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @Value("${thaihoc.oauth2.redirect-uri:#{'http://localhost:8080'}}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        log.info("OAuth2 Login successful. Starting success handler logic.");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String email = oAuth2User.getAttribute("email");
            log.info("Extracted email from OAuth2User: {}", email);

            if (email != null) {
                User user = userService.getUserByEmail(email);
                log.info("User lookup result for email {}: {}", email, user != null ? "Found" : "Not Found");

                if (user != null) {
                    // Create tokens and set cookies
                    RestLoginDTO restLoginDTO = new RestLoginDTO();
                    restLoginDTO.setUser(new RestLoginDTO.UserLogin(
                            user.getId(),
                            user.getEmail(),
                            user.getName(),
                            user.getRole()));

                    // Generate access token
                    String accessToken = securityUtil.createAccessToken(email, restLoginDTO);
                    log.info("Tokens generated successfully.");

                    // Generate refresh token
                    String refreshToken = securityUtil.createRefreshToken(email, restLoginDTO);

                    // Update user's refresh token in database
                    userService.handleUpdateUserToken(email, refreshToken);
                    log.info("User refresh token updated in DB.");

                    // Set refresh token as cookie
                    ResponseCookie cookie = ResponseCookie
                            .from("refresh_token", refreshToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(refreshTokenExpiration)
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                    log.info("Refresh token cookie added to response.");

                    // Add access token to response header
                    response.addHeader("Authorization", "Bearer " + accessToken);
                    log.info("Access token header added to response.");

                    // Redirect to front-end application
                    String redirectUrl = redirectUri != null ? redirectUri : "/";
                    log.info("Final redirect URI determined: {}", redirectUrl);
                    log.info("Attempting to send redirect...");

                    response.sendRedirect(redirectUrl);
                    log.info("Redirect sent successfully (?). Finishing handler.");
                    return;
                } else {
                    log.error("User with email {} not found after OAuth2 authentication. Redirecting to error page.",
                            email);
                    response.sendRedirect("/login?error=oauth2_user_not_found");
                    return;
                }
            } else {
                log.error("Email not found in OAuth2 user attributes. Redirecting to error page.");
                response.sendRedirect("/login?error=oauth2_email_missing");
                return;
            }
        } else {
            log.warn("Authentication object is not an OAuth2AuthenticationToken: {}. Redirecting to error page.",
                    authentication.getClass().getName());
        }

        // Fallback if something goes wrong before return
        log.error("Reached fallback redirect. Something went wrong in the handler logic.");
        response.sendRedirect("/login?error=oauth2_handler_error");
    }
}