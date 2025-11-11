package com.dev.photoshare.service.AuthService;


import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.dto.request.LogoutRequest;
import com.dev.photoshare.dto.request.RefreshTokenRequest;
import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.AuthResponse;
import com.dev.photoshare.dto.response.MessageResponse;
import com.dev.photoshare.dto.response.UserResponse;
import com.dev.photoshare.entity.RefreshToken;
import com.dev.photoshare.entity.Roles;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.EmailAlreadyExistsException;
import com.dev.photoshare.exception.InvalidCredentialsException;
import com.dev.photoshare.exception.TokenRefreshException;
import com.dev.photoshare.exception.UserNotFoundException;
import com.dev.photoshare.repository.RoleRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.security.JwtTokenProvider;
import com.dev.photoshare.service.JwtBlackListService.JwtBlacklistService;
import com.dev.photoshare.service.RefreshTokenService.IRefreshTokenService;
import com.dev.photoshare.utils.enums.UserStatus;
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

import java.time.LocalDateTime;

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
    private final JwtBlacklistService jwtBlacklistService;

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

        // Create new user
        Users user = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .authProvider("local")
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();

        Users savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Auto login and generate tokens
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//        );

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
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken oldToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));

        refreshTokenService.verifyExpiration(oldToken); // nếu expired sẽ throw

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);

        // Tạo access token mới
        String newAccessToken = jwtTokenProvider.generateAccessToken(oldToken.getUser().getUsername());

        log.info("Token refreshed successfully for user: {}", oldToken.getUser().getUsername());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .user(mapToUserResponse(oldToken.getUser()))
                .build();
    }

    @Transactional
    public MessageResponse logout(LogoutRequest request, String accessToken) {
        String refreshToken = request.getRefreshToken();

        jwtBlacklistService.blacklistToken(accessToken);
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

    // Helper method to generate auth response
    private AuthResponse generateAuthResponse(Authentication authentication, Users user) {
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

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
}