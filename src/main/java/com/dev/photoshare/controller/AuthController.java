package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.*;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.dto.utils.LoginResult;
import com.dev.photoshare.dto.utils.RefreshResult;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.security.refresh.CookieUtils;
import com.dev.photoshare.service.AuditLogService.AuditLogService;
import com.dev.photoshare.service.AuthService.IAuthService;
import com.dev.photoshare.service.RateLimiterService.RateLimiterService;
import com.dev.photoshare.usecase.LoginUseCase;
import com.dev.photoshare.usecase.LogoutUseCase;
import com.dev.photoshare.usecase.RefreshTokenUseCase;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final RefreshTokenUseCase  refreshTokenUseCase;
    private final LogoutUseCase  logoutUseCase;
    private final CookieUtils  cookieUtils;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntityBuilder.created("Tạo tài khoản thành công", response);
    }


    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Void> logout(HttpServletRequest request, @Valid @RequestBody LogoutRequest logoutRequest) {
        int userId = getUserIdFromToken();
        String ipAddress = getClientIpAddress(request);
        logoutUseCase.execute(userId, logoutRequest.getRefreshToken(), ipAddress);
        return ResponseEntityBuilder.noContent();
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIpAddress(httpRequest);
        log.info("Login attempt for username: {} from IP: {}", request.getUsername(), ipAddress);

        try {
            // Check rate limit
            rateLimiterService.checkRateLimit(request.getUsername(), ipAddress);

            // Execute login
            LoginResult result = loginUseCase.execute(request, ipAddress);

            ResponseCookie cookie = cookieUtils.refreshTokenCookie(
                    result.getRefreshToken());

            // Reset rate limit on successful login
            if (!result.getLoginResponse().isRequiresMfa()) {
                rateLimiterService.resetRateLimit(request.getUsername(), ipAddress);
            }

            return ResponseEntityBuilder.okWithHeader(HttpHeaders.SET_COOKIE,  cookie.toString(),"Đăng nhập thành công",  result.getLoginResponse());

        } catch (Exception e) {
            log.error("Login failed for username: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/verify-account")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(@Valid @RequestBody VerifyAccountRequest request) {
        authService.verifyAccount(request.getEmail(), request.getOtp());
        return ResponseEntityBuilder.ok("Tài khoản đã được xác minh");
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIpAddress(httpRequest);
        log.info("Token refresh attempt from IP: {}", ipAddress);

        RefreshResult result = refreshTokenUseCase.execute(refreshToken, ipAddress);

        ResponseCookie cookie = cookieUtils.refreshTokenCookie(
                result.getRefreshToken());
        return ResponseEntityBuilder.okWithHeader(HttpHeaders.SET_COOKIE,  cookie.toString(), result.getRefreshTokenResponse());
    }


    private String getJwtFromRequest(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
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

    private int getUserIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Không có quyền truy cập");
        }

        return userDetails.getId();
    }


}
