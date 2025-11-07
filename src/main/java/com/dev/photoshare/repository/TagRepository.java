package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tags,Long> {
}
