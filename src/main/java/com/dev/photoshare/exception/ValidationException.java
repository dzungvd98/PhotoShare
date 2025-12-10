package com.dev.photoshare.exception;

public class ValidationException extends AuthException {
    public ValidationException(String message) {
        super("INVALID_INPUT", message);
    }
}