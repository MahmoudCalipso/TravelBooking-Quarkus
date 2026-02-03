package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for accommodation_availability table.
 * This is persistence model for accommodation availability calendar.
 */
@Entity
@Table(name = "accommodation_availability", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_accommodation_availability_accommodation_date", columnNames = {"accommodation_id", "date"})
    },
    indexes = {
        @Index(name = "idx_accommodation_availability_accommodation_id", columnList = "accommodation_id"),
        @Index(name = "idx_accommodation_availability_date", columnList = "date")
    })
public class AccommodationAvailabilityEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "accommodation_id", nullable = false)
    private UUID accommodationId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Column(name = "price_override", precision = 10, scale = 2)
    private BigDecimal priceOverride;

    @Column(name = "minimum_nights_override")
    private Integer minimumNightsOverride;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public AccommodationAvailabilityEntity() {
    }

    // Constructor for creating new entity
    public AccommodationAvailabilityEntity(UUID id, UUID accommodationId, LocalDate date) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.date = date;
        this.isAvailable = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callback for updating timestamp
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }

    public Integer getMinimumNightsOverride() {
        return minimumNightsOverride;
    }

    public void setMinimumNightsOverride(Integer minimumNightsOverride) {
        this.minimumNightsOverride = minimumNightsOverride;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
