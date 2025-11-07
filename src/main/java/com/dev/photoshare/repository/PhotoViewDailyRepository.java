package com.dev.photoshare.repository;

import com.dev.photoshare.entity.PhotoViewDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoViewDailyRepository extends JpaRepository<PhotoViewDaily, Long> {
}
