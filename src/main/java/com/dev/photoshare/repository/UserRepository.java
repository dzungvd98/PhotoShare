package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Users;
import com.dev.photoshare.utils.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Users> findByUsernameAndStatus(String username, UserStatus status);
}
