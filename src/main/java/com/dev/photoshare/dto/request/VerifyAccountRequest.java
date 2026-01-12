package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyAccountRequest {
    @NotBlank(message = "Mã định danh tài khoản là bắt buộc")
    private String mid;

    @NotBlank(message = "Otp là bắt buộc!")
    private String otp;
}
