package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profiles,Integer> {
}
