package com.dev.photoshare.service.PhotoViewDailyService;

import com.dev.photoshare.repository.PhotoViewDailyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoViewDailyService implements IPhotoViewDailyService{
    private final PhotoViewDailyRepository photoViewDailyRepository;
}
