package com.dev.photoshare.entity;

import com.dev.photoshare.utils.enums.ModerationAction;
import com.dev.photoshare.utils.enums.ModerationTargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "moderation_logs",
        indexes = {
                @Index(name = "idx_moderator_actions", columnList = "moderator_id, created_at"),
                @Index(name = "idx_moderated_content", columnList = "target_type, target_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "moderator_id", nullable = false)
    private Integer moderatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ModerationAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private ModerationTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Integer targetId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}