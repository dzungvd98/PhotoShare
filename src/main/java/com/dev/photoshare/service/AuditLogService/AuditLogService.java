package com.dev.photoshare.service.AuditLogService;

import com.dev.photoshare.dto.request.LoginRequest;
import com.dev.photoshare.entity.AuditLog;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccessfulLogin(Users user, Long sessionId, String ipAddress,
                                   LoginRequest.DeviceInfo deviceInfo) {
        try {
            Map<String, Object> details = new HashMap<>();
            details.put("session_id", sessionId);
            details.put("device", deviceInfo);

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .username(user.getUsername())
                    .eventType("LOGIN_SUCCESS")
                    .status("SUCCESS")
                    .ipAddress(ipAddress)
                    .userAgent(deviceInfo != null ? deviceInfo.getUserAgent() : null)
                    .deviceInfo(serializeDeviceInfo(deviceInfo))
                    .sessionId(sessionId)
                    .details(objectMapper.writeValueAsString(details))
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Logged successful login for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to log successful login", e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailedAttempt(String username, Long userId, String ipAddress,
                                 String reason, LoginRequest.DeviceInfo deviceInfo) {
        try {
            Map<String, Object> details = new HashMap<>();
            details.put("reason", reason);
            if (deviceInfo != null) {
                details.put("device", deviceInfo);
            }

            AuditLog auditLog = AuditLog.builder()
                    .id(userId)
                    .username(username)
                    .eventType("LOGIN_FAILED")
                    .status("FAILED")
                    .ipAddress(ipAddress)
                    .userAgent(deviceInfo != null ? deviceInfo.getUserAgent() : null)
                    .deviceInfo(serializeDeviceInfo(deviceInfo))
                    .details(objectMapper.writeValueAsString(details))
                    .build();

            auditLogRepository.save(auditLog);
            log.warn("Logged failed login attempt for username: {}, reason: {}", username, reason);
        } catch (Exception e) {
            log.error("Failed to log failed attempt", e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logMfaRequired(Users user, String ipAddress, LoginRequest.DeviceInfo deviceInfo) {
        try {
            Map<String, Object> details = new HashMap<>();
            details.put("mfa_methods", user.getMfaEnabled() ? "TOTP" : "NONE");

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .username(user.getUsername())
                    .eventType("LOGIN_MFA_REQUIRED")
                    .status("PENDING")
                    .ipAddress(ipAddress)
                    .userAgent(deviceInfo != null ? deviceInfo.getUserAgent() : null)
                    .deviceInfo(serializeDeviceInfo(deviceInfo))
                    .details(objectMapper.writeValueAsString(details))
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Logged MFA requirement for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Failed to log MFA requirement", e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(Users user, String eventType, String status, Map<String, Object> details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .username(user.getUsername())
                    .eventType(eventType)
                    .status(status)
                    .details(objectMapper.writeValueAsString(details))
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Logged event: {} for user: {}", eventType, user.getUsername());
        } catch (Exception e) {
            log.error("Failed to log event", e);
        }
    }

    private String serializeDeviceInfo(LoginRequest.DeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(deviceInfo);
        } catch (Exception e) {
            log.error("Failed to serialize device info", e);
            return null;
        }
    }
}
