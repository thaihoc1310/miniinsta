package com.thaihoc.miniinsta.dto.user;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowProfileRequest {
    @Positive
    private long profileId;
    @Positive
    private long followerId;
}