package com.dev.photoshare.service.ViewGuardService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ViewGuardService {
    private final RedisTemplate<String, String> redis;
    private static final long USER_TTL_SECONDS = 24 * 3600L;
    private static final long IP_TTL_SECONDS = 30L;
    private static final long RATE_LIMIT_WINDOW_SECONDS = 60L;
    private static final long RATE_LIMIT_MAX = 30L;

    public boolean shouldCountView(Long userId, String ip, Long photoId) {
        if (userId != null) {
            String key = "view:user:" + userId + ":photo:" + photoId;
            Boolean exists = redis.hasKey(key);
            if (Boolean.TRUE.equals(exists)) return false;
            // set lock 1 day
            redis.opsForValue().set(key, "1", USER_TTL_SECONDS, TimeUnit.SECONDS);
            // rate limit
            return checkRateLimitForUser(userId);
        } else {
            String key = "view:ip:" + ip + ":photo:" + photoId;
            Boolean exists = redis.hasKey(key);
            if (Boolean.TRUE.equals(exists)) return false;
            redis.opsForValue().set(key, "1", IP_TTL_SECONDS, TimeUnit.SECONDS);
            // no rate limit for anon or you can add per-ip rate limit
            return true;
        }
    }

    private boolean checkRateLimitForUser(Long userId) {
        String key = "rate:photo:view:user:" + userId;
        Long count = redis.opsForValue().increment(key);
        if (count == 1) {
            redis.expire(key, RATE_LIMIT_WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        return count <= RATE_LIMIT_MAX;
    }
}
