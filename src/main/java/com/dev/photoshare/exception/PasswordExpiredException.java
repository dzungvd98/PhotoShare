package com.dev.photoshare.exception;

import lombok.Getter;

@Getter
public class PasswordExpiredException extends AuthException {
    private final String resetPasswordUrl;

    public PasswordExpiredException(String message, String resetPasswordUrl) {
        super("PASSWORD_EXPIRED", message);
        this.resetPasswordUrl = resetPasswordUrl;
    }
}