package com.dev.photoshare.service.AuthService;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import com.dev.photoshare.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

public interface IAuthService {
    UserResponse register(RegisterRequest request);
    boolean verifyAccount(String email, String otp);
}
