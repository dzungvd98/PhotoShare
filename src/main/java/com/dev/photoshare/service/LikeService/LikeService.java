package com.dev.photoshare.service.LikeService;

import com.dev.photoshare.entity.Likes;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.*;
import com.dev.photoshare.utils.enums.LikeableType;
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
    private final CommentStatsRepository  commentStatsRepository;

    @Transactional
    public boolean toggleLike(int userId, long likeableId, LikeableType likeableType) {
        Likes existingLike = likeRepository.findByUserIdAndLikeableIdAndLikeableType(
                userId, likeableId, likeableType
        ).orElse(null);

        boolean isLiked;

        if (likeableType == LikeableType.PHOTO) {
            isLiked = handlePhotoLike(userId, likeableId, existingLike);
        } else if (likeableType == LikeableType.COMMENT) {
            isLiked = handleCommentLike(userId, likeableId, existingLike);
        } else {
            throw new IllegalArgumentException("Unsupported LikeableType: " + likeableType);
        }

        return isLiked;
    }

    private boolean handlePhotoLike(int userId, long photoId, Likes existingLike) {
        if (!photoStatsRepository.existsByPhotoId(photoId)) {
            throw new EntityNotFoundException("Photo not found with ID: " + photoId);
        }

        if (existingLike == null) {
            saveLike(userId, photoId, LikeableType.PHOTO);
            photoStatsRepository.incrementLikeCount(photoId);
            return true;
        } else {
            likeRepository.delete(existingLike);
            photoStatsRepository.decrementLikeCount(photoId);
            return false;
        }
    }

    private boolean handleCommentLike(int userId, long commentId, Likes existingLike) {
        if(!commentStatsRepository.existsByCommentId(commentId)) {
            throw new EntityNotFoundException("Comment not found with ID: " + commentId);
        }

        if(existingLike == null) {
            saveLike(userId, commentId, LikeableType.COMMENT);
            commentStatsRepository.incrementLikeCount(commentId);
            return true;
        } else {
            likeRepository.delete(existingLike);
            commentStatsRepository.decrementLikeCount(commentId);
            return false;
        }
    }

    private void saveLike(int userId, long likeableId, LikeableType type) {
        Likes like = new Likes();
        like.setUser(new Users(userId));
        like.setLikeableType(type);
        like.setLikeableId(likeableId);
        likeRepository.save(like);
    }
}
