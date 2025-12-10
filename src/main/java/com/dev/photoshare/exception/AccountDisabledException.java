package com.dev.photoshare.exception;

public class AccountDisabledException extends AuthException {
    public AccountDisabledException(String message) {
        super("ACCOUNT_DISABLED", message);
    }
}
