package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
public class LstProfileResponse {
    private Integer id;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String authProvider;
    private String status;
    private String roleName;
    private Integer postCount;
    private Integer followingCount;
    private Integer followersCount;
    private String displayName;
    private String avatarUrl;
    private LocalDateTime createdAt;
}