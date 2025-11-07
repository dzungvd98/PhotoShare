package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentStatsRepository extends JpaRepository<Comments,Long> {
}
