package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_stats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserStats {

    @Id
    @Column(name = "user_id")
    private Integer userId; // Trùng với User.id

    @Column(name = "followers_count", nullable = false)
    @Builder.Default
    private Integer followersCount = 0;

    @Column(name = "following_count", nullable = false)
    @Builder.Default
    private Integer followingCount = 0;

    @Column(name = "post_count", nullable = false)
    @Builder.Default
    private Integer postCount = 0;

    @Column(name = "total_likes_received", nullable = false)
    @Builder.Default
    private Integer totalLikesReceived = 0;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;
}
