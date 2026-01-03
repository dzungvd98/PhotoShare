package com.dev.photoshare.entity;

import com.dev.photoshare.exception.BusinessException;
import com.dev.photoshare.utils.enums.ModerationStatus;
import com.dev.photoshare.utils.enums.PhotoStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_user_photos", columnList = "user_id, created_at"),
                @Index(name = "idx_photo_status", columnList = "status, moderation_status"),
                @Index(name = "idx_photo_recent", columnList = "created_at")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Photos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhotoStatus status = PhotoStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users moderatedBy;

    private LocalDateTime moderatedAt;

    private String rejectionReason;

    @Builder.Default
    private Boolean isArchived = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private PhotoStats stats;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PhotoTags> photoTags;

    public void validateEditableBy(int userId) {
        if (!this.user.getId().equals(userId)) {
            throw new BusinessException("Chỉ tác giả mới có quyền chỉnh sửa");
        }
    }

    public void validateUpdatableStatus() {
        if (Boolean.TRUE.equals(this.isArchived)) {
            throw new BusinessException("Ảnh đã bị xoá, không thể chỉnh sửa");
        }
    }
}
