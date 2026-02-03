package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for accommodation_amenities table.
 * This is persistence model for AccommodationAmenity domain entity.
 */
@Entity
@Table(name = "accommodation_amenities", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_accommodation_amenity", columnNames = {"accommodation_id", "amenity_name"})
       },
       indexes = {
           @Index(name = "idx_accommodation_amenities_accommodation_id", columnList = "accommodation_id")
       })
public class AccommodationAmenityEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "accommodation_id", nullable = false)
    private UUID accommodationId;

    @Column(name = "amenity_name", nullable = false, length = 100)
    private String amenityName;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public AccommodationAmenityEntity() {
    }

    // Constructor for creating new entity
    public AccommodationAmenityEntity(UUID id, UUID accommodationId, String amenityName, String category) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.amenityName = amenityName;
        this.category = category;
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

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
