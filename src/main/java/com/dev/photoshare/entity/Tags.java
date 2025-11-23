package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "tags",
        indexes = {
                @Index(name = "idx_popular_tags", columnList = "usage_count")
        }
)
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String tagName;

    @Column(unique = true, length = 200)
    private String slug;

    private long usageCount  = 0L;

    @Builder.Default
    private boolean isTrending = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tags", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PhotoTags> photoTags;

    public void incrementUsage() {
        this.usageCount++;
    }

    public void decrementUsage() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }


}
