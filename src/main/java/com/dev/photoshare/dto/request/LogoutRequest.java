package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LogoutRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
