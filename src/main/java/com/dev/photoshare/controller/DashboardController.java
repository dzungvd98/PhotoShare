package com.dev.photoshare.controller;

import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.dto.response.DashboardStatsResponse;
import com.dev.photoshare.service.DashboardService.IDashboardService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dashboard")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Dashboard Controller")
public class DashboardController {
    private final IDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        return ResponseEntityBuilder.ok(dashboardService.getDashboardStats());
    }
}
