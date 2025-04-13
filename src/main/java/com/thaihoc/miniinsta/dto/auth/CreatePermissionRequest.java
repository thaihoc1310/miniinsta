package com.thaihoc.miniinsta.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePermissionRequest {
    @NotBlank(message = "Permission name is required")
    private String name;

    @NotBlank(message = "API path is required")
    private String apiPath;

    @NotBlank(message = "Method is required")
    private String method;

    @NotBlank(message = "Module is required")
    private String module;
}
