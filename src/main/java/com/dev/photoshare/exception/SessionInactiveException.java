package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SessionInactiveException extends BaseException {

  public SessionInactiveException() {
    super(
            "Session is no longer active",
            HttpStatus.FORBIDDEN,
            "SESSION_INACTIVE"
    );
  }
}
