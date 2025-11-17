package com.dev.photoshare.repository;

import com.dev.photoshare.entity.PhotoStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoStatsRepository extends JpaRepository<PhotoStats,Long> {
    Optional<PhotoStats> findByPhotoId(long photoId);
}
