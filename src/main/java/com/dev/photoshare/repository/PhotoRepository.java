package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Photos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photos,Long> {
    Page<Photos> findAllByUser_IdOrderByUpdatedAtDesc(Integer userId, Pageable pageable);

    @Query("""
        SELECT p FROM Photos p 
        JOIN Likes l 
            ON p.id = l.likeableId 
            AND l.likeableType = com.dev.photoshare.utils.enums.LikeableType.PHOTO
        WHERE l.user.id = :userId
        ORDER BY p.createdAt DESC
    """)
    Page<Photos> findPhotosLikeByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT p.id FROM Photos p")
    List<Long> findAllPhotoIds();

}
