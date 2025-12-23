package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ValidationException extends BaseException {

    public ValidationException(String field, String message) {
        super(
                message,
                HttpStatus.BAD_REQUEST,
                "INVALID_INPUT",
                Map.of("field", field)
        );
    }
}