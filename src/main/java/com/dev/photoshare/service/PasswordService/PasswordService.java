package com.dev.photoshare.service.PasswordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordService implements IPasswordService{
    private final PasswordEncoder passwordEncoder;
    private static final int BCRYPT_STRENGTH = 12;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public boolean verify(String plainPassword, String hashedPassword) {
        try {
            return passwordEncoder.matches(plainPassword, hashedPassword);
        } catch (Exception e) {
            log.error("Error verifying password", e);
            return false;
        }
    }

    public void hashDummy() {
        passwordEncoder.encode("dummy-password-for-timing-attack-prevention");
    }

    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

}
