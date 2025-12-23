package com.dev.photoshare.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class AccountLockedException extends BaseException {
    public AccountLockedException(LocalDateTime lockedUntil, String reason) {
        super( "Account is locked until " + lockedUntil, HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED", Map.of(
                "lockedUntil", lockedUntil.toString(),
                "reason", reason
        ));
    }
}