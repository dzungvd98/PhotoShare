package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Photos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photos,Long> {
    Page<Photos> findAllByUser_IdOrderByUpdatedAtDesc(Integer userId, Pageable pageable);
}
