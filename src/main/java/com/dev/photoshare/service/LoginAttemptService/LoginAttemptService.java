package com.dev.photoshare.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "login_attempts:";

    public int getAttempts(String username) {
        Integer count = (Integer) redisTemplate.opsForValue().get(PREFIX + username);
        return count == null ? 0 : count;
    }

    public void increase(String username) {
        String key = PREFIX + username;
        Integer count = (Integer) redisTemplate.opsForValue().get(key);

        if (count == null) count = 0;
        count++;

        redisTemplate.opsForValue().set(key, count, Duration.ofMinutes(15));
    }

    public void reset(String username) {
        redisTemplate.delete(PREFIX + username);
    }
}
