package com.dev.photoshare.usecase;

import com.dev.photoshare.entity.Session;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.SessionRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.service.AuditLogService.IAuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCase {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final IAuditLogService auditLogService;

    @Transactional
    public void execute(Integer userId, String refreshToken, String ipAddress) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (refreshToken != null && !refreshToken.isBlank()) {
            // Logout specific session
            Session session = sessionRepository.findByRefreshToken(refreshToken)
                    .orElse(null);

            if (session != null && session.getUser().getId().equals(userId)) {
                sessionRepository.revokeSession(session.getId());

                auditLogService.logEvent(
                        user,
                        "LOGOUT",
                        "SUCCESS",
                        Map.of(
                                "session_id", session.getId(),
                                "ip_address", ipAddress
                        )
                );

                log.info("User {} logged out, session {} revoked", user.getUsername(), session.getId());
            }
        } else {
            // If no refresh token provided, revoke all sessions (optional behavior)
            sessionRepository.revokeAllUserSessions(userId);

            auditLogService.logEvent(
                    user,
                    "LOGOUT_ALL",
                    "SUCCESS",
                    Map.of("ip_address", ipAddress)
            );

            log.info("User {} logged out from all sessions", user.getUsername());
        }
    }
}

