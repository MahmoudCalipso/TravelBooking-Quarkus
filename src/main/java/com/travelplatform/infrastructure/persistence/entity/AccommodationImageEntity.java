package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for accommodation_images table.
 * This is persistence model for AccommodationImage domain entity.
 */
@Entity
@Table(name = "accommodation_images", indexes = {
        @Index(name = "idx_accommodation_images_accommodation_id", columnList = "accommodation_id"),
        @Index(name = "idx_accommodation_images_order", columnList = "accommodation_id, display_order")
})
public class AccommodationImageEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "accommodation_id", nullable = false)
    private UUID accommodationId;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "caption", length = 255)
    private String caption;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public AccommodationImageEntity() {
    }

    // Constructor for creating new entity
    public AccommodationImageEntity(UUID id, UUID accommodationId, String imageUrl, Integer displayOrder) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.isPrimary = false;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with caption
    public AccommodationImageEntity(UUID id, UUID accommodationId, String imageUrl, Integer displayOrder,
            String caption) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.isPrimary = false;
        this.caption = caption;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
