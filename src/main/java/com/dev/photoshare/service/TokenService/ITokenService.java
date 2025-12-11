package com.dev.photoshare.service.TokenService;

import io.jsonwebtoken.Claims;

public interface ITokenService {
    String generateAccessToken(Integer userId, String role);
    String generateRefreshToken(Integer userId);
    Claims validateAccessToken(String token);
    Claims validateRefreshToken(String token);
    Integer getUserIdFromToken(Claims claims);
    int getAccessTokenExpiration();
}
