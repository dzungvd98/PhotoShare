package com.dev.photoshare.security.refresh;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtils {

    public ResponseCookie refreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(30))
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refresh_token", "")
                .path("/auth/refresh")
                .maxAge(0)
                .build();
    }
}
