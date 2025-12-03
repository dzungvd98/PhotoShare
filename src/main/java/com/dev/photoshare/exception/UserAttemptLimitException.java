package com.dev.photoshare.exception;

public class UserAttemptLimitException extends RuntimeException {
    public UserAttemptLimitException(String message) {
        super(message);
    }
}