package com.dev.photoshare.service.RateLimiterService;
import io.github.bucket4j.Bucket;

public interface IRateLimiterService {
    void checkRateLimit(String username, String ipAddress);
    Bucket resolveBucket(String key, int capacity, int refillMinutes);
    void resetRateLimit(String username, String ipAddress);
    void clearAll();
}
