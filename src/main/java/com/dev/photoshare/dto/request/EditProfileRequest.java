package com.dev.photoshare.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileRequest {
    private String avatarUrl;
    private String displayName;
    private String bio;
}
