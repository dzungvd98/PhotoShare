package com.dev.photoshare.service.OtpService;

public interface IOtpService {
    String createOtp(String email);
    boolean verifyOtp(String email, String inputOtp);
}
