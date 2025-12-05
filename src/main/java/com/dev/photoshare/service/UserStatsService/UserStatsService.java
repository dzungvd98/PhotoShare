package com.dev.photoshare.service.UserStatsService;

import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatsService implements IUserStatsService{
    private final UserRepository userRepository;

    @Override
    public void increasePostCount(Users user) {
        UserStats stats = Optional.ofNullable(user.getUserStats())
                .orElseGet(() -> {
                    UserStats s = UserStats.builder().user(user).build();
                    user.setUserStats(s);
                    return s;
                });
        stats.setPostCount(stats.getPostCount() + 1);
        userRepository.save(user);
    }
}
