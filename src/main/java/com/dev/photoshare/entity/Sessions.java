package com.dev.photoshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions", indexes = {
        @Index(name = "idx_user_sessions", columnList = "user_id, last_activity")
})
public class Sessions   {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @NotNull(message = "Token not provide")
    @Column(unique = true)
    private String token;

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    private String userAgent;

    @CreationTimestamp
    private LocalDateTime lastActivity;

    private LocalDateTime expiresAt;
}
