package com.thaihoc.miniinsta.dto.hashtag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateHashtagRequest {

    @NotBlank(message = "Hashtag name cannot be empty")
    @Pattern(regexp = "^[\\w]+$", message = "Hashtag can only contain alphanumeric characters and underscores")
    private String name;
}