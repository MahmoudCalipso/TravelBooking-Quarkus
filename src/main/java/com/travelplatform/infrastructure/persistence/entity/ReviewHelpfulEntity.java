package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for review_helpful table.
 * This is persistence model for ReviewHelpful domain entity.
 */
@Entity
@Table(name = "review_helpful", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_review_helpful_review_user", columnNames = {"review_id", "user_id"})
       },
       indexes = {
           @Index(name = "idx_review_helpful_review_id", columnList = "review_id"),
           @Index(name = "idx_review_helpful_user_id", columnList = "user_id")
       })
public class ReviewHelpfulEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "review_id", nullable = false)
    private UUID reviewId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "is_helpful", nullable = false)
    private boolean isHelpful;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public ReviewHelpfulEntity() {
    }

    // Constructor for creating new entity
    public ReviewHelpfulEntity(UUID id, UUID reviewId, UUID userId, boolean isHelpful) {
        this.id = id;
        this.reviewId = reviewId;
        this.userId = userId;
        this.isHelpful = isHelpful;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isHelpful() {
        return isHelpful;
    }

    public void setHelpful(boolean helpful) {
        isHelpful = helpful;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
