package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService implements IFollowService{
    private final FollowRepository followRepository;
}
