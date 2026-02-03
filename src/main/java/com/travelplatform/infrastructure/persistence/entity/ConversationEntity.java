package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for conversations table.
 * This is persistence model for Conversation domain entity.
 */
@Entity
@Table(name = "conversations", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_conversation_participants", columnNames = {"participant1_id", "participant2_id"})
       },
       indexes = {
           @Index(name = "idx_conversations_participant1", columnList = "participant1_id"),
           @Index(name = "idx_conversations_participant2", columnList = "participant2_id"),
           @Index(name = "idx_conversations_last_message", columnList = "last_message_at DESC")
       })
public class ConversationEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "participant1_id", nullable = false)
    private UUID participant1Id;

    @Column(name = "participant2_id", nullable = false)
    private UUID participant2Id;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "unread_count_p1")
    private Integer unreadCountP1 = 0;

    @Column(name = "unread_count_p2")
    private Integer unreadCountP2 = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public ConversationEntity() {
    }

    // Constructor for creating new entity
    public ConversationEntity(UUID id, UUID participant1Id, UUID participant2Id) {
        this.id = id;
        this.participant1Id = participant1Id;
        this.participant2Id = participant2Id;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParticipant1Id() {
        return participant1Id;
    }

    public void setParticipant1Id(UUID participant1Id) {
        this.participant1Id = participant1Id;
    }

    public UUID getParticipant2Id() {
        return participant2Id;
    }

    public void setParticipant2Id(UUID participant2Id) {
        this.participant2Id = participant2Id;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public Integer getUnreadCountP1() {
        return unreadCountP1;
    }

    public void setUnreadCountP1(Integer unreadCountP1) {
        this.unreadCountP1 = unreadCountP1;
    }

    public Integer getUnreadCountP2() {
        return unreadCountP2;
    }

    public void setUnreadCountP2(Integer unreadCountP2) {
        this.unreadCountP2 = unreadCountP2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
