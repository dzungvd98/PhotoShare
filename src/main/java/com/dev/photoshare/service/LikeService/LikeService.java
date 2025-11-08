package com.dev.photoshare.service.LikeService;

import com.dev.photoshare.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService implements ILikeService {
    private final LikeRepository likeRepository;
}
