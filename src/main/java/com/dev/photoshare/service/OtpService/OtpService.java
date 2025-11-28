package com.dev.photoshare.service.OtpService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OtpService implements IOtpService {
    private final StringRedisTemplate redis;
    private static final SecureRandom RNG = new SecureRandom();
    private static final long OTP_TTL_MINUTES = 5;

    public OtpService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private String generateOtp() {
        int code = RNG.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    public String createOtp(String emailOrPhone) {
        String otp = generateOtp();

        String key = "otp:" + emailOrPhone;   // key redis: otp:email

        redis.opsForValue().set(
                key,
                otp,
                OTP_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        return otp;
    }

    // Xác nhận OTP
    public boolean verifyOtp(String emailOrPhone, String inputOtp) {
        String key = "otp:" + emailOrPhone;

        String stored = redis.opsForValue().get(key);
        if (stored == null) return false;

        boolean ok = stored.equals(inputOtp);

        if (ok) redis.delete(key);

        return ok;
    }
}
