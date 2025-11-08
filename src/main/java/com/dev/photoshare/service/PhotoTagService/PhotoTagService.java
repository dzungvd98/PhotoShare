package com.dev.photoshare.service.PhotoTagService;

import com.dev.photoshare.repository.PhotoTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoTagService implements IPhotoTagService{
    private final PhotoTagRepository photoTagRepository;
}
