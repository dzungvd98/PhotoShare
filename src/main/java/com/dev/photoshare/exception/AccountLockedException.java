package com.dev.photoshare.exception;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccountLockedException extends AuthException {
    private final LocalDateTime lockedUntil;
    private final String reason;

    public AccountLockedException(LocalDateTime lockedUntil, String reason) {
        super("ACCOUNT_LOCKED", "Account is locked until " + lockedUntil);
        this.lockedUntil = lockedUntil;
        this.reason = reason;
    }
}