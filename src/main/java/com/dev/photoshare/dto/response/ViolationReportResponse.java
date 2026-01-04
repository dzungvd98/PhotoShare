package com.dev.photoshare.dto.response;

import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ViolationReportResponse {
    private int reporterId;
    private ViolationReportStatus status;
    private TargetType targetType;
    private Long targetId;
    private int reportedPersonId;
    private LocalDateTime createdAt;
}
