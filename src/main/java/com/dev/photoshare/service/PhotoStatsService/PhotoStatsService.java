package com.dev.photoshare.service.PhotoStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoStatsService implements IPhotoStatsService{
    private final RedisTemplate<String, String> redis;
    private static final String VIEW_KEY_PREFIX = "photo:view:";
    private static final String PENDING_SET_KEY = "photo:pending";

    // tăng counter trong Redis
    public void increaseViewCounter(Long photoId, long inc) {
        String key = VIEW_KEY_PREFIX + photoId;
        redis.opsForValue().increment(key, inc);
        // đánh dấu photo cần sync
        redis.opsForSet().add(PENDING_SET_KEY, String.valueOf(photoId));
    }

    // lấy và xóa pending IDs atomically
    public Set<String> pollPendingPhotoIds() {
        // lấy toàn bộ members (kịch bản nhỏ). Nếu lớn, dùng scan/stream.
        Set<String> members = redis.opsForSet().members(PENDING_SET_KEY);
        if (members == null || members.isEmpty()) return Collections.emptySet();
        // xóa các phần tử đó khỏi pending (SREM)
        redis.opsForSet().remove(PENDING_SET_KEY, members.toArray());
        return members;
    }

    public long getAndDeleteCounter(Long photoId) {
        String key = VIEW_KEY_PREFIX + photoId;
        String value = redis.opsForValue().get(key);
        if (value == null) return 0L;
        // reset key (delete)
        redis.delete(key);
        return Long.parseLong(value);
    }
}
