package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EditProfileResponse {
    private int userId;
    private String avatarUrl;
    private String displayName;
    private String bio;
}
