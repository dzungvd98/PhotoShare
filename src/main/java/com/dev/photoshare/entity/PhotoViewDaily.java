package com.dev.photoshare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "photo_view_daily",
        indexes = {
            @Index(name = "idx_photo_daily_views", columnList = "photo_id, view_date")
})
public class PhotoViewDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photos photo;

    private LocalDate viewDate;

    private int uniqueViewers;

    private int totalViews;
}
