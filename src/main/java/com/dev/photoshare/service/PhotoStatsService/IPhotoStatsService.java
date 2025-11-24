package com.dev.photoshare.service.PhotoStatsService;

import java.util.Set;

public interface IPhotoStatsService {
    void increaseViewCounter(Long photoId, long inc);
    Set<String> pollPendingPhotoIds();
    long getAndDeleteCounter(Long photoId);
}
