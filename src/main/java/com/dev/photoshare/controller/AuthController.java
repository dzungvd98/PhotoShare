package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.request.VerifyAccountRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.LoginResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import com.dev.photoshare.dto.response.VerifyAccountResponse;
import com.dev.photoshare.service.AuditLogService.AuditLogService;
import com.dev.photoshare.service.AuthService.IAuthService;
import com.dev.photoshare.service.RateLimiterService.RateLimiterService;
import com.dev.photoshare.usecase.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
@Slf4j
public class AuthController {
    private final IAuthService authService;
    private final LoginUseCase loginUseCase;
    private final RateLimiterService rateLimiterService;
    private final AuditLogService auditLogService;

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
        String refreshToken = getJwtFromRequest(request, "Authorization-Refresh");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthResponse response = authService.refreshToken(refreshToken, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        String refreshToken = getJwtFromRequest(request, "Authorization-Refresh");
        String accessToken = getJwtFromRequest(request, "Authorization-Access");
        MessageResponse response = authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices")
    public ResponseEntity<MessageResponse> logoutAll(Authentication authentication) {
        String username = authentication.getName();
        MessageResponse response = authService.logoutAll(username);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/loginn")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIpAddress(httpRequest);
        log.info("Login attempt for username: {} from IP: {}", request.getUsername(), ipAddress);

        try {
            // Check rate limit
            rateLimiterService.checkRateLimit(request.getUsername(), ipAddress);

            // Execute login
            LoginResponse response = loginUseCase.execute(request, ipAddress);

            // Reset rate limit on successful login
            if (!response.isRequiresMfa()) {
                rateLimiterService.resetRateLimit(request.getUsername(), ipAddress);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for username: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/verify-account")
    public ResponseEntity<VerifyAccountResponse> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        authService.verifyAccount(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(new VerifyAccountResponse("Account verified successfully", LocalDateTime.now()));
    }

    /**
     * Extracts the real client IP address from the request
     * Handles proxy headers like X-Forwarded-For
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }


    private String getJwtFromRequest(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
