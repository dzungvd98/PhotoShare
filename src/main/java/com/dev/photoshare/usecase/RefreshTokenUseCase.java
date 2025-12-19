package com.dev.photoshare.usecase;

import com.dev.photoshare.dto.request.RefreshTokenRequest;
import com.dev.photoshare.dto.response.RefreshTokenResponse;
import com.dev.photoshare.entity.Session;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.AuthException;
import com.dev.photoshare.repository.SessionRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.service.AuditLogService.AuditLogService;
import com.dev.photoshare.service.TokenService.ITokenService;
import com.dev.photoshare.utils.enums.SessionStatus;
import com.dev.photoshare.utils.enums.UserStatus;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ITokenService tokenService;
    private final AuditLogService auditLogService;

    @Transactional
    public RefreshTokenResponse execute(RefreshTokenRequest request, String ipAddress) {
        // 1. Validate refresh token format
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new AuthException("INVALID_TOKEN", "Refresh token is required");
        }

        // 2. Verify token signature and expiration
        Claims claims;
        try {
            claims = tokenService.validateRefreshToken(request.getRefreshToken());
        } catch (Exception e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
            throw new AuthException("INVALID_TOKEN", "Invalid or expired refresh token");
        }

        // 3. Find session by refresh token
        Session session = sessionRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("SESSION_NOT_FOUND", "Session not found"));

        // 4. Validate session status
        if (!session.getActive() || session.getStatus() != SessionStatus.ACTIVE) {
            auditLogService.logEvent(
                    session.getUser(),
                    "TOKEN_REFRESH_FAILED",
                    "FAILED",
                    Map.of("reason", "inactive_session")
            );
            throw new AuthException("SESSION_INACTIVE", "Session is no longer active");
        }

        // 5. Check if session is expired
        if (session.isExpired()) {
            sessionRepository.revokeSession(session.getId());
            auditLogService.logEvent(
                    session.getUser(),
                    "TOKEN_REFRESH_FAILED",
                    "FAILED",
                    Map.of("reason", "session_expired")
            );
            throw new AuthException("SESSION_EXPIRED", "Session has expired");
        }

        // 6. Get user and validate account status
        Users user = session.getUser();
        validateUserStatus(user);

        // 7. Generate new tokens
        String role = user.getRole().getRoleName();

        String newAccessToken = tokenService.generateAccessToken(user.getId(), role);
        String newRefreshToken = tokenService.generateRefreshToken(user.getId());

        // 8. Update session with new refresh token
        session.setRefreshToken(newRefreshToken);
        session.setLastAccessedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(30));
        sessionRepository.save(session);

        // 9. Audit log
        auditLogService.logEvent(
                user,
                "TOKEN_REFRESHED",
                "SUCCESS",
                Map.of(
                        "session_id", session.getId(),
                        "ip_address", ipAddress
                )
        );

        log.info("Tokens refreshed for user: {}, session: {}", user.getUsername(), session.getId());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenService.getAccessTokenExpiration())
                .build();
    }

    private void validateUserStatus(Users user) {
        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new AuthException("ACCOUNT_LOCKED", "Account is locked");
        }

        // Check if account is disabled
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AuthException("ACCOUNT_DISABLED", "Account has been disabled");
        }

        // Check if email is verified (optional, depends on requirements)
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new AuthException("EMAIL_NOT_VERIFIED", "Email verification required");
        }

        // Check password expiry
        if (user.isPasswordExpired()) {
            throw new AuthException("PASSWORD_EXPIRED", "Password has expired");
        }
    }
}
