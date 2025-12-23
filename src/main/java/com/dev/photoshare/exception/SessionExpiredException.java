package com.dev.photoshare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SessionExpiredException extends BaseException {

  public SessionExpiredException() {
    super(
            "Session has expired",
            HttpStatus.FORBIDDEN,
            "SESSION_EXPIRED"
    );
  }
}
