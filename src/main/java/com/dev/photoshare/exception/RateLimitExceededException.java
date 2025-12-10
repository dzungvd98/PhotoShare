package com.dev.photoshare.exception;

@Getter
public class RateLimitExceededException extends AuthException {
    private final Integer retryAfter;

    public RateLimitExceededException(String message, Integer retryAfter) {
        super("RATE_LIMIT_EXCEEDED", message);
        this.retryAfter = retryAfter;
    }
}