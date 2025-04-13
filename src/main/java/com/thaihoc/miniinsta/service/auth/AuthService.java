package com.thaihoc.miniinsta.service.auth;

import org.springframework.http.ResponseEntity;

import com.thaihoc.miniinsta.dto.auth.ReqLoginDTO;
import com.thaihoc.miniinsta.dto.auth.RestLoginDTO;
import com.thaihoc.miniinsta.dto.user.UserResponse;
import com.thaihoc.miniinsta.exception.IdInvalidException;

public interface AuthService {
    ResponseEntity<RestLoginDTO> loginAndGenerateTokens(ReqLoginDTO loginDTO);

    UserResponse getUserAccount() throws IdInvalidException;

    ResponseEntity<Void> handleLogout();

    ResponseEntity<RestLoginDTO> createNewAccessToken(String email) throws IdInvalidException;
}
