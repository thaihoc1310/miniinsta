package com.thaihoc.miniinsta.dto.feed;

import java.util.List;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {
    @Size(max = 2200, message = "Caption cannot exceed 2200 characters")
    private String caption;

    private List<String> hashtags;
}