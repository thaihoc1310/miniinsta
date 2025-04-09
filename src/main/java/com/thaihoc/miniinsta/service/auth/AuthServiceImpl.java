package com.thaihoc.miniinsta.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.auth.ReqLoginDTO;
import com.thaihoc.miniinsta.dto.auth.RestLoginDTO;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.service.user.UserService;
import com.thaihoc.miniinsta.util.SecurityUtil;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${thaihoc.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserService userService;
    private SecurityUtil securityUtil;

    public AuthServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService,
            SecurityUtil securityUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
    }

    @Override
    public ResponseEntity<RestLoginDTO> loginAndGenerateTokens(ReqLoginDTO loginDTO) {
        String emailLogin = loginDTO.getUsername();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                emailLogin, loginDTO.getPassword());
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return this.createTokenAndCookie(emailLogin);
    }

    private ResponseEntity<RestLoginDTO> createTokenAndCookie(String email) {
        User currentUser = this.userService.getUserByEmail(email);
        RestLoginDTO res = new RestLoginDTO();
        if (currentUser != null)
            res.setUser(new RestLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getName(),
                    currentUser.getRole()));

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(email, res);
        this.userService.handleUpdateUserToken(email, refresh_token);

        // set cookie
        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res);
    }

    @Override
    public UserResponse getUserAccount() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        return this.userService.handleGetUserByEmail(email);
    }

    @Override
    public ResponseEntity<Void> handleLogout() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.userService.handleUpdateUserToken(email, null);
        ResponseCookie cookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Override
    public ResponseEntity<RestLoginDTO> createNewAccessToken(String refreshToken) throws IdInvalidException {
        if (refreshToken.equals("nullval")) {
            throw new IdInvalidException("Refresh token is invalid");
        }
        Jwt decondedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decondedToken.getSubject();
        // check user by token and email
        User user = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (user == null) {
            throw new IdInvalidException("Refresh token is invalid");
        }
        return this.createTokenAndCookie(email);
    }

}
