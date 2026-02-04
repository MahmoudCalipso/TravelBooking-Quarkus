package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.model.reel.ReelComment;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for reel_comments table.
 * This is persistence model for ReelComment domain entity.
 */
@Entity
@Table(name = "reel_comments", indexes = {
    @Index(name = "idx_reel_comments_reel_id", columnList = "reel_id"),
    @Index(name = "idx_reel_comments_parent_id", columnList = "parent_comment_id"),
    @Index(name = "idx_reel_comments_user_id", columnList = "user_id"),
    @Index(name = "idx_reel_comments_created_at", columnList = "created_at DESC")
})
public class ReelCommentEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reel_id", nullable = false)
    private UUID reelId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @Column(name = "content", nullable = false, length = 300)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReelComment.CommentStatus status = ReelComment.CommentStatus.VISIBLE;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public ReelCommentEntity() {
    }

    // Constructor for creating new entity
    public ReelCommentEntity(UUID id, UUID reelId, UUID userId, String content) {
        this.id = id;
        this.reelId = reelId;
        this.userId = userId;
        this.content = content;
        this.status = ReelComment.CommentStatus.VISIBLE;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with parent comment (for replies)
    public ReelCommentEntity(UUID id, UUID reelId, UUID userId, UUID parentCommentId, String content) {
        this.id = id;
        this.reelId = reelId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.status = ReelComment.CommentStatus.VISIBLE;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callback for updating timestamp
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ReelComment.CommentStatus getStatus() {
        return status;
    }

    public void setStatus(ReelComment.CommentStatus status) {
        this.status = status;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
