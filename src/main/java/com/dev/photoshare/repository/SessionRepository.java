package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByRefreshToken(String refreshToken);

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.active = true AND s.status = 'ACTIVE'")
    List<Session> findActiveSessionsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Session s SET s.active = false, s.status = 'REVOKED' WHERE s.id = :sessionId")
    void revokeSession(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("UPDATE Session s SET s.active = false, s.status = 'REVOKED' WHERE s.user.id = :userId")
    void revokeAllUserSessions(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Session s SET s.lastAccessedAt = :accessTime WHERE s.id = :sessionId")
    void updateLastAccessed(@Param("sessionId") Long sessionId, @Param("accessTime") LocalDateTime accessTime);
}
