package com.dev.photoshare.dto.response;

import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ListReportResponse {
    private int reporterId;
    private ViolationReportStatus status;
    private TargetType targetType;
    private Long targetId;
    private int reportedPersonId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
