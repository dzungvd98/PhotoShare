package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshTokenResponse {
    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
}