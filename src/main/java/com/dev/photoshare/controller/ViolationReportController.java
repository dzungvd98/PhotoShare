package com.dev.photoshare.controller;

import com.dev.photoshare.dto.projection.ViolationReportView;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
@Tag(name = "Violation Report Controller", description = "Violation Report APIs")
public class ViolationReportController {
    private final IViolationReportService violationReportService;

    @PreAuthorize("hasAnyRole('ADMIN','MOD')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ViolationReportView>>> getViolationReports(@Valid ViolationSearchRequest req,
                                                                                              @RequestParam(defaultValue = "1")
                                                                                             @Min(value = 1, message = "pageNum phải >= 1")
                                                                                             int pageNum,
                                                                                              @RequestParam(defaultValue = "10")
                                                                                             @Min(value = 1, message = "pageSize phải >= 1")
                                                                                             @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageResponse<ViolationReportView>response  = violationReportService.getViolationReports(pageable, req);

        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d report", response.getTotalElements()),response);
    }


    @PostMapping
    public ResponseEntity<ApiResponse<ViolationReportResponse>> violationReport(@RequestBody @Valid ReportViolationRequest request) {
        int userId = getUserIdFromToken();

        ViolationReportResponse response = violationReportService.sendViolationReport(userId, request);

        return ResponseEntityBuilder.created("Gửi báo cáo thành công, nội dung sẽ được quản trị viên xem xét", response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MOD')")
    @PostMapping("/{id}/handle")
    public ResponseEntity<ApiResponse<ViolationHandleResponse>> handleViolation(@PathVariable int id,
                                                                                @RequestBody @Valid ViolationHandleRequest request) {
        int  userId = getUserIdFromToken();
        ViolationHandleResponse response = violationReportService.handleViolationReport(id, request,userId);
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
