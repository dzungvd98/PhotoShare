package com.dev.photoshare.service.LikeService;

import com.dev.photoshare.entity.Likes;
import com.dev.photoshare.entity.PhotoStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.UserNotFoundException;
import com.dev.photoshare.repository.LikeRepository;
import com.dev.photoshare.repository.PhotoStatsRepository;
import com.dev.photoshare.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService implements ILikeService {
    private final LikeRepository likeRepository;
    private final PhotoStatsRepository photoStatsRepository;

    @Transactional
    public boolean toggleLike(int userId, long photoId) {
        Likes existing = likeRepository.findByUserIdAndPhotoId(userId, photoId)
                .orElse(null);

        PhotoStats stats = photoStatsRepository.findByPhotoId(photoId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoStats not found!"));
        if (existing == null) {
            likePhoto(userId, photoId, stats);
            return true;
        } else {
            unlikePhoto(stats, existing);
            return false;
        }
    }

    private void likePhoto(int userId, long photoId, PhotoStats photoStats) {
        Likes like = new Likes();
        like.setUser(new Users(userId));
        like.setLikeableType("Photo");
        like.setLikeableId(photoId);
        likeRepository.save(like);
        photoStats.setLikeCount(photoStats.getLikeCount() + 1);
        photoStatsRepository.save(photoStats);
    }

    private void unlikePhoto(PhotoStats photoStats, Likes like) {
        likeRepository.delete(like);
        photoStats.setLikeCount(photoStats.getLikeCount() - 1);
        photoStatsRepository.save(photoStats);
    }
}
