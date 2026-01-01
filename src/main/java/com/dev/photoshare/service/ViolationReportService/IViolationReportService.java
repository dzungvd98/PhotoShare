package com.dev.photoshare.service.ViolationReportService;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.request.ViolationHandleRequest;
import com.dev.photoshare.dto.response.ViolationHandleResponse;
import com.dev.photoshare.dto.response.ViolationReportResponse;
import jakarta.validation.Valid;

public interface IViolationReportService {
    ViolationReportResponse sendViolationReport(int userReportId, ReportViolationRequest request);

    ViolationHandleResponse handleViolationReport(long id, ViolationHandleRequest request);
}
