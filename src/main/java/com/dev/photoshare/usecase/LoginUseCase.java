package com.dev.photoshare.usecase;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.response.LoginResponse;
import com.dev.photoshare.entity.Session;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.*;
import com.dev.photoshare.repository.SessionRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.service.AuditLogService.IAuditLogService;
import com.dev.photoshare.service.PasswordService.IPasswordService;
import com.dev.photoshare.service.TokenService.ITokenService;
import com.dev.photoshare.utils.enums.SessionStatus;
import com.dev.photoshare.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final IPasswordService passwordService;
    private final ITokenService tokenService;
    private final IAuditLogService auditLogService;

    @Value("${auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${auth.lock-duration-minutes:30}")
    private int lockDurationMinutes;

    @Value("${auth.password-expiry-days:90}")
    private int passwordExpiryDays;

    @Transactional
    public LoginResponse execute(LoginRequest request, String ipAddress) {
        // 1. Validate Input
        validateInput(request);

        // 2. Find User
        Users user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            // Prevent timing attacks
            passwordService.hashDummy();
            auditLogService.logFailedAttempt(
                    request.getUsername(), null, ipAddress,
                    "USER_NOT_FOUND", request.getDeviceInfo()
            );
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // 3. Check Account Status
        checkAccountStatus(user, ipAddress, request.getDeviceInfo());

        // 4. Verify Password
        if (!passwordService.verify(request.getPassword(), user.getPassword())) {
            handleInvalidPassword(user, ipAddress, request.getDeviceInfo());
        }

        // 5. Check Password Expiry
        checkPasswordExpiry(user);

        // 6. Reset Failed Attempts
        userRepository.resetFailedAttempts(user.getId(), LocalDateTime.now());

        // 7. Check MFA Requirement
        if (user.getMfaEnabled()) {
            return handleMfaRequired(user, ipAddress, request.getDeviceInfo());
        }

        // 8. Generate Tokens and Create Session
        return handleSuccessfulLogin(user, ipAddress, request.getDeviceInfo());
    }

    private void validateInput(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ValidationException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Password is required");
        }
    }

    private void checkAccountStatus(Users user, String ipAddress,
                                    LoginRequest.DeviceInfo deviceInfo) {
        // Check if account is locked
        if (user.isAccountLocked()) {
            auditLogService.logFailedAttempt(
                    user.getUsername(), user.getId(), ipAddress,
                    "ACCOUNT_LOCKED", deviceInfo
            );
            throw new AccountLockedException(
                    user.getLockedUntil(),
                    "TOO_MANY_ATTEMPTS"
            );
        }

        // Check if account is disabled
        if (user.getStatus() == UserStatus.DISABLED) {
            auditLogService.logFailedAttempt(
                    user.getUsername(), user.getId(), ipAddress,
                    "ACCOUNT_DISABLED", deviceInfo
            );
            throw new AccountDisabledException("Your account has been disabled");
        }

        // Check is account verify
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            auditLogService.logFailedAttempt(
                    user.getUsername(), user.getId(), ipAddress,
                    "ACCOUNT_NOT_VERIFIED", deviceInfo
            );
            throw new AccountNotVerifiedException(
                    "Please verify your email address before logging in",
                    "/api/auth/resend-verification"
            );
        }
    }

    private void handleInvalidPassword(Users user, String ipAddress,
                                       LoginRequest.DeviceInfo deviceInfo) {
        // Increment failed attempts
        userRepository.incrementFailedAttempts(user.getId(), LocalDateTime.now());
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        // Check if should lock account
        if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
            userRepository.lockAccount(user.getId(), lockUntil, LocalDateTime.now());

            auditLogService.logFailedAttempt(
                    user.getUsername(), user.getId(), ipAddress,
                    "INVALID_PASSWORD_LOCKED", deviceInfo
            );

            throw new AccountLockedException(lockUntil, "TOO_MANY_ATTEMPTS");
        }

        int attemptsRemaining = maxFailedAttempts - user.getFailedLoginAttempts();
        auditLogService.logFailedAttempt(
                user.getUsername(), user.getId(), ipAddress,
                "INVALID_PASSWORD", deviceInfo
        );


        throw new InvalidCredentialsException(
                "Invalid username or password",
                attemptsRemaining
        );
    }

    private void checkPasswordExpiry(Users user) {
        if (user.isPasswordExpired()) {
            auditLogService.logEvent(
                    user, "PASSWORD_EXPIRED", "WARNING",
                    Map.of("password_changed_at", user.getPasswordChangedAt())
            );
            throw new PasswordExpiredException(
                    "Your password has expired. Please reset it.",
                    "/api/auth/reset-password"
            );
        }
    }

    private LoginResponse handleMfaRequired(Users user, String ipAddress,
                                            LoginRequest.DeviceInfo deviceInfo) {
        // Create pending session
        String mfaChallengeId = java.util.UUID.randomUUID().toString();
        String tempToken = tokenService.generateAccessToken(
                user.getId(),
                Set.of("ROLE_MFA_PENDING").toString()
        );

        Session pendingSession = Session.builder()
                .user(user)
                .refreshToken(tempToken)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .status(SessionStatus.PENDING_MFA)
                .mfaChallengeId(mfaChallengeId)
                .ipAddress(ipAddress)
                .deviceName(deviceInfo != null ? deviceInfo.getDeviceName() : null)
                .deviceType(deviceInfo != null ? deviceInfo.getDeviceType() : null)
                .browser(deviceInfo != null ? deviceInfo.getBrowser() : null)
                .operatingSystem(deviceInfo != null ? deviceInfo.getOperatingSystem() : null)
                .userAgent(deviceInfo != null ? deviceInfo.getUserAgent() : null)
                .build();

        sessionRepository.save(pendingSession);

        auditLogService.logMfaRequired(user, ipAddress, deviceInfo);

        return LoginResponse.builder()
                .requiresMfa(true)
                .sessionToken(tempToken)
                .mfaMethods(List.of("TOTP", "SMS"))
                .build();
    }

    private LoginResponse handleSuccessfulLogin(Users user, String ipAddress,
                                                LoginRequest.DeviceInfo deviceInfo) {
        // Generate tokens
        String role = user.getRole().getRoleName();

        String accessToken = tokenService.generateAccessToken(user.getId(), role);
        String refreshToken = tokenService.generateRefreshToken(user.getId());

        // Create session
        Session session = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .status(SessionStatus.ACTIVE)
                .ipAddress(ipAddress)
                .deviceName(deviceInfo != null ? deviceInfo.getDeviceName() : null)
                .deviceType(deviceInfo != null ? deviceInfo.getDeviceType() : null)
                .browser(deviceInfo != null ? deviceInfo.getBrowser() : null)
                .operatingSystem(deviceInfo != null ? deviceInfo.getOperatingSystem() : null)
                .userAgent(deviceInfo != null ? deviceInfo.getUserAgent() : null)
                .build();

        sessionRepository.save(session);

        // Update last login
        userRepository.updateLastLogin(
                user.getId(),
                LocalDateTime.now(),
                ipAddress,
                LocalDateTime.now()
        );

        // Audit log
        auditLogService.logSuccessfulLogin(user, session.getId(), ipAddress, deviceInfo);

        // Check for new device/location (simplified)
        checkSecurityAlerts(user, ipAddress, deviceInfo);

        // Build response
        return LoginResponse.builder()
                .requiresMfa(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenService.getAccessTokenExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getUsername())
                        .role(role)
                        .lastLoginAt(user.getLastLogin())
                        .build())
                .build();
    }

    private void checkSecurityAlerts(Users user, String ipAddress,
                                     LoginRequest.DeviceInfo deviceInfo) {
        // Check if this is a new IP or device
        boolean isNewDevice = user.getLastLoginIp() != null &&
                !user.getLastLoginIp().equals(ipAddress);

        if (isNewDevice) {
            log.info("New device/location detected for user: {}", user.getUsername());
            // Here you would send a security notification email/push
            // emailService.sendSecurityAlert(user, ipAddress, deviceInfo);
        }
    }
}
