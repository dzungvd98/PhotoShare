package com.dev.photoshare.service.AuthService;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.LogoutRequest;
import com.dev.photoshare.dto.request.RefreshTokenRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface IAuthService {
    String register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(HttpServletRequest request, Authentication authentication);
    MessageResponse logout(LogoutRequest request, String accessToken);
    MessageResponse logoutAll(String username);
}
