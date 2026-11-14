package com.sharesphere.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "items", indexes = {
    @Index(name = "idx_items_category", columnList = "category"),
    @Index(name = "idx_items_owner_id", columnList = "owner_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 100, nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    @Builder.Default
    private ItemCondition condition = ItemCondition.GOOD;

    @Column(name = "daily_rate", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal dailyRate = BigDecimal.ZERO;

    @Column(name = "security_deposit", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal securityDeposit = BigDecimal.ZERO;

    @Column(name = "is_free", nullable = false)
    @Builder.Default
    private Boolean isFree = false;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(length = 500, nullable = false)
    private String address;

    private Double latitude;

    private Double longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ItemImage> images = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
