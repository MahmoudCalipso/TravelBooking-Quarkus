package com.travelplatform.domain.model.review;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user's vote on whether a review is helpful.
 * This is part of the Review aggregate.
 */
public class ReviewHelpful {
    private final UUID id;
    private final UUID reviewId;
    private final UUID userId;
    private final boolean isHelpful;
    private final LocalDateTime createdAt;

    /**
     * Creates a new ReviewHelpful.
     *
     * @param reviewId  review ID
     * @param userId    user ID who voted
     * @param isHelpful true if helpful, false if not helpful
     * @throws IllegalArgumentException if required fields are null
     */
    public ReviewHelpful(UUID reviewId, UUID userId, boolean isHelpful) {
        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        this.id = UUID.randomUUID();
        this.reviewId = reviewId;
        this.userId = userId;
        this.isHelpful = isHelpful;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a ReviewHelpful from persistence.
     */
    public ReviewHelpful(UUID id, UUID reviewId, UUID userId, boolean isHelpful, LocalDateTime createdAt) {
        this.id = id;
        this.reviewId = reviewId;
        this.userId = userId;
        this.isHelpful = isHelpful;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isHelpful() {
        return isHelpful;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the vote is helpful.
     *
     * @return true if helpful
     */
    public boolean isHelpfulVote() {
        return isHelpful;
    }

    /**
     * Checks if the vote is not helpful.
     *
     * @return true if not helpful
     */
    public boolean isNotHelpfulVote() {
        return !isHelpful;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewHelpful that = (ReviewHelpful) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ReviewHelpful{id=%s, reviewId=%s, userId=%s, isHelpful=%s}",
                id, reviewId, userId, isHelpful);
    }
}
