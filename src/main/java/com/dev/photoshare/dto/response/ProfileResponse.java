package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProfileResponse {
    private String displayName;
    private long posts;
    private long followers;
    private long following;
    private String bio;
    private String avatarUrl;
    private boolean isVerified;
}
