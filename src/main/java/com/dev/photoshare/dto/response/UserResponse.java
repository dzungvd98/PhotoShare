package com.dev.photoshare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String status;
    private String roleName;
    private LocalDateTime lastLogin;
}
