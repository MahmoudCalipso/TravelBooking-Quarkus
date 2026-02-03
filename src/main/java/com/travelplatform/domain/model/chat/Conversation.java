package com.travelplatform.domain.model.chat;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a direct message conversation between two users.
 * This is the aggregate root for the direct message aggregate.
 */
public class Conversation {
    private final UUID id;
    private final UUID participant1Id;
    private final UUID participant2Id;
    private LocalDateTime lastMessageAt;
    private int unreadCountP1;
    private int unreadCountP2;
    private final LocalDateTime createdAt;

    /**
     * Creates a new Conversation.
     *
     * @param participant1Id first participant user ID
     * @param participant2Id second participant user ID
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Conversation(UUID participant1Id, UUID participant2Id) {
        if (participant1Id == null) {
            throw new IllegalArgumentException("Participant 1 ID cannot be null");
        }
        if (participant2Id == null) {
            throw new IllegalArgumentException("Participant 2 ID cannot be null");
        }
        if (participant1Id.equals(participant2Id)) {
            throw new IllegalArgumentException("Participants cannot be the same user");
        }

        // Ensure consistent ordering (smaller UUID first)
        if (participant1Id.compareTo(participant2Id) < 0) {
            this.participant1Id = participant1Id;
            this.participant2Id = participant2Id;
        } else {
            this.participant1Id = participant2Id;
            this.participant2Id = participant1Id;
        }

        this.id = UUID.randomUUID();
        this.lastMessageAt = null;
        this.unreadCountP1 = 0;
        this.unreadCountP2 = 0;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a Conversation from persistence.
     */
    public Conversation(UUID id, UUID participant1Id, UUID participant2Id, LocalDateTime lastMessageAt,
                     int unreadCountP1, int unreadCountP2, LocalDateTime createdAt) {
        this.id = id;
        this.participant1Id = participant1Id;
        this.participant2Id = participant2Id;
        this.lastMessageAt = lastMessageAt;
        this.unreadCountP1 = unreadCountP1;
        this.unreadCountP2 = unreadCountP2;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getParticipant1Id() {
        return participant1Id;
    }

    public UUID getParticipant2Id() {
        return participant2Id;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public int getUnreadCountP1() {
        return unreadCountP1;
    }

    public int getUnreadCountP2() {
        return unreadCountP2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Updates the last message timestamp.
     *
     * @param lastMessageAt timestamp of last message
     */
    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        if (lastMessageAt == null) {
            throw new IllegalArgumentException("Last message at cannot be null");
        }
        this.lastMessageAt = lastMessageAt;
    }

    /**
     * Increments the unread count for a participant.
     *
     * @param userId user ID to increment unread count for
     */
    public void incrementUnreadCount(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId.equals(participant1Id)) {
            this.unreadCountP1++;
        } else if (userId.equals(participant2Id)) {
            this.unreadCountP2++;
        } else {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
    }

    /**
     * Marks messages as read for a participant.
     *
     * @param userId user ID to mark as read
     */
    public void markAsRead(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId.equals(participant1Id)) {
            this.unreadCountP1 = 0;
        } else if (userId.equals(participant2Id)) {
            this.unreadCountP2 = 0;
        } else {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
    }

    /**
     * Gets the unread count for a specific user.
     *
     * @param userId user ID
     * @return unread count for the user
     */
    public int getUnreadCount(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId.equals(participant1Id)) {
            return unreadCountP1;
        } else if (userId.equals(participant2Id)) {
            return unreadCountP2;
        } else {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
    }

    /**
     * Checks if a user is a participant in this conversation.
     *
     * @param userId user ID
     * @return true if user is a participant
     */
    public boolean isParticipant(UUID userId) {
        if (userId == null) {
            return false;
        }
        return userId.equals(participant1Id) || userId.equals(participant2Id);
    }

    /**
     * Gets the other participant in the conversation.
     *
     * @param userId current user ID
     * @return the other participant's ID
     */
    public UUID getOtherParticipant(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId.equals(participant1Id)) {
            return participant2Id;
        } else if (userId.equals(participant2Id)) {
            return participant1Id;
        } else {
            throw new IllegalArgumentException("User is not a participant in this conversation");
        }
    }

    /**
     * Checks if the conversation has unread messages for a user.
     *
     * @param userId user ID
     * @return true if there are unread messages
     */
    public boolean hasUnreadMessages(UUID userId) {
        return getUnreadCount(userId) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Conversation{id=%s, participant1Id=%s, participant2Id=%s, unreadCountP1=%d, unreadCountP2=%d}",
                id, participant1Id, participant2Id, unreadCountP1, unreadCountP2);
    }
}
