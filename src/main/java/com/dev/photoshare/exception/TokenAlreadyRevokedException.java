package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenAlreadyRevokedException extends BaseException {

    public TokenAlreadyRevokedException(String message) {
        super(
                message,
                HttpStatus.UNAUTHORIZED,
                "TOKEN_ALREADY_REVOKED",
                null
        );
    }
}