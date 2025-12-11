package com.dev.photoshare.service.TokenService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class TokenService implements ITokenService {

    private final SecretKey accessTokenSecret;
    private final SecretKey refreshTokenSecret;

    @Value("${jwt.access-token-expiration}")// 1 hour default
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}") // 30 days default
    private long refreshTokenExpiration;

    @Value("${jwt.issuer:auth-service}")
    private String issuer;

    public TokenService(
            @Value("${jwt.secret.access}") String accessSecret,
            @Value("${jwt.secret.refresh}") String refreshSecret) {
        this.accessTokenSecret = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenSecret = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an access token with user ID and roles
     */
    public String generateAccessToken(Integer userId, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("roles", role)
                .claim("type", "access")
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .setId(UUID.randomUUID().toString())
                .signWith(accessTokenSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generates a refresh token
     */
    public String generateRefreshToken(Integer userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .setId(UUID.randomUUID().toString())
                .signWith(refreshTokenSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates and parses an access token
     */
    public Claims validateAccessToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Access token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("Invalid access token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validates and parses a refresh token
     */
    public Claims validateRefreshToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(refreshTokenSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts user ID from token
     */
    public Integer getUserIdFromToken(Claims claims) {
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * Gets access token expiration time in seconds
     */
    public int getAccessTokenExpiration() {
        return (int) accessTokenExpiration;
    }
}