package com.thaihoc.miniinsta.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Integer id;
    private String username;
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private String website;
    private boolean isPrivate;
    private boolean isVerified;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private boolean isFollowedByCurrentUser;
}