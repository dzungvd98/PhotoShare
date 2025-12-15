package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyAccountRequest {
    @Email(message = "invalid email!")
    @NotBlank(message = "email is required!")
    private String email;

    @NotBlank(message = "otp is required!")
    private String otp;
}
