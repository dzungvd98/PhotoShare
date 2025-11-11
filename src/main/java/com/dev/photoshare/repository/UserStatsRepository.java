package com.dev.photoshare.repository;

import com.dev.photoshare.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE UserStats us SET us.followingCount = us.followingCount + 1 WHERE us.userId = :userId")
    int incrementFollowingCount(@Param("userId") Integer userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserStats us SET us.followingCount = us.followingCount - 1 WHERE us.userId = :userId AND us.followingCount > 0")
    int decrementFollowingCount(@Param("userId") Integer userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserStats us SET us.followersCount = us.followersCount + 1 WHERE us.userId = :userId")
    int incrementFollowersCount(@Param("userId") Integer userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserStats us SET us.followersCount = us.followersCount - 1 WHERE us.userId = :userId AND us.followersCount > 0")
    int decrementFollowersCount(@Param("userId") Integer userId);
}
