package com.dev.photoshare.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class LoginResponse {

    private boolean requiresMfa;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private UserInfo user;
    private List<String> mfaMethods;
    private String sessionToken;

    @Builder
    @Getter @Setter
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private Set<String> roles;
        private LocalDateTime lastLoginAt;
    }
}
