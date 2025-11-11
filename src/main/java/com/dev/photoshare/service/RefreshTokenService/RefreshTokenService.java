package com.dev.photoshare.service.RefreshTokenService;
import com.dev.photoshare.entity.RefreshToken;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.TokenAlreadyRevokedException;
import com.dev.photoshare.exception.TokenNotFoundException;
import com.dev.photoshare.exception.TokenRefreshException;
import com.dev.photoshare.repository.RefreshTokenRepository;
import com.dev.photoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements IRefreshTokenService {
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // Create refresh token
    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Revoke all existing tokens for this user (optional - for single device login)
        // refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // Find by token
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // Verify expiration
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }

        if (token.getRevoked()) {
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was revoked. Please make a new signin request");
        }

        return token;
    }

    // Revoke token
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new TokenAlreadyRevokedException("Refresh token is already revoked");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    // Revoke all user tokens
    @Transactional
    public void revokeAllUserTokens(Users user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    // Delete token
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        if (oldToken.isExpired() || oldToken.getRevoked()) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }

        // Tạo token mới
        RefreshToken newToken = createRefreshToken(oldToken.getUser().getId());

        // Đánh dấu token cũ là revoked
        oldToken.setRevoked(true);
        oldToken.setReplacedByToken(newToken.getToken());
        refreshTokenRepository.save(oldToken);

        return newToken;
    }

    // Clean up expired tokens (runs daily at midnight)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
        log.info("Completed cleanup of expired refresh tokens");
    }
}
