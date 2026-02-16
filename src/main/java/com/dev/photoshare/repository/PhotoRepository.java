package com.dev.photoshare.repository;

import com.dev.photoshare.dto.projection.PhotoFeedView;
import com.dev.photoshare.dto.response.AwaitingApprovalPhotoResponse;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.utils.enums.ModerationStatus;
import com.dev.photoshare.utils.enums.PhotoStatus;
import com.dev.photoshare.utils.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photos,Long> {
    Page<Photos> findAllByUser_IdOrderByUpdatedAtDesc(Integer userId, Pageable pageable);
    boolean existsByIdAndUser_Id(Long id, Integer userId);

    @Query(
            value = """
        SELECT u.*
        FROM photos p
        JOIN users u ON p.user_id = u.id
        WHERE p.id = :photoId
          AND p.is_archived = false
        """,
                nativeQuery = true
        )
    Optional<Users> findAuthorByPhotoId(@Param("photoId") Long photoId);

    @Query("""
        SELECT p FROM Photos p 
        JOIN Likes l 
            ON p.id = l.likeableId 
        WHERE l.user.id = :userId
        ORDER BY p.createdAt DESC
    """)
    Page<Photos> findPhotosLikeByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT p.id FROM Photos p")
    List<Long> findAllPhotoIds();

    @Query(
            value = """
                SELECT 
                    p.id AS photoId,
                    p.title AS title,
                    p.description AS description,
                    p.url AS photoUrl,
                    u.id AS creatorId,
                    p.slug as slug,
                    pf.display_name AS creatorName,
                    pf.avatar_url AS ownerAvatar,
                    s.like_count AS likeCount,
                    s.comment_count AS commentCount,
                    (
                        (s.like_count + s.comment_count * 2)
                        / POWER(
                            (EXTRACT(EPOCH FROM (NOW() - p.created_at)) / 3600) + 2,
                            1.5
                        )
                    ) AS score
                FROM photos p
                JOIN photo_stats s ON p.id = s.photo_id
                JOIN users u ON u.id = p.user_id
                LEFT JOIN profiles pf ON u.id = pf.user_id
                WHERE p.status = 'APPROVED'
                  AND p.is_archived = false
                  AND u.status = 'ACTIVE'
                ORDER BY score DESC
            """,
                    countQuery = """
                SELECT COUNT(*)
                FROM photos p
                JOIN photo_stats s ON p.id = s.photo_id
                JOIN users u ON u.id = p.user_id
                WHERE p.status = 'APPROVED'
                  AND p.is_archived = false
                  AND u.status = 'ACTIVE'
            """,
            nativeQuery = true
    )
    Page<PhotoFeedView> findPopularPhotos(Pageable pageable);


    @Query("""
        SELECT p FROM Photos p 
        JOIN FETCH p.user u 
        LEFT JOIN FETCH u.profile prof
        WHERE p.moderationStatus = :photoStatus 
        AND u.status = :userStatus
        ORDER BY p.createdAt DESC
    """)
    Page<Photos> findPendingPhotosByActiveUsers(
            @Param("photoStatus") ModerationStatus photoStatus,
            @Param("userStatus") UserStatus userStatus,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT 
                    p.id AS photoId,
                    p.title AS title,
                    p.description AS description,
                    p.url AS photoUrl,
                    p.slug as slug,
                    u.id AS creatorId,
                    pf.display_name AS creatorName,
                    pf.avatar_url AS ownerAvatar,
        
                    s.like_count AS likeCount,
                    s.comment_count AS commentCount,
        
                    (
                        (s.like_count + s.comment_count * 2)
                        / POWER(
                            (EXTRACT(EPOCH FROM (NOW() - p.created_at)) / 3600) + 2,
                            1.5
                        )
                    ) AS score
        
                FROM photos p
                JOIN photo_stats s ON p.id = s.photo_id
                JOIN users u ON u.id = p.user_id
                LEFT JOIN profiles pf ON u.id = pf.user_id
        
                JOIN follows f 
                    ON f.followed_id = u.id
        
                WHERE f.follower_id = :userId
                  AND f.status = 'ACTIVE'
                  AND p.status = 'APPROVED'
                  AND p.is_archived = false
                  AND u.status = 'ACTIVE'
        
                ORDER BY score DESC
                """,
                    countQuery = """
                SELECT COUNT(*)
                FROM photos p
                JOIN photo_stats s ON p.id = s.photo_id
                JOIN users u ON u.id = p.user_id
                JOIN follows f 
                    ON f.followed_id = u.id
                WHERE f.follower_id = :userId
                  AND f.status = 'ACTIVE'
                  AND p.status = 'APPROVED'
                  AND p.is_archived = false
                  AND u.status = 'ACTIVE'
                """,
            nativeQuery = true
    )
    Page<PhotoFeedView> findFollowedUsersPhotos(
            @Param("userId") int userId,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT 
                    p.id AS photoId,
                    p.title AS title,
                    p.description AS description,
                    p.url AS photoUrl,
                    p.slug as slug,
                    u.id AS creatorId,
                    pf.display_name AS creatorName,
                    pf.avatar_url AS ownerAvatar,
        
                    s.like_count AS likeCount,
                    s.comment_count AS commentCount
        
                FROM photos p
                JOIN photo_stats s ON p.id = s.photo_id
                JOIN users u ON u.id = p.user_id
                LEFT JOIN profiles pf ON u.id = pf.user_id
        
                WHERE p.status = 'APPROVED'
                  AND p.is_archived = false
                  AND u.status = 'ACTIVE'
        
                ORDER BY p.created_at DESC
                """,
                    countQuery = """
                SELECT COUNT(*)
                FROM photos p
                JOIN photo_stats s ON p.id = s.photo_id
                JOIN users u ON u.id = p.user_id
                WHERE p.status = 'APPROVED'
                  AND p.is_archived = false
                  AND u.status = 'ACTIVE'
                """,
            nativeQuery = true
    )
    Page<PhotoFeedView> findLatestPhotos(Pageable pageable);

    @Modifying
    @Query("""
         UPDATE Photos p
            SET p.isArchived = true
            WHERE p.id = :photoId
    """)
    public void softDeletePhotoById(@Param("photoId") long photoId);

    Long countByStatus(PhotoStatus status);
}
