package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photo_first_views", indexes = {
        @Index(name = "idx_unique_first_view", columnList = "user_id, photo_id", unique = true),
        @Index(name = "idx_photo_first_views", columnList = "photo_id, viewed_at")

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoFirstViews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photos photo;

    @CreationTimestamp
    private LocalDateTime viewedAt;
}
