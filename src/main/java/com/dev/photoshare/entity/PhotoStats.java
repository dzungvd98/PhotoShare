package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "photo_stats", indexes = {
        @Index(name = "idx_trending_likes", columnList = "like_count DESC"),
        @Index(name = "idx_trending_views", columnList = "view_count DESC"),
        @Index(name = "idx_trending_comments", columnList = "comment_count DESC")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoStats {

    @Id
    @Column(name = "photo_id")
    private Long photoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Sử dụng photo.id làm PK
    @JoinColumn(name = "photo_id")
    private Photos photo;

    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer shareCount = 0;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    // Helper methods
    public void incrementLikes() {
        this.likeCount++;
    }

    public void decrementLikes() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementComments() {
        this.commentCount++;
    }

    public void decrementComments() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementViews() {
        this.viewCount++;
    }
}
