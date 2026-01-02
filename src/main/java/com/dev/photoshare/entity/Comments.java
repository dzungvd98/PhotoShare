package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_photo_comments", columnList = "photo_id, created_at"),
                @Index(name = "idx_user_comments", columnList = "user_id, created_at"),
                @Index(name = "idx_reply_comments", columnList = "parent_id")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photos photo;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comments parent; // For nested replies

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comments> replies = new ArrayList<>();

    @Column(nullable = false, length = 1000)
    private String content;

    private boolean deleted = false;

    private int replyCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private Integer updatedBy;
}