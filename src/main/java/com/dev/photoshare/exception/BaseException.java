package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final Map<String, Object> metadata;

    public BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.metadata = null;
    }

    public BaseException(String message, HttpStatus httpStatus, String errorCode,
                         Map<String, Object> metadata) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.metadata = metadata;
    }

    public BaseException(String message, Throwable cause, HttpStatus httpStatus,
                         String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.metadata = null;
    }
}
