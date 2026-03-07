package com.dev.photoshare.service.ViolationReportService;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.request.ViolationHandleRequest;
import com.dev.photoshare.dto.request.ViolationSearchRequest;
import com.dev.photoshare.dto.response.ListReportResponse;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.dto.response.ViolationHandleResponse;
import com.dev.photoshare.dto.response.ViolationReportResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface IViolationReportService {
    PageResponse<ListReportResponse> getViolationReports(Pageable pageable, ViolationSearchRequest violationSearchRequest);

    ViolationReportResponse sendViolationReport(int userReportId, ReportViolationRequest request);

    ViolationHandleResponse handleViolationReport(long id, ViolationHandleRequest request);
}
