package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.request.ViolationHandleRequest;
import com.dev.photoshare.dto.request.ViolationSearchRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.ViolationReportService.IViolationReportService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
@Tag(name = "Violation Report Controller", description = "Violation Report APIs")
public class ViolationReportController {
    private final IViolationReportService violationReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ListReportResponse>>> getViolationReports(@Valid ViolationSearchRequest req, @PageableDefault(size = 10, page = 1) Pageable pageable) {
        PageResponse<ListReportResponse>response  =violationReportService.getViolationReports(pageable, req);

        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d report", response.getTotalElements()),response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ViolationReportResponse>> violationReport(@RequestBody @Valid ReportViolationRequest request) {
        int userId = getUserIdFromToken();

        ViolationReportResponse response = violationReportService.sendViolationReport(userId, request);

        return ResponseEntityBuilder.created("Gửi báo cáo thành công, nội dung sẽ được quản trị viên xem xét", response);
    }

    @PostMapping("/{id}/handle")
    public ResponseEntity<ApiResponse<ViolationHandleResponse>> handleViolation(@PathVariable int id,
                                                                                @RequestBody @Valid ViolationHandleRequest request) {
        ViolationHandleResponse response = violationReportService.handleViolationReport(id, request);
        return ResponseEntityBuilder.ok("Báo cáo đã được xử lý", response);
    }


    private int getUserIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Không có quyền truy cập");
        }

        return userDetails.getId();
    }
}
