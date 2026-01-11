package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private DeviceInfo deviceInfo;

    @Builder
    @Getter @Setter
    public static class DeviceInfo {
        private String deviceName;
        private String deviceType;
        private String browser;
        private String operatingSystem;
        private String userAgent;
    }
}