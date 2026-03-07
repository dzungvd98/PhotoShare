package com.dev.photoshare.dto.request;

import com.dev.photoshare.utils.enums.ViolationReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ViolationSearchRequest {
    private ViolationReportStatus status;
    private String sortBy;
}
