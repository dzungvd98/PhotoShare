package com.dev.photoshare.dto.utils;

import com.dev.photoshare.dto.response.RefreshTokenResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RefreshResult {
    private String refreshToken;
    private RefreshTokenResponse refreshTokenResponse;
}
