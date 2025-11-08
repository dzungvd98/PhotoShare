package com.dev.photoshare.repository;

import com.dev.photoshare.entity.PhotoStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoStatsRepository extends JpaRepository<PhotoStats,Long> {
}
