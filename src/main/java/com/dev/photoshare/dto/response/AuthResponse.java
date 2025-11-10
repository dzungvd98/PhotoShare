package com.dev.photoshare.dto.response;

import lombok.*;


@Getter
@Setter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}
