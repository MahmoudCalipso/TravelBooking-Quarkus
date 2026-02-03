package com.travelplatform.domain.model.notification;

import com.travelplatform.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a system notification.
 * Notifications are sent to users for various platform events.
 */
public class Notification {

    private final UUID id;
    private final UUID userId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private String entityType;
    private UUID entityId;
    private String actionUrl;
    private boolean isRead;
    private final LocalDateTime createdAt;
    private LocalDateTime readAt;

    /**
     * Creates a new notification.
     *
     * @param id         notification ID
     * @param userId     user ID to receive notification
     * @param type       notification type
     * @param title      notification title
     * @param message    notification message
     * @param entityType related entity type (optional)
     * @param entityId   related entity ID (optional)
     * @param actionUrl  action URL (optional)
     */
    public Notification(UUID id, UUID userId, NotificationType type, String title, String message,
            String entityType, UUID entityId, String actionUrl) {
        if (id == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification title cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be null or empty");
        }

        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.entityType = entityType;
        this.entityId = entityId;
        this.actionUrl = actionUrl;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.readAt = null;
    }

    public Notification(UUID id, UUID userId, NotificationType type, String title, String message) {
        this(id, userId, type, title, message, null, null, null);
    }

    /**
     * Reconstructs a notification from persistence.
     *
     * @param id         notification ID
     * @param userId     user ID
     * @param type       notification type
     * @param title      notification title
     * @param message    notification message
     * @param entityType related entity type
     * @param entityId   related entity ID
     * @param actionUrl  action URL
     * @param isRead     read status
     * @param createdAt  creation timestamp
     * @param readAt     read timestamp
     */
    public Notification(UUID id, UUID userId, NotificationType type, String title, String message,
            String entityType, UUID entityId, String actionUrl, boolean isRead,
            LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.entityType = entityType;
        this.entityId = entityId;
        this.actionUrl = actionUrl;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    /**
     * Marks the notification as read.
     */
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Marks the notification as unread.
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setRelatedEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setRelatedEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    // Setters for reconstruction

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
