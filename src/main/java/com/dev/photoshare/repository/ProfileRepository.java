package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profiles,Integer> {
    Optional<Profiles> findByUserId(Integer userId);


}
