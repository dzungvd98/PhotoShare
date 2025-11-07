package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Sessions,Long> {
}
