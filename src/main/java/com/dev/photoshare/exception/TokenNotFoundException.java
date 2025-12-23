package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenNotFoundException extends BaseException {

    public TokenNotFoundException(String message) {
        super(
                message,
                HttpStatus.UNAUTHORIZED,
                "TOKEN_NOT_FOUND",
                null
        );
    }
}