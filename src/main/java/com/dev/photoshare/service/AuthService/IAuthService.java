package com.dev.photoshare.service.AuthService;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import org.springframework.security.core.Authentication;

public interface IAuthService {
    String register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken, Authentication authentication);
    MessageResponse logout(String accessToken, String refreshToken);
    MessageResponse logoutAll(String username);
    boolean verifyAccount(String email, String otp);
}
