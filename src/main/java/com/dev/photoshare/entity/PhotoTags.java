package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "photo_tags", indexes = {
        @Index(name = "idx_unique_photo_tag", columnList = "photo_id, tag_id", unique = true),
        @Index(name = "idx_tag_recent_photos", columnList = "tag_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id" , nullable = false)
    private Tags tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id" , nullable = false)
    private Photos photo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
