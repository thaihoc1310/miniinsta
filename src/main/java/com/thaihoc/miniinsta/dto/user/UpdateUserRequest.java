package com.thaihoc.miniinsta.dto.user;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private UUID id;

    private String name;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private String provider;

    private String providerId;

    private UpdateRoleRequest role;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRoleRequest {
        private Long id;
    }
}
