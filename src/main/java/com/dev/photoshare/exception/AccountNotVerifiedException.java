package com.dev.photoshare.exception;

@Getter
public class AccountNotVerifiedException extends AuthException {
    private final String resendVerificationUrl;

    public AccountNotVerifiedException(String message, String resendVerificationUrl) {
        super("EMAIL_NOT_VERIFIED", message);
        this.resendVerificationUrl = resendVerificationUrl;
    }
}

