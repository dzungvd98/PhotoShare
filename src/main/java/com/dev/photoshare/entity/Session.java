package com.dev.photoshare.entity;

import com.dev.photoshare.utils.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean active;

    private String deviceName;

    private String deviceType;

    private String browser;

    private String operatingSystem;

    private String ipAddress;

    private String userAgent;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private String mfaChallengeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastAccessedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastAccessedAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
        if (status == null) {
            status = SessionStatus.ACTIVE;
        }
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isPending() {
        return status == SessionStatus.PENDING_MFA;
    }
}
