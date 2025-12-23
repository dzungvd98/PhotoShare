package com.dev.photoshare.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BaseException {
    public AccountDisabledException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED");
    }
}
