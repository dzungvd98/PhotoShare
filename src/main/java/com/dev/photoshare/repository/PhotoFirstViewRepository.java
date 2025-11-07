package com.dev.photoshare.repository;

import com.dev.photoshare.entity.PhotoFirstViews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoFirstViewRepository extends JpaRepository<PhotoFirstViews,Long> {
}
