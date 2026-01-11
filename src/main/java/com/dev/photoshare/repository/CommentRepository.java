package com.dev.photoshare.repository;

import com.dev.photoshare.dto.projection.CommentProjection;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.entity.Comments;
import com.dev.photoshare.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comments,Long> {
    Optional<Comments> findById(long id);
    @Query(
            value = """
        SELECT u.*
        FROM comments c
        JOIN users u ON c.user_id = u.id
        WHERE c.id = :commentId
          AND c.deleted = false
        """,
            nativeQuery = true
    )
    Optional<Users> findAuthorByCommentId(@Param("commentId") Long commentId);

    @Query("""
    SELECT
        c.id AS commentId,
        c.content AS commentContent,
        c.createdAt AS createdDate,
        u.id AS userId,
        p.displayName AS userDisplayName,
        p.avatarUrl AS userAvatar,
        c.replyCount AS replyCount
    FROM Comments c
    JOIN c.user u
    LEFT JOIN u.profile p
    WHERE c.photo.id = :photoId
      AND c.parent IS NULL
      AND c.deleted = false
    ORDER BY c.createdAt DESC
    """)
    Page<CommentProjection> findRootCommentsByPhotoId(
            @Param("photoId") Long photoId,
            Pageable pageable
    );


    @Query("""
        SELECT
            c.id AS commentId,
            c.content AS commentContent,
            c.createdAt AS createdDate,
            u.id AS userId,
            p.displayName AS userDisplayName,
            p.avatarUrl AS userAvatar
        FROM Comments c
        JOIN c.user u
        LEFT JOIN u.profile p
        WHERE c.parent.id = :parentId
        AND c.deleted = false
        ORDER BY c.createdAt ASC
    """)
    Page<CommentProjection> findRepliesByParentId(
            @Param("parentId") Long parentId,
            Pageable pageable
    );

    @Modifying
    @Query("""
    UPDATE Comments c
    SET c.deleted = true
    WHERE c.id = :rootId OR c.parent.id = :rootId
    """)
    void softDeleteRootAndReplies(@Param("rootId") Long rootId);

}
