package com.dev.photoshare.service.RefreshTokenService;

import com.dev.photoshare.entity.RefreshToken;
import com.dev.photoshare.entity.Users;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(Authentication authentication, Users user);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void revokeToken(String token);
    void revokeAllUserTokens(Users user);
    void deleteByToken(String token);
    RefreshToken rotateRefreshToken(RefreshToken oldToken, Authentication authentication, Users user);
    public void cleanupExpiredTokens();
}
