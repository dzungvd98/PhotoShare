package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByUserIdAndPhotoId(int userId, long photoId);
}
