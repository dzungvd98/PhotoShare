package com.dev.photoshare.entity;

import com.dev.photoshare.utils.enums.FollowStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_follower_followed", columnList = "follower_id, followed_id", unique = true),
                @Index(name = "idx_followed_recent", columnList = "followed_id, created_at"),
                @Index(name = "idx_follower_recent", columnList = "follower_id, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Users follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private Users followed;

    // 'active', 'pending'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStatus status = FollowStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
