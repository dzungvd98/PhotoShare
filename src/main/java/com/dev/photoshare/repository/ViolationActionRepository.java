package com.dev.photoshare.repository;

import com.dev.photoshare.entity.ViolationAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationActionRepository extends JpaRepository<ViolationAction, Long> {
}
