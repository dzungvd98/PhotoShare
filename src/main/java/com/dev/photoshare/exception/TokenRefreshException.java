package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenRefreshException extends BaseException {

    public TokenRefreshException(String message) {
        super(
                message,
                HttpStatus.UNAUTHORIZED,
                "TOKEN_REFRESH_FAILED",
                null
        );
    }
}