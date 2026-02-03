package com.travelplatform.domain.model.accommodation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing an amenity for an accommodation.
 * Part of the Accommodation aggregate.
 */
public class AccommodationAmenity {
    private final UUID id;
    private final UUID accommodationId;
    private final String amenityName;
    private final AmenityCategory category;
    private final LocalDateTime createdAt;

    /**
     * Creates a new AccommodationAmenity.
     *
     * @param accommodationId accommodation ID
     * @param amenityName     amenity name
     * @param category        amenity category
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public AccommodationAmenity(UUID accommodationId, String amenityName, AmenityCategory category) {
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (amenityName == null || amenityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Amenity name cannot be null or empty");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        this.id = UUID.randomUUID();
        this.accommodationId = accommodationId;
        this.amenityName = amenityName.trim();
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Convenience constructor for Service layer.
     */
    public AccommodationAmenity(UUID id, UUID accommodationId, String amenityName, String categoryName) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.amenityName = amenityName;
        this.category = AmenityCategory.valueOf(categoryName != null ? categoryName : "BASIC");
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs an AccommodationAmenity from persistence.
     */
    public AccommodationAmenity(UUID id, UUID accommodationId, String amenityName,
            AmenityCategory category, LocalDateTime createdAt) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.amenityName = amenityName;
        this.category = category;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public AmenityCategory getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AccommodationAmenity that = (AccommodationAmenity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("AccommodationAmenity{id=%s, name='%s', category=%s}",
                id, amenityName, category);
    }

    /**
     * Enumeration of amenity categories.
     */
    public enum AmenityCategory {
        /**
         * Basic amenities (WiFi, AC, Heating, TV).
         */
        BASIC,

        /**
         * Safety amenities (Smoke detector, Fire extinguisher, First aid kit).
         */
        SAFETY,

        /**
         * Kitchen amenities (Refrigerator, Microwave, Oven, Coffee maker).
         */
        KITCHEN,

        /**
         * Bathroom amenities (Hair dryer, Shampoo, Towels).
         */
        BATHROOM,

        /**
         * Outdoor amenities (Pool, Garden, Balcony, BBQ grill).
         */
        OUTDOOR,

        /**
         * Entertainment amenities (Netflix, Gaming console, Board games).
         */
        ENTERTAINMENT,

        /**
         * Accessibility amenities (Wheelchair accessible, Elevator).
         */
        ACCESSIBILITY,

        /**
         * Parking amenities (Free parking, Paid parking, EV charger).
         */
        PARKING
    }
}
