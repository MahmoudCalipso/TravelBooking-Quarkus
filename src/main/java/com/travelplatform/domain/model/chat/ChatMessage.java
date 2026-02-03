package com.travelplatform.domain.model.chat;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a message in a group chat.
 * This is part of the ChatGroup aggregate.
 */
public class ChatMessage {
    private final UUID id;
    private final UUID chatGroupId;
    private final UUID senderId;
    private final String message;
    private final MessageType messageType;
    private final String attachmentUrl;
    private final LocalDateTime createdAt;

    /**
     * Message type enumeration.
     */
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        LOCATION
    }

    /**
     * Creates a new ChatMessage.
     *
     * @param chatGroupId  chat group ID
     * @param senderId     user ID who sent the message
     * @param message      message content
     * @param messageType   type of message
     * @param attachmentUrl URL of attachment (if any)
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public ChatMessage(UUID chatGroupId, UUID senderId, String message, MessageType messageType, String attachmentUrl) {
        if (chatGroupId == null) {
            throw new IllegalArgumentException("Chat group ID cannot be null");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (messageType == null) {
            throw new IllegalArgumentException("Message type cannot be null");
        }

        this.id = UUID.randomUUID();
        this.chatGroupId = chatGroupId;
        this.senderId = senderId;
        this.message = message;
        this.messageType = messageType;
        this.attachmentUrl = attachmentUrl;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a ChatMessage from persistence.
     */
    public ChatMessage(UUID id, UUID chatGroupId, UUID senderId, String message, MessageType messageType,
                     String attachmentUrl, LocalDateTime createdAt) {
        this.id = id;
        this.chatGroupId = chatGroupId;
        this.senderId = senderId;
        this.message = message;
        this.messageType = messageType;
        this.attachmentUrl = attachmentUrl;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the message is a text message.
     *
     * @return true if message type is TEXT
     */
    public boolean isTextMessage() {
        return messageType == MessageType.TEXT;
    }

    /**
     * Checks if the message is an image.
     *
     * @return true if message type is IMAGE
     */
    public boolean isImageMessage() {
        return messageType == MessageType.IMAGE;
    }

    /**
     * Checks if the message is a file.
     *
     * @return true if message type is FILE
     */
    public boolean isFileMessage() {
        return messageType == MessageType.FILE;
    }

    /**
     * Checks if the message is a location.
     *
     * @return true if message type is LOCATION
     */
    public boolean isLocationMessage() {
        return messageType == MessageType.LOCATION;
    }

    /**
     * Checks if the message has an attachment.
     *
     * @return true if attachment URL is present
     */
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ChatMessage{id=%s, chatGroupId=%s, senderId=%s, messageType=%s}",
                id, chatGroupId, senderId, messageType);
    }
}
