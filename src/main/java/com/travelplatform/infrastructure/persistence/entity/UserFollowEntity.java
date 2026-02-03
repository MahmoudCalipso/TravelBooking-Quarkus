package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for user_follows table.
 * This is persistence model for social following relationships between users.
 */
@Entity
@Table(name = "user_follows", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_follows_follower_following", columnNames = {"follower_id", "following_id"})
    },
    indexes = {
        @Index(name = "idx_user_follows_follower_id", columnList = "follower_id"),
        @Index(name = "idx_user_follows_following_id", columnList = "following_id")
    })
public class UserFollowEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "following_id", nullable = false)
    private UUID followingId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public UserFollowEntity() {
    }

    // Constructor for creating new entity
    public UserFollowEntity(UUID id, UUID followerId, UUID followingId) {
        this.id = id;
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFollowerId() {
        return followerId;
    }

    public void setFollowerId(UUID followerId) {
        this.followerId = followerId;
    }

    public UUID getFollowingId() {
        return followingId;
    }

    public void setFollowingId(UUID followingId) {
        this.followingId = followingId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
