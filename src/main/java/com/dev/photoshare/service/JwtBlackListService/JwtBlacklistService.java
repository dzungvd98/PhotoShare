package com.dev.photoshare.service.JwtBlackListService;

import com.dev.photoshare.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor // Tự động tiêm (inject) các final fields
public class JwtBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtService; // Service từ bước trên

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:jwt:";

    /**
     * Thêm Access Token vào blacklist khi logout.
     */
    public void blacklistToken(String accessToken) {
        Instant expiryTime = jwtService.getExpirationDateFromToken(accessToken).toInstant();
        Instant now = Instant.now();

        // Chỉ lưu nếu token vẫn còn hạn
        if (expiryTime.isAfter(now)) {
            long timeToLiveMs = expiryTime.toEpochMilli() - now.toEpochMilli();

            String key = BLACKLIST_KEY_PREFIX + accessToken;

            // Lưu vào Redis với thời gian (TTL) bằng thời gian còn lại của token
            redisTemplate.opsForValue().set(
                    key,
                    "blacklisted",
                    Duration.ofMillis(timeToLiveMs)
            );
        }
    }

    /**
     * Kiểm tra xem token có bị blacklist không (gọi từ Filter)
     */
    public boolean isTokenBlacklisted(String accessToken) {
        String key = BLACKLIST_KEY_PREFIX + accessToken;
        // Kiểm tra xem key có tồn tại trong Redis không
        return redisTemplate.hasKey(key);
    }
}
