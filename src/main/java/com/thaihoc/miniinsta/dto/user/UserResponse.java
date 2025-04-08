package com.thaihoc.miniinsta.dto.user;

import java.time.LocalDate;
import java.util.UUID;

import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.Role;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String provider;
    private String providerId;
    private String picture;
    private Role role;
    private Profile profile;
}
