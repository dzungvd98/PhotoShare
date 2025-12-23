package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class AccountNotVerifiedException extends BaseException {
    public AccountNotVerifiedException(String message, String resendVerificationUrl) {
        super(
                message,
                HttpStatus.FORBIDDEN,
                "ACCOUNT_NOT_VERIFIED",
                Map.of("resendVerificationUrl", resendVerificationUrl)
        );
    }
}

