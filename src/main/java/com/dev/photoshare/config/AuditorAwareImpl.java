package com.dev.photoshare.config;

import com.dev.photoshare.entity.Users;
import com.dev.photoshare.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<Users> {

    @Override
    public Optional<Users> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // ✅ Chỉ lấy userId từ CustomUserDetails, KHÔNG query database
        if (principal instanceof CustomUserDetails customUserDetails) {
            Integer userId = customUserDetails.getId();
            if (userId != null) {
                // Tạo Users entity với chỉ ID, Hibernate sẽ tự động reference
                return Optional.of(new Users(userId));
            }
        }

        return Optional.empty();
    }
}