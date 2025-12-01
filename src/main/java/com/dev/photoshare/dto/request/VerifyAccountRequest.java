package com.dev.photoshare.dto.request;

import lombok.Getter;

@Getter
public class VerifyAccountRequest {
    private String email;
    private String otp;
}
