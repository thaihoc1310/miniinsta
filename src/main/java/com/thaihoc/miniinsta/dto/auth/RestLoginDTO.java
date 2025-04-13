package com.thaihoc.miniinsta.dto.auth;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thaihoc.miniinsta.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RestLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private UUID id;
        private String email;
        private String name;
        private Role role;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserAccount {
        UserLogin user;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private UUID id;
        private String email;
        private String name;
    }
}
