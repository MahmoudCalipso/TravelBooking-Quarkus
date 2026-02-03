package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.enums.EngagementType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for reel_engagement table.
 * This is persistence model for ReelEngagement domain entity.
 */
@Entity
@Table(name = "reel_engagement", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_reel_engagement", columnNames = {"reel_id", "user_id", "engagement_type"})
       },
       indexes = {
           @Index(name = "idx_reel_engagement_reel_id", columnList = "reel_id"),
           @Index(name = "idx_reel_engagement_user_id", columnList = "user_id"),
           @Index(name = "idx_reel_engagement_type", columnList = "engagement_type")
       })
public class ReelEngagementEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reel_id", nullable = false)
    private UUID reelId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "engagement_type", nullable = false)
    private EngagementType engagementType;

    @Column(name = "watch_duration")
    private Integer watchDuration;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public ReelEngagementEntity() {
    }

    // Constructor for creating new entity
    public ReelEngagementEntity(UUID id, UUID reelId, UUID userId, EngagementType engagementType) {
        this.id = id;
        this.reelId = reelId;
        this.userId = userId;
        this.engagementType = engagementType;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with watch duration
    public ReelEngagementEntity(UUID id, UUID reelId, UUID userId, EngagementType engagementType, Integer watchDuration) {
        this.id = id;
        this.reelId = reelId;
        this.userId = userId;
        this.engagementType = engagementType;
        this.watchDuration = watchDuration;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReelId() {
        return reelId;
    }

    public void setReelId(UUID reelId) {
        this.reelId = reelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public EngagementType getEngagementType() {
        return engagementType;
    }

    public void setEngagementType(EngagementType engagementType) {
        this.engagementType = engagementType;
    }

    public Integer getWatchDuration() {
        return watchDuration;
    }

    public void setWatchDuration(Integer watchDuration) {
        this.watchDuration = watchDuration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
