package com.dev.photoshare.exception;

public class IpRateLimitException extends RuntimeException {
    public IpRateLimitException(String message) {
        super(message);
    }
}