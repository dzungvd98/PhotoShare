package com.dev.photoshare.entity;

import com.dev.photoshare.utils.enums.ActionType;
import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReason;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "violation_reports",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"reporter_id", "target_type", "target_id"}
        ),
        indexes = {
                @Index(name = "idx_violation_target", columnList = "target_type, target_id"),
                @Index(name = "idx_violation_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Users reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_person_id", nullable = false)
    private Users reportedPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ViolationReason reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ViolationReportStatus status = ViolationReportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_id")
    private Users mod;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
