package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for chat_group_members table.
 */
@Entity
@Table(name = "chat_group_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_group_member", columnNames = { "chat_group_id", "user_id" })
}, indexes = {
        @Index(name = "idx_chat_group_members_group", columnList = "chat_group_id"),
        @Index(name = "idx_chat_group_members_user", columnList = "user_id")
})
public class ChatGroupMemberEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "chat_group_id", nullable = false)
    private UUID chatGroupId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role", length = 20, nullable = false)
    private String role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    public ChatGroupMemberEntity() {
    }

    public ChatGroupMemberEntity(UUID id, UUID chatGroupId, UUID userId, String role) {
        this.id = id;
        this.chatGroupId = chatGroupId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(UUID chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
