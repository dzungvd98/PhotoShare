package com.dev.photoshare.service.PhotoStatsService;

import com.dev.photoshare.repository.PhotoStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoStatsService implements IPhotoStatsService{
    private final PhotoStatsRepository photoStatsRepository;
}
