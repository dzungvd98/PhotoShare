package com.dev.photoshare.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
  public ResourceNotFoundException(String resource, String field, Object value) {
    super(
            String.format("%s không tồn tại với %s: '%s'", resource, field, value),
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
    );
  }
}
