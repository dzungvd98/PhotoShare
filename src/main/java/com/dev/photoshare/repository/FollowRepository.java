package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follows,Long> {
}
