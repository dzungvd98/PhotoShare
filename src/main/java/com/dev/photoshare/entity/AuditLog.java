package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_event_type", columnList = "event_type")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private String username;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String status;

    private String ipAddress;

    private String userAgent;

    private String deviceInfo;

    @Column(columnDefinition = "TEXT")
    private String details;

    private Long sessionId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
