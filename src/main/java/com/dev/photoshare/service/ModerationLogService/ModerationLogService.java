package com.dev.photoshare.service.ModerationLogService;

import com.dev.photoshare.repository.ModerationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationLogService implements IModerationLogService {
    private final ModerationLogRepository  moderationLogRepository;
}
