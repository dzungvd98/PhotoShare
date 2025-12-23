package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class PasswordExpiredException extends BaseException {

    public PasswordExpiredException(String message, String resetPasswordUrl) {
        super(
                message,
                HttpStatus.FORBIDDEN,
                "PASSWORD_EXPIRED",
                Map.of("resetPasswordUrl", resetPasswordUrl)
        );
    }
}
