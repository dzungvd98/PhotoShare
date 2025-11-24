package com.dev.photoshare.repository;

import com.dev.photoshare.entity.PhotoStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoStatsRepository extends JpaRepository<PhotoStats,Long> {
    Optional<PhotoStats> findByPhotoId(long photoId);

    boolean existsByPhotoId(long photoId);

    @Modifying
    @Query("UPDATE PhotoStats p SET p.likeCount = p.likeCount + 1 WHERE p.photoId = :photoId")
    void incrementLikeCount(@Param("photoId") long photoId);

    @Modifying
    @Query("UPDATE PhotoStats p SET p.likeCount = p.likeCount - 1 WHERE p.photoId = :photoId")
    void decrementLikeCount(@Param("photoId") long photoId);

    @Modifying
    @Query("UPDATE PhotoStats ps SET ps.viewCount = ps.viewCount + :inc WHERE ps.photo.id = :id")
    void incrementView(@Param("id") long photoId, @Param("inc") long inc);
}
