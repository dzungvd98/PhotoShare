package com.dev.photoshare.service.TokenService;

import io.jsonwebtoken.Claims;

public interface ITokenService {
    String generateAccessToken(Integer userId, String role);
    Claims validateAccessToken(String token);
    Integer getUserIdFromToken(Claims claims);
    int getAccessTokenExpiration();
}
