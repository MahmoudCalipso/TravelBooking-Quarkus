package com.travelplatform.domain.model.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a participant in an event.
 * This is part of the Event aggregate.
 */
public class EventParticipant {
    private final UUID id;
    private final UUID eventId;
    private final UUID userId;
    private final LocalDateTime registeredAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private boolean attended;

    /**
     * Creates a new EventParticipant.
     *
     * @param eventId event ID
     * @param userId  user ID who is participating
     * @throws IllegalArgumentException if required fields are null
     */
    public EventParticipant(UUID eventId, UUID userId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.userId = userId;
        this.registeredAt = LocalDateTime.now();
        this.cancelledAt = null;
        this.cancellationReason = null;
        this.attended = false;
    }

    public EventParticipant(UUID id, UUID eventId, UUID userId) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.registeredAt = LocalDateTime.now();
        this.cancelledAt = null;
        this.cancellationReason = null;
        this.attended = false;
    }

    /**
     * Reconstructs an EventParticipant from persistence.
     */
    public EventParticipant(UUID id, UUID eventId, UUID userId, LocalDateTime registeredAt,
            LocalDateTime cancelledAt, String cancellationReason, boolean attended) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.registeredAt = registeredAt;
        this.cancelledAt = cancelledAt;
        this.cancellationReason = cancellationReason;
        this.attended = attended;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public boolean isAttended() {
        return attended;
    }

    /**
     * Cancels the participation.
     *
     * @param reason cancellation reason
     */
    public void cancel(String reason) {
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    /**
     * Marks the participant as attended.
     */
    public void markAsAttended() {
        this.attended = true;
    }

    /**
     * Marks the participant as not attended.
     */
    public void markAsNotAttended() {
        this.attended = false;
    }

    /**
     * Checks if the participation is cancelled.
     *
     * @return true if cancelled
     */
    public boolean isCancelled() {
        return cancelledAt != null;
    }

    /**
     * Checks if the participation is active.
     *
     * @return true if not cancelled
     */
    public boolean isActive() {
        return cancelledAt == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EventParticipant that = (EventParticipant) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("EventParticipant{id=%s, eventId=%s, userId=%s, cancelled=%s}",
                id, eventId, userId, isCancelled());
    }
}
