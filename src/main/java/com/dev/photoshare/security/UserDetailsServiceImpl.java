package com.dev.photoshare.security;

import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new UsernameNotFoundException("User account is not active");
        }

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().toUpperCase())
                ))
                .build();
    }

    public UserDetails loadUserById(int userId) throws UsernameNotFoundException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new UsernameNotFoundException("User account is not active");
        }

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().toUpperCase())
                ))
                .enabled(user.getStatus() == UserStatus.ACTIVE)
                .accountNonLocked(!user.isAccountLocked())
                .accountNonExpired(true)
                .credentialsNonExpired(user.getPasswordExpiresAt() == null
                        || user.getPasswordExpiresAt().isAfter(LocalDateTime.now()))
                .build();
    }
}