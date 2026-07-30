package com.hoagiayphudong.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Setter
    private Category category;

    @Column(nullable = false, length = 180)
    @Setter
    private String name;

    @Column(nullable = false, unique = true, length = 220)
    @Setter
    private String slug;

    @Column(length = 40)
    @Setter
    private String code;

    @Column(name = "short_description", columnDefinition = "TEXT")
    @Setter
    private String shortDescription;

    @Column(name = "price_amount", precision = 12, scale = 2)
    @Setter
    private BigDecimal priceAmount;

    @Column(name = "price_label", nullable = false, length = 80)
    @Setter
    private String priceLabel = "Liên hệ";

    @Column(name = "color_family", length = 80)
    @Setter
    private String colorFamily;

    @Column(name = "height_cm")
    @Setter
    private Integer heightCm;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_status", nullable = false, length = 40)
    @Setter
    private InventoryStatus inventoryStatus = InventoryStatus.AVAILABLE;

    @Column(name = "thumbnail_url", length = 500)
    @Setter
    private String thumbnailUrl;

    @Column(nullable = false)
    @Setter
    private Boolean featured = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void beforeCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = OffsetDateTime.now();
    }

}
