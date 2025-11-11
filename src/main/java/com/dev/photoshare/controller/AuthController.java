package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.LogoutRequest;
import com.dev.photoshare.dto.request.RefreshTokenRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import com.dev.photoshare.service.AuthService.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthResponse response = authService.refreshToken(request, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody LogoutRequest request,
            HttpServletRequest httpRequest) {

        String accessToken = getJwtFromRequest(httpRequest);
        MessageResponse response = authService.logout(request, accessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices")
    public ResponseEntity<MessageResponse> logoutAll(Authentication authentication) {
        String username = authentication.getName();
        MessageResponse response = authService.logoutAll(username);
        return ResponseEntity.ok(response);
    }

    // Helper method to extract JWT from request
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
