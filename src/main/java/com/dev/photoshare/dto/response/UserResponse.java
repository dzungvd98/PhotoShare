package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class UserResponse {
    private String username;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String status;
    private String roleName;
    private LocalDateTime lastLogin;
}
