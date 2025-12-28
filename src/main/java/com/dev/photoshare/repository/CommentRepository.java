package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comments,Long> {
    Optional<Comments> findById(long id);
}
