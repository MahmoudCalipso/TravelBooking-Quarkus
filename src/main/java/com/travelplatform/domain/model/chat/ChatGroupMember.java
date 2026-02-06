package com.travelplatform.domain.model.chat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a member of a chat group.
 */
public class ChatGroupMember {
    private final UUID id;
    private final UUID chatGroupId;
    private final UUID userId;
    private final Role role;
    private final LocalDateTime joinedAt;

    public enum Role {
        OWNER,
        ADMIN,
        MEMBER
    }

    public ChatGroupMember(UUID chatGroupId, UUID userId, Role role) {
        this.id = UUID.randomUUID();
        this.chatGroupId = chatGroupId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    public ChatGroupMember(UUID id, UUID chatGroupId, UUID userId, Role role, LocalDateTime joinedAt) {
        this.id = id;
        this.chatGroupId = chatGroupId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChatGroupId() {
        return chatGroupId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public boolean canManageMembers() {
        return role == Role.OWNER || role == Role.ADMIN;
    }

    public boolean canDeleteGroup() {
        return role == Role.OWNER;
    }
}
