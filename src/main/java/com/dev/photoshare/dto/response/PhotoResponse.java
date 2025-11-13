package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoResponse {
    private long photoId;
    private String description;
    private String photoUrl;
}
