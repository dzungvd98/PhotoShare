package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidOtpException extends BaseException {

    public InvalidOtpException(String message) {
        super(
                message,
                HttpStatus.BAD_REQUEST,
                "INVALID_OTP"
        );
    }
}