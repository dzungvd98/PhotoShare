package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Likes;
import com.dev.photoshare.utils.enums.LikeableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByUserIdAndLikeableIdAndLikeableType(int userId, long likeableId, LikeableType likeableType);
}
