package com.dev.photoshare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean requiresMfa;
    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
    private UserInfo user;
    private List<String> mfaMethods;
    private String sessionToken;

    @Builder
    @Getter @Setter
    public static class UserInfo {
        private Integer id;
        private String username;
        private String email;
        private String fullName;
        private String role;
        private LocalDateTime lastLoginAt;
    }
}
