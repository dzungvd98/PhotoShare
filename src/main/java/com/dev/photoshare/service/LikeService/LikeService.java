package com.dev.photoshare.service.LikeService;

import com.dev.photoshare.entity.Likes;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.*;
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
    public boolean toggleLike(int userId, long likeableId) {
        Likes existingLike = likeRepository.findByUserIdAndLikeableId(
                userId, likeableId
        ).orElse(null);

        return handlePhotoLike(userId, likeableId, existingLike);
    }

    private boolean handlePhotoLike(int userId, long photoId, Likes existingLike) {
        if (!photoStatsRepository.existsByPhotoId(photoId)) {
            throw new EntityNotFoundException("Photo not found with ID: " + photoId);
        }

        if (existingLike == null) {
            saveLike(userId, photoId);
            photoStatsRepository.incrementLikeCount(photoId);
            return true;
        } else {
            likeRepository.delete(existingLike);
            photoStatsRepository.decrementLikeCount(photoId);
            return false;
        }
    }


    private void saveLike(int userId, long likeableId) {
        Likes like = new Likes();
        like.setUser(new Users(userId));
        like.setLikeableId(likeableId);
        likeRepository.save(like);
    }
}
