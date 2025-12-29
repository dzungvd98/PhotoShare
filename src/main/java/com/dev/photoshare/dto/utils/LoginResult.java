package com.dev.photoshare.dto.utils;

import com.dev.photoshare.dto.response.LoginResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResult {
    private LoginResponse loginResponse;
    private String refreshToken;
}
