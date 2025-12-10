package com.dev.photoshare.exception;

@Getter
public class AuthException extends RuntimeException {
    private final String errorCode;

    public AuthException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
