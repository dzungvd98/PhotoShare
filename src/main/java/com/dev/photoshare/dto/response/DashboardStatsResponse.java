package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardStatsResponse {
    private long totalImages;
    private long totalPendingImages;
    private long totalUsers;
}
