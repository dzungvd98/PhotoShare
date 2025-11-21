package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoRejectRequest {
    @NotEmpty(message = "Reason reject is required")
    private String reason;
}
