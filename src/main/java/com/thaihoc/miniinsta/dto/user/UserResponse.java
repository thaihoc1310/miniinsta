package com.thaihoc.miniinsta.dto.user;

import java.time.LocalDate;
import java.util.UUID;

import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.Role;
import com.thaihoc.miniinsta.model.enums.GenderEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private GenderEnum gender;
    private LocalDate dateOfBirth;
    private String provider;
    private String providerId;
    private String picture;
    private Role role;
    private Profile profile;
}
