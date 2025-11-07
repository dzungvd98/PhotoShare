package com.dev.photoshare.repository;

import com.dev.photoshare.entity.ModerationLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationLogRepository extends JpaRepository<ModerationLogs,Long> {
}
