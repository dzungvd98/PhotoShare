package com.dev.photoshare.exception;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BaseException {
    public ResourceConflictException(String message) {
        super(
                message,
                HttpStatus.CONFLICT,
                "RESOURCE_CONFLICT"
        );
    }
}
