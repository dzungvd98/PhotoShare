package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException(String message) {
        super(
                message,
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }
}

