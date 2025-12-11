package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private Integer retryAfter;
    private LocalDateTime lockedUntil;
    private String reason;
    private Integer attemptsRemaining;
    private String resendVerificationUrl;
    private String resetPasswordUrl;
    private List<String> details;
    Map<String, String> errors = new HashMap<>(); // for validate input

    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

}