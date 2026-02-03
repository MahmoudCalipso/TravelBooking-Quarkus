package com.travelplatform.domain.model.chat;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a direct message between two users.
 * This is part of the Conversation aggregate.
 */
public class DirectMessage {
    private final UUID id;
    private final UUID conversationId;
    private final UUID senderId;
    private final String message;
    private boolean isRead;
    private String attachmentUrl;
    private final LocalDateTime createdAt;
    private LocalDateTime readAt;

    /**
     * Creates a new DirectMessage.
     *
     * @param conversationId conversation ID
     * @param senderId       user ID who sent the message
     * @param message        message content
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public DirectMessage(UUID conversationId, UUID senderId, String message) {
        if (conversationId == null) {
            throw new IllegalArgumentException("Conversation ID cannot be null");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        this.id = UUID.randomUUID();
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.readAt = null;
    }

    /**
     * Reconstructs a DirectMessage from persistence.
     */
    public DirectMessage(UUID id, UUID conversationId, UUID senderId, String message,
            boolean isRead, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
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

    /**
     * Marks the message as read.
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Checks if the message is unread.
     *
     * @return true if message is not read
     */
    public boolean isUnread() {
        return !isRead;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DirectMessage that = (DirectMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("DirectMessage{id=%s, conversationId=%s, senderId=%s, isRead=%s}",
                id, conversationId, senderId, isRead);
    }
}
