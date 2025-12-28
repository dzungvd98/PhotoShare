package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.dto.response.ViolationReportResponse;
import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.ViolationReportService.IViolationReportService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/report")
@RequiredArgsConstructor
@Tag(name = "Violation Report Controller", description = "Violation Report APIs")
public class ViolationReportController {
    private final IViolationReportService violationReportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ViolationReportResponse>> violationReport(@RequestBody @Valid ReportViolationRequest request) {
        int userId = getUserIdFromToken();

        ViolationReportResponse response = violationReportService.sendViolationReport(userId, request);

        return ResponseEntityBuilder.created("Gửi báo cáo thành công, nội dung sẽ được quản trị viên xem xét", response);
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
