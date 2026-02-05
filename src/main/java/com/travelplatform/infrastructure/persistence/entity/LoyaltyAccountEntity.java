package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccountEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private int points;

    @Column(columnDefinition = "TEXT")
    private String badges; // comma-separated for simplicity

    public LoyaltyAccountEntity() {
    }

    public LoyaltyAccountEntity(UUID id, UUID userId, int points, String badges) {
        this.id = id;
        this.userId = userId;
        this.points = points;
        this.badges = badges;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getBadges() {
        return badges;
    }

    public void setBadges(String badges) {
        this.badges = badges;
    }
}
