package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.utils.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Users> findByUsernameAndStatus(String username, UserStatus status);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.userStats")
    Page<Users> findAllWithStats(Pageable pageable);

    @Modifying
    @Query("UPDATE Users u SET u.failedLoginAttempts = u.failedLoginAttempts + 1, u.updatedAt = :now WHERE u.id = :userId")
    void incrementFailedAttempts(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Users u SET u.failedLoginAttempts = 0, u.updatedAt = :now WHERE u.id = :userId")
    void resetFailedAttempts(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Users u SET u.lockedUntil = :lockedUntil, u.status = 'ACTIVE', u.updatedAt = :now WHERE u.id = :userId")
    void lockAccount(@Param("userId") Integer userId, @Param("lockedUntil") LocalDateTime lockedUntil, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Users u SET u.lastLogin = :loginTime, u.lastLoginIp = :ip, u.updatedAt = :now WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Integer userId, @Param("loginTime") LocalDateTime loginTime,
                         @Param("ip") String ip, @Param("now") LocalDateTime now);
}
