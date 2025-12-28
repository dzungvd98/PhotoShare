package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "violation_actions",
        indexes = {
                @Index(name = "idx_action_report", columnList = "report_id"),
                @Index(name = "idx_action_admin", columnList = "admin_id"),
                @Index(name = "idx_action_type", columnList = "action_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private ViolationReport report;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private Users admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private ActionType actionType;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum ActionType {
        NO_VIOLATION,     // Report sai
        WARNING,          // Cảnh cáo user
        CONTENT_HIDDEN,   // Ẩn ảnh / comment
        CONTENT_REMOVED,  // Xóa nội dung
        USER_SUSPENDED,   // Khóa tạm user
        USER_BANNED       // Ban vĩnh viễn
    }
}
