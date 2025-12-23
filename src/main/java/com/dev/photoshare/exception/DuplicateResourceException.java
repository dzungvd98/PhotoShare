package com.dev.photoshare.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseException {
  public DuplicateResourceException(String resource, String field, Object value) {
    super(
            String.format("%s đã tồn tại với %s: '%s'", resource, field, value),
            HttpStatus.CONFLICT,
            "DUPLICATE_RESOURCE"
    );
  }
}
