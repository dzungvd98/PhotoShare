package com.dev.photoshare.service.UserStatsService;

import com.dev.photoshare.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsService implements IUserStatsService{
    private final UserStatsRepository userStatsRepository;
}
