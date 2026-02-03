package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for reel_analytics table.
 * This is persistence model for daily metrics for reels.
 */
@Entity
@Table(name = "reel_analytics", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_reel_analytics_reel_date", columnNames = {"reel_id", "date"})
    },
    indexes = {
        @Index(name = "idx_reel_analytics_reel_id", columnList = "reel_id"),
        @Index(name = "idx_reel_analytics_date", columnList = "date")
    })
public class ReelAnalyticsEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reel_id", nullable = false)
    private UUID reelId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @Column(name = "unique_views", nullable = false)
    private Long uniqueViews = 0L;

    @Column(name = "likes", nullable = false)
    private Integer likes = 0;

    @Column(name = "shares", nullable = false)
    private Integer shares = 0;

    @Column(name = "comments", nullable = false)
    private Integer comments = 0;

    @Column(name = "average_watch_time")
    private Integer averageWatchTime;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate;

    // Default constructor for JPA
    public ReelAnalyticsEntity() {
    }

    // Constructor for creating new entity
    public ReelAnalyticsEntity(UUID id, UUID reelId, LocalDate date) {
        this.id = id;
        this.reelId = reelId;
        this.date = date;
        this.views = 0L;
        this.uniqueViews = 0L;
        this.likes = 0;
        this.shares = 0;
        this.comments = 0;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getUniqueViews() {
        return uniqueViews;
    }

    public void setUniqueViews(Long uniqueViews) {
        this.uniqueViews = uniqueViews;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getAverageWatchTime() {
        return averageWatchTime;
    }

    public void setAverageWatchTime(Integer averageWatchTime) {
        this.averageWatchTime = averageWatchTime;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate) {
        this.completionRate = completionRate;
    }
}
