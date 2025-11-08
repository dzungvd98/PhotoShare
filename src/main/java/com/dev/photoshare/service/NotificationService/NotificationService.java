package com.dev.photoshare.service.NotificationService;

import com.dev.photoshare.repository.NotificationRepository;
import com.dev.photoshare.service.LikeService.ILikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;
}
