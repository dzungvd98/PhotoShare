package com.dev.photoshare.dto.response;

import com.dev.photoshare.utils.enums.ModerationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PhotoReviewResponse {
    private long photoId;
    private ModerationStatus oldStatus;
    private ModerationStatus newStatus;
    private int moderatedBy;
    private LocalDateTime moderatedAt;
    private String message;
    private String reason;

}
