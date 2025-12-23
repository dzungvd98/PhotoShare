package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class RateLimitExceededException extends BaseException {
    public RateLimitExceededException(String message, Integer retryAfter) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", Map.of("retryAfter", retryAfter));
    }
}