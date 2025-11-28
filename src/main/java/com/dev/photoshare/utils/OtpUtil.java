package com.dev.photoshare.utils;

import java.security.SecureRandom;

public class OtpUtil {
    private static final SecureRandom RNG = new SecureRandom();

    public static String generateOtp() {
        int code = RNG.nextInt(1_000_000); // 0..999999
        return String.format("%06d", code); // đảm bảo 6 chữ số (padding 0)
    }

}
