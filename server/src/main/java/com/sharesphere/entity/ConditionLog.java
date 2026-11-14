package com.sharesphere.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "condition_logs", indexes = {
    @Index(name = "idx_condition_booking", columnList = "booking_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConditionLog {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "logged_by_id", nullable = false)
    private User loggedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionStage stage;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "image_urls", columnDefinition = "JSON", nullable = false)
    private String imageUrls;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
