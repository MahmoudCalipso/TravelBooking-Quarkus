package com.travelplatform.domain.model.reel;

import com.travelplatform.domain.enums.EngagementType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user's engagement with a reel.
 * Part of the TravelReel aggregate.
 */
public class ReelEngagement {
    private final UUID id;
    private final UUID reelId;
    private final UUID userId;
    private final EngagementType engagementType;
    private Integer watchDuration;
    private final LocalDateTime createdAt;

    /**
     * Creates a new ReelEngagement.
     *
     * @param reelId         reel ID
     * @param userId          user ID who engaged
     * @param engagementType  type of engagement
     * @param watchDuration    how many seconds watched (for VIEW type)
     * @throws IllegalArgumentException if required fields are null
     */
    public ReelEngagement(UUID reelId, UUID userId, EngagementType engagementType, Integer watchDuration) {
        if (reelId == null) {
            throw new IllegalArgumentException("Reel ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (engagementType == null) {
            throw new IllegalArgumentException("Engagement type cannot be null");
        }

        this.id = UUID.randomUUID();
        this.reelId = reelId;
        this.userId = userId;
        this.engagementType = engagementType;
        this.watchDuration = watchDuration;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a ReelEngagement from persistence.
     */
    public ReelEngagement(UUID id, UUID reelId, UUID userId, EngagementType engagementType,
                          Integer watchDuration, LocalDateTime createdAt) {
        this.id = id;
        this.reelId = reelId;
        this.userId = userId;
        this.engagementType = engagementType;
        this.watchDuration = watchDuration;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReelId() {
        return reelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public EngagementType getEngagementType() {
        return engagementType;
    }

    public Integer getWatchDuration() {
        return watchDuration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Updates the watch duration.
     *
     * @param watchDuration new watch duration in seconds
     */
    public void updateWatchDuration(Integer watchDuration) {
        this.watchDuration = watchDuration;
    }

    /**
     * Checks if this is a view engagement.
     *
     * @return true if engagement type is VIEW
     */
    public boolean isView() {
        return this.engagementType == EngagementType.VIEW;
    }

    /**
     * Checks if this is a like engagement.
     *
     * @return true if engagement type is LIKE
     */
    public boolean isLike() {
        return this.engagementType == EngagementType.LIKE;
    }

    /**
     * Checks if this is a share engagement.
     *
     * @return true if engagement type is SHARE
     */
    public boolean isShare() {
        return this.engagementType == EngagementType.SHARE;
    }

    /**
     * Checks if this is a bookmark engagement.
     *
     * @return true if engagement type is BOOKMARK
     */
    public boolean isBookmark() {
        return this.engagementType == EngagementType.BOOKMARK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReelEngagement that = (ReelEngagement) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ReelEngagement{id=%s, reelId=%s, userId=%s, type=%s}",
                id, reelId, userId, engagementType);
    }
}
