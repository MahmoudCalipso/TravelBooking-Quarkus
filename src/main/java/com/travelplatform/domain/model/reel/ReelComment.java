package com.travelplatform.domain.model.reel;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a comment on a reel.
 * Part of the TravelReel aggregate.
 */
public class ReelComment {
    private final UUID id;
    private final UUID reelId;
    private final UUID userId;
    private UUID parentCommentId;
    private final String content;
    private CommentStatus status;
    private int likeCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates a new ReelComment.
     *
     * @param reelId          reel ID
     * @param userId           user ID who commented
     * @param parentCommentId  parent comment ID for replies (null for top-level comments)
     * @param content           comment text
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public ReelComment(UUID reelId, UUID userId, UUID parentCommentId, String content) {
        if (reelId == null) {
            throw new IllegalArgumentException("Reel ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (content.length() > 300) {
            throw new IllegalArgumentException("Content cannot exceed 300 characters");
        }

        this.id = UUID.randomUUID();
        this.reelId = reelId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.content = content.trim();
        this.status = CommentStatus.VISIBLE;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a ReelComment from persistence.
     */
    public ReelComment(UUID id, UUID reelId, UUID userId, UUID parentCommentId, String content,
                     CommentStatus status, int likeCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.reelId = reelId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.status = status;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public String getContent() {
        return content;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the comment content.
     *
     * @param content new comment text
     */
    public void updateContent(String content) {
        if (content != null && !content.trim().isEmpty() && content.length() <= 300) {
            this.content = content.trim();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Hides the comment (moderation).
     */
    public void hide() {
        this.status = CommentStatus.HIDDEN;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Shows the comment (moderation).
     */
    public void show() {
        this.status = CommentStatus.VISIBLE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Flags the comment for review.
     */
    public void flag() {
        this.status = CommentStatus.FLAGGED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the like count.
     */
    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrements the like count.
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if this is a reply to another comment.
     *
     * @return true if has parent comment
     */
    public boolean isReply() {
        return this.parentCommentId != null;
    }

    /**
     * Checks if this is a top-level comment.
     *
     * @return true if no parent comment
     */
    public boolean isTopLevel() {
        return this.parentCommentId == null;
    }

    /**
     * Checks if the comment is visible.
     *
     * @return true if status is VISIBLE
     */
    public boolean isVisible() {
        return this.status == CommentStatus.VISIBLE;
    }

    /**
     * Checks if the comment is hidden.
     *
     * @return true if status is HIDDEN
     */
    public boolean isHidden() {
        return this.status == CommentStatus.HIDDEN;
    }

    /**
     * Checks if the comment is flagged.
     *
     * @return true if status is FLAGGED
     */
    public boolean isFlagged() {
        return this.status == CommentStatus.FLAGGED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReelComment that = (ReelComment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ReelComment{id=%s, reelId=%s, userId=%s, status=%s}",
                id, reelId, userId, status);
    }

    /**
     * Enumeration of comment statuses.
     */
    public enum CommentStatus {
        VISIBLE,
        HIDDEN,
        FLAGGED
    }
}
