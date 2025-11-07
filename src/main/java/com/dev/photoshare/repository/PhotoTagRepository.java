package com.dev.photoshare.repository;

import com.dev.photoshare.entity.PhotoTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoTagRepository extends JpaRepository<PhotoTags,Long> {
}
