package com.sharesphere.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_booking", columnList = "booking_id"),
    @Index(name = "idx_reviews_reviewee", columnList = "reviewee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(name = "punctuality_rating")
    private Integer punctualityRating;

    @Column(name = "condition_rating")
    private Integer conditionRating;

    @Column(name = "communication_rating")
    private Integer communicationRating;

    @Column(columnDefinition = "TEXT")
    private String comment;

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
