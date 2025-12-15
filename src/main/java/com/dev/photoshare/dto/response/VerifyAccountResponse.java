package com.dev.photoshare.dto.response;

import java.time.LocalDateTime;

public record VerifyAccountResponse(
        String message,
        LocalDateTime verifiedAt
) {}