package com.dev.photoshare.service.ViolationReportService;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.response.ViolationReportResponse;

public interface IViolationReportService {
    ViolationReportResponse sendViolationReport(int userReportId, ReportViolationRequest request);
}
