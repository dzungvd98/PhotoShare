package com.dev.photoshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoStatsRepository extends JpaRepository<PhotoStatsRepository,Long> {
}
