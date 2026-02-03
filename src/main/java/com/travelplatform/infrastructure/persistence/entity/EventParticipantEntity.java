package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for event_participants table.
 * This is persistence model for EventParticipant domain entity.
 */
@Entity
@Table(name = "event_participants", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_event_participant", columnNames = {"event_id", "user_id"})
       },
       indexes = {
           @Index(name = "idx_event_participants_event_id", columnList = "event_id"),
           @Index(name = "idx_event_participants_user_id", columnList = "user_id")
       })
public class EventParticipantEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    // Default constructor for JPA
    public EventParticipantEntity() {
    }

    // Constructor for creating new entity
    public EventParticipantEntity(UUID id, UUID eventId, UUID userId) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.registeredAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
