package com.dev.photoshare.service.DashboardService;

import com.dev.photoshare.dto.response.DashboardStatsResponse;
import com.dev.photoshare.repository.CommentRepository;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.utils.enums.PhotoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService implements IDashboardService{
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalImages = photoRepository.count();
        long totalPendingImages = photoRepository.countByStatus(PhotoStatus.PENDING);
        long  totalUsers = userRepository.count();

        return  DashboardStatsResponse.builder()
                .totalImages(totalImages)
                .totalPendingImages(totalPendingImages)
                .totalUsers(totalUsers)
                .build();
    }
}
