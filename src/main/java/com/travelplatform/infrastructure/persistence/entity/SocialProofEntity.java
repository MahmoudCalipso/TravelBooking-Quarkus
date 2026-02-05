package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "social_proof")
public class SocialProofEntity {

    @Id
    private UUID accommodationId;

    @Column(nullable = false)
    private int currentViewers;

    @Column(nullable = false)
    private int recentBookings;

    @Column(nullable = false)
    private long lastBookedSeconds;

    @Column(nullable = false)
    private int popularityScore;

    public SocialProofEntity() {
    }

    public SocialProofEntity(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public int getCurrentViewers() {
        return currentViewers;
    }

    public void setCurrentViewers(int currentViewers) {
        this.currentViewers = currentViewers;
    }

    public int getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(int recentBookings) {
        this.recentBookings = recentBookings;
    }

    public long getLastBookedSeconds() {
        return lastBookedSeconds;
    }

    public void setLastBookedSeconds(long lastBookedSeconds) {
        this.lastBookedSeconds = lastBookedSeconds;
    }

    public int getPopularityScore() {
        return popularityScore;
    }

    public void setPopularityScore(int popularityScore) {
        this.popularityScore = popularityScore;
    }

    public Duration lastBookedDuration() {
        return Duration.ofSeconds(lastBookedSeconds);
    }
}
