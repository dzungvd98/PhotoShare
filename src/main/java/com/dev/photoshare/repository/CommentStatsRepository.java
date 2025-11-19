package com.dev.photoshare.repository;

import com.dev.photoshare.entity.CommentStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentStatsRepository extends JpaRepository<CommentStats,Long> {
    Optional<CommentStats> findByCommentId(Long commentId);

    boolean existsByCommentId(Long commentId);

    @Modifying
    @Query("UPDATE CommentStats c SET c.likeCount = c.likeCount + 1 WHERE c.commentId = :commentId")
    void incrementLikeCount(@Param("commentId") long commentId);

    @Modifying
    @Query("UPDATE CommentStats c SET c.likeCount = c.likeCount - 1 WHERE c.commentId = :commentId")
    void decrementLikeCount(@Param("commentId") long commentId);
}
