package com.travelplatform.domain.model.accommodation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing an image for an accommodation.
 * Part of the Accommodation aggregate.
 */
public class AccommodationImage {
    private final UUID id;
    private final UUID accommodationId;
    private final String imageUrl;
    private int displayOrder;
    private boolean isPrimary;
    private String caption;
    private final LocalDateTime createdAt;

    /**
     * Creates a new AccommodationImage.
     *
     * @param accommodationId accommodation ID
     * @param imageUrl        image URL
     * @param displayOrder    display order
     * @param isPrimary       whether this is the primary image
     * @param caption         image caption
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public AccommodationImage(UUID accommodationId, String imageUrl, int displayOrder, boolean isPrimary, String caption) {
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (displayOrder < 0) {
            throw new IllegalArgumentException("Display order cannot be negative");
        }

        this.id = UUID.randomUUID();
        this.accommodationId = accommodationId;
        this.imageUrl = imageUrl.trim();
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
        this.caption = caption;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs an AccommodationImage from persistence.
     */
    public AccommodationImage(UUID id, UUID accommodationId, String imageUrl, int displayOrder,
                          boolean isPrimary, String caption, LocalDateTime createdAt) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
        this.caption = caption;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public String getCaption() {
        return caption;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Updates the display order.
     *
     * @param displayOrder new display order
     */
    public void updateDisplayOrder(int displayOrder) {
        if (displayOrder >= 0) {
            this.displayOrder = displayOrder;
        }
    }

    /**
     * Updates the caption.
     *
     * @param caption new caption
     */
    public void updateCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Sets this image as the primary image.
     */
    public void setAsPrimary() {
        this.isPrimary = true;
    }

    /**
     * Removes primary status from this image.
     */
    public void removePrimaryStatus() {
        this.isPrimary = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccommodationImage that = (AccommodationImage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("AccommodationImage{id=%s, accommodationId=%s, isPrimary=%s}",
                id, accommodationId, isPrimary);
    }
}
