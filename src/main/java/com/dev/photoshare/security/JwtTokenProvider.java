package com.dev.photoshare.security;

import com.dev.photoshare.utils.enums.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static com.dev.photoshare.utils.enums.TokenType.ACCESS_TOKEN;
import static com.dev.photoshare.utils.enums.TokenType.REFRESH_TOKEN;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret.access}")
    private String accessSecret;

    @Value("${jwt.secret.refresh}")
    private String refreshSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    private Key getSigningKey(TokenType type) {
        String secret = switch (type) {
            case ACCESS_TOKEN -> accessSecret;
            case REFRESH_TOKEN -> refreshSecret;
            default -> throw new IllegalArgumentException("Invalid token type");
        };
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication, TokenType type) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return switch (type) {
            case ACCESS_TOKEN -> generateAccessToken(userDetails);
            case REFRESH_TOKEN -> generateRefreshToken(userDetails);
            default -> throw new IllegalArgumentException("Invalid token type");
        };
    }

    private String generateAccessToken(CustomUserDetails userDetails) {
        log.info("Generating access token for user {}", userDetails.getUsername());
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + accessTokenExpirationMs);

        return Jwts.builder()
                .setHeaderParam("typ", ACCESS_TOKEN.toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("userId", userDetails.getId())
                .claim("roles", userDetails.getAuthorities())
                .signWith(getSigningKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(CustomUserDetails userDetails) {
        log.info("Generating refresh token for user {}", userDetails.getUsername());
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setHeaderParam("typ", REFRESH_TOKEN.toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("userId", userDetails.getId())
                .claim("roles", userDetails.getAuthorities())
                .signWith(getSigningKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenType getTokenType(String token) {
        log.info("Getting token type for token {}", token);
        JwsHeader header = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getHeader();


        String typ = (String) header.get("typ"); // đọc string từ header
        if (typ == null) {
            throw new IllegalArgumentException("Token type not found in JWT header");
        }

        return switch (typ) {
            case "ACCESS_TOKEN" -> ACCESS_TOKEN;
            case "REFRESH_TOKEN" -> REFRESH_TOKEN;
            default -> throw new IllegalArgumentException("Unknown token type: " + typ);
        };
    }

    public String getUsernameFromToken(String token, TokenType type) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(type))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Integer getUserIdFromToken(String token, TokenType type) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey(type)).build()
                .parseClaimsJws(token).getBody();
        return claims.get("userId", Integer.class);
    }

    // Get expiration date from token
    public Date getExpirationDateFromToken(String token, TokenType type) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(type))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }




    // Validate token
    public boolean validateToken(String token, TokenType type) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(type))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}