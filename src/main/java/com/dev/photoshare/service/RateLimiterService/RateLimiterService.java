package com.dev.photoshare.service.RateLimiterService;

import com.dev.photoshare.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // 5 attempts per 15 minutes per IP
    private static final int IP_CAPACITY = 5;
    private static final int IP_REFILL_MINUTES = 15;

    // 3 attempts per 15 minutes per username
    private static final int USERNAME_CAPACITY = 3;
    private static final int USERNAME_REFILL_MINUTES = 15;

    public void checkRateLimit(String username, String ipAddress) {
        // Check IP-based rate limit
        String ipKey = "ip:" + ipAddress;
        Bucket ipBucket = resolveBucket(ipKey, IP_CAPACITY, IP_REFILL_MINUTES);

        if (!ipBucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for IP: {}", ipAddress);
            throw new RateLimitExceededException(
                    "Too many login attempts from this IP address. Please try again later.",
                    IP_REFILL_MINUTES * 60
            );
        }

        // Check username-based rate limit
        if (username != null && !username.isBlank()) {
            String userKey = "user:" + username.toLowerCase();
            Bucket userBucket = resolveBucket(userKey, USERNAME_CAPACITY, USERNAME_REFILL_MINUTES);

            if (!userBucket.tryConsume(1)) {
                log.warn("Rate limit exceeded for username: {}", username);
                throw new RateLimitExceededException(
                        "Too many login attempts for this account. Please try again later.",
                        USERNAME_REFILL_MINUTES * 60
                );
            }
        }
    }

    /**
     * Resolves or creates a bucket for a given key
     */
    private Bucket resolveBucket(String key, int capacity, int refillMinutes) {
        return buckets.computeIfAbsent(key, k -> createBucket(capacity, refillMinutes));
    }

    /**
     * Creates a new bucket with specified capacity and refill rate
     */
    private Bucket createBucket(int capacity, int refillMinutes) {
        Bandwidth limit = Bandwidth.classic(
                capacity,
                Refill.intervally(capacity, Duration.ofMinutes(refillMinutes))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Resets rate limit for a specific key (e.g., after successful login)
     */
    public void resetRateLimit(String username, String ipAddress) {
        if (ipAddress != null) {
            buckets.remove("ip:" + ipAddress);
        }
        if (username != null) {
            buckets.remove("user:" + username.toLowerCase());
        }
    }

    /**
     * Clears all rate limit buckets (for maintenance)
     */
    public void clearAll() {
        buckets.clear();
        log.info("All rate limit buckets cleared");
    }
}
