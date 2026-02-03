package com.travelplatform.domain.model.chat;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a group chat.
 * This is the aggregate root for the chat aggregate.
 */
public class ChatGroup {
    private final UUID id;
    private final String name;
    private final ReferenceType referenceType;
    private final UUID referenceId;
    private final UUID createdBy;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;

    /**
     * Reference type enumeration.
     */
    public enum ReferenceType {
        EVENT,
        TRAVEL_PROGRAM,
        CUSTOM
    }

    /**
     * Creates a new ChatGroup.
     *
     * @param name          group name
     * @param referenceType type of reference (EVENT, TRAVEL_PROGRAM, CUSTOM)
     * @param referenceId   ID of the referenced entity
     * @param createdBy     user ID who created the group
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public ChatGroup(String name, ReferenceType referenceType, UUID referenceId, UUID createdBy) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (referenceType == null) {
            throw new IllegalArgumentException("Reference type cannot be null");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.createdBy = createdBy;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a ChatGroup from persistence.
     */
    public ChatGroup(UUID id, String name, ReferenceType referenceType, UUID referenceId,
            UUID createdBy, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.createdBy = createdBy;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the group is linked to an event.
     *
     * @return true if reference type is EVENT
     */
    public boolean isEventGroup() {
        return referenceType == ReferenceType.EVENT;
    }

    /**
     * Checks if the group is linked to a travel program.
     *
     * @return true if reference type is TRAVEL_PROGRAM
     */
    public boolean isTravelProgramGroup() {
        return referenceType == ReferenceType.TRAVEL_PROGRAM;
    }

    /**
     * Checks if the group is custom (not linked to any entity).
     *
     * @return true if reference type is CUSTOM
     */
    public boolean isCustomGroup() {
        return referenceType == ReferenceType.CUSTOM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChatGroup that = (ChatGroup) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ChatGroup{id=%s, name=%s, referenceType=%s, referenceId=%s}",
                id, name, referenceType, referenceId);
    }

    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
}
