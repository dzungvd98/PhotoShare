package com.dev.photoshare.service.ModerationLogService;

import com.dev.photoshare.repository.CommentRepository;
import com.dev.photoshare.repository.ModerationLogRepository;
import com.dev.photoshare.service.LikeService.ILikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationLogService implements ILikeService {
    private final ModerationLogRepository  moderationLogRepository;
}
