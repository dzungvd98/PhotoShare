package com.dev.photoshare.repository;

import com.dev.photoshare.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(int userId);

    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ip AND a.createdAt > :since ORDER BY a.createdAt DESC")
    List<AuditLog> findRecentByIp(@Param("ip") String ip, @Param("since") LocalDateTime since);
}