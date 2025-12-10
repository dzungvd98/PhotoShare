package com.dev.photoshare.service.AuditLogService;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.entity.Users;

import java.util.Map;

public interface IAuditLogService {
    void logSuccessfulLogin(Users user, Long sessionId, String ipAddress, LoginRequest.DeviceInfo deviceInfo);
    void logFailedAttempt(String username, Long userId, String ipAddress, String reason, LoginRequest.DeviceInfo deviceInfo);
    void logMfaRequired(Users user, String ipAddress, LoginRequest.DeviceInfo deviceInfo);
    void logEvent(Users user, String eventType, String status, Map<String, Object> details);
}
