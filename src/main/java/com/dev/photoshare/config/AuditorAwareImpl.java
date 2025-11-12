package com.dev.photoshare.config;

import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<Users> {

    private final UserRepository userRepository;

    @Override
    public Optional<Users> getCurrentAuditor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof CustomUserDetails customUserDetails) {
            username = customUserDetails.getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            username = springUser.getUsername();
        } else if (principal instanceof String str) {
            username = str;
        } else {
            return Optional.empty();
        }

        return userRepository.findByUsername(username);
    }
}
