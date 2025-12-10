package com.dev.photoshare.exception;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends AuthException {
    private final Integer attemptsRemaining;

    public InvalidCredentialsException(String message, Integer attemptsRemaining) {
        super("INVALID_CREDENTIALS", message);
        this.attemptsRemaining = attemptsRemaining;
    }

    public InvalidCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message);
        this.attemptsRemaining = null;
    }
}
