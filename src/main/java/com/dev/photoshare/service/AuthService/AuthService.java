package com.dev.photoshare.service.AuthService;


import com.dev.photoshare.dto.request.RegisterRequest;
import com.dev.photoshare.dto.response.UserResponse;
import com.dev.photoshare.entity.*;
import com.dev.photoshare.exception.*;
import com.dev.photoshare.repository.RoleRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.service.MailService.IMailService;
import com.dev.photoshare.service.OtpService.IOtpService;
import com.dev.photoshare.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IMailService  mailService;
    private final IOtpService otpService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Attempting to register email: {}", request.getEmail());

        // Validate username
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Validate email
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
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
        profile.setDisplayName(request.getUsername());
        UserStats userStats = new UserStats();

        Users user = Users.builder()
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
        log.info("User registered successfully: {}", savedUser.getEmail());


        mailService.sendSimpleEmail(user.getEmail(), "Verify Account", "Your verify code is: " + otpService.createOtp(user.getEmail()));
        log.info("Email verified is sent: {}", savedUser.getEmail());

        return UserResponse.builder()
                .email(savedUser.getEmail())
                .roleName(savedUser.getRole().getRoleName().toUpperCase())
                .status(UserStatus.PENDING_VERIFICATION.toString())
                .build();
    }


    @Transactional
    public boolean verifyAccount(String email, String otp) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->  new ResourceNotFoundException(
                        "User",
                        "email",
                        email
                ));

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

}