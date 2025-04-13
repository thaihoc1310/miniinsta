package com.thaihoc.miniinsta.dto.auth;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "Role name is required")
    private String name;
    private boolean active;
    private String description;
    private List<Long> permissionIds;
}
