package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class UserAttemptLimitException extends BaseException {

    public UserAttemptLimitException(String message, Integer retryAfter) {
        super(
                message,
                HttpStatus.TOO_MANY_REQUESTS,
                "USER_ATTEMPT_LIMIT_EXCEEDED",
                Map.of("retryAfter", retryAfter)
        );
    }
}