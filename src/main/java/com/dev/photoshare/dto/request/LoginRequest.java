package com.dev.photoshare.dto.request;

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
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
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