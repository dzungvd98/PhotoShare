package com.dev.photoshare.service.AuthService;


import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import com.dev.photoshare.dto.response.UserResponse;
import com.dev.photoshare.entity.*;
import com.dev.photoshare.exception.*;
import com.dev.photoshare.repository.RoleRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.security.JwtTokenProvider;
import com.dev.photoshare.service.JwtBlackListService.JwtBlacklistService;
import com.dev.photoshare.service.MailService.IMailService;
import com.dev.photoshare.service.OtpService.IOtpService;
import com.dev.photoshare.service.RefreshTokenService.IRefreshTokenService;
import com.dev.photoshare.utils.enums.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

import static com.dev.photoshare.utils.enums.TokenType.ACCESS_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final IRefreshTokenService refreshTokenService;
    private final IMailService  mailService;
    private final IOtpService otpService;

    @Transactional
    public String register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        // Validate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Get default role
        Roles userRole = roleRepository.findByRoleName("user")
                .orElseGet(() -> {
                    Roles newRole = Roles.builder()
                            .roleName("user")
                            .roleDescription("{}")
                            .build();
                    return roleRepository.save(newRole);
                });
        Profiles profile = new Profiles();
        UserStats userStats = new UserStats();

        Users user = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .authProvider("local")
                .status(UserStatus.PENDING_VERIFICATION)
                .failedLoginAttempts(0)
                .role(userRole)
                .profile(profile)
                .userStats(userStats)
                .build();

        profile.setUser(user);
        userStats.setUser(user);

        Users savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());


        mailService.sendSimpleEmail(user.getEmail(), "Verify Account", "Your verify code is: " + otpService.createOtp(user.getEmail()));
        log.info("Email verified is sent: {}", savedUser.getEmail());

        return String.format("User registered successfully: %s", savedUser.getUsername());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Update last login
            Users user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            log.info("User logged in successfully: {}", request.getUsername());

            return generateAuthResponse(authentication, user);

        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken, Authentication authentication) {
        RefreshToken oldToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));

        refreshTokenService.verifyExpiration(oldToken); // nếu expired sẽ throw

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken, authentication, oldToken.getUser());

        // Tạo access token mới
        String newAccessToken = jwtTokenProvider.generateToken(authentication, ACCESS_TOKEN);

        log.info("Token refreshed successfully for user: {}", oldToken.getUser().getUsername());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .user(mapToUserResponse(oldToken.getUser()))
                .build();
    }

    @Transactional
    public MessageResponse logout(String accessToken, String refreshToken) {
        log.info("Add access token to blacklist for user: {}", accessToken);

        // Revoke refresh token
        refreshTokenService.revokeToken(refreshToken);
        log.info("Refresh token revoked");

        return new MessageResponse("Logout successful");
    }

    @Transactional
    public MessageResponse logoutAll(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenService.revokeAllUserTokens(user);
        log.info("All tokens revoked for user: {}", username);

        return new MessageResponse("Logged out from all devices");
    }

    @Override
    public boolean verifyAccount(String email, String otp) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Not found user with email: " + email));

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new BusinessException("Account already verified");
        }

        boolean verified = otpService.verifyOtp(email, otp);

        if (!verified) {
            log.warn("OTP verification failed for email={}", email);
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        userRepository.changeUserStatus(email, UserStatus.ACTIVE);
        log.info("Account verified successfully for email={}", email);

        return true;
    }

    // Helper method to generate auth response
    private AuthResponse generateAuthResponse(Authentication authentication, Users user) {

        String accessToken = jwtTokenProvider.generateToken(authentication, ACCESS_TOKEN);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication, user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(Users user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .status(user.getStatus().toString())
                .roleName(user.getRole().getRoleName())
                .lastLogin(user.getLastLogin())
                .build();
    }

    private void validateUserStatus(Users user) {
        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new AuthException("ACCOUNT_LOCKED", "Account is locked");
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AuthException("ACCOUNT_DISABLED", "Account has been disabled");
        }

        // Check if email is verified (optional, depends on requirements)
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new AuthException("EMAIL_NOT_VERIFIED", "Email verification required");
        }

        // Check password expiry
        if (user.isPasswordExpired()) {
            throw new AuthException("PASSWORD_EXPIRED", "Password has expired");
        }
    }

}