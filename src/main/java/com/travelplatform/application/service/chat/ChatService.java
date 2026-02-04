package com.travelplatform.application.service.chat;

import com.travelplatform.application.dto.response.chat.MessageResponse;
import com.travelplatform.application.mapper.ChatMapper;
import com.travelplatform.domain.model.chat.ChatGroup;
import com.travelplatform.domain.model.chat.ChatMessage;
import com.travelplatform.domain.model.chat.Conversation;
import com.travelplatform.domain.model.chat.DirectMessage;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.ChatRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Chat operations.
 * Orchestrates chat-related business workflows.
 */
@ApplicationScoped
public class ChatService {

    @Inject
    ChatRepository chatRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ChatMapper chatMapper;

    /**
     * Create a new chat group.
     */
    @Transactional
    public UUID createChatGroup(UUID userId, String name, String referenceType, UUID referenceId) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create chat group
        ChatGroup chatGroup = new ChatGroup(
                name,
                ChatGroup.ReferenceType.valueOf(referenceType),
                referenceId,
                userId);

        // Save chat group
        chatRepository.saveChatGroup(chatGroup);

        return chatGroup.getId();
    }

    /**
     * Get chat group by ID.
     */
    @Transactional
    public ChatGroup getChatGroupById(UUID chatGroupId) {
        return chatRepository.findChatGroupById(chatGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Chat group not found"));
    }

    /**
     * Get user's chat groups.
     */
    @Transactional
    public List<ChatGroup> getUserChatGroups(UUID userId, int page, int pageSize) {
        return chatRepository.findChatGroupsByUser(userId, page, pageSize);
    }

    /**
     * Send message to chat group.
     */
    @Transactional
    public MessageResponse sendGroupMessage(UUID userId, UUID chatGroupId, String message, String messageType,
            String attachmentUrl) {
        // Verify chat group exists
        ChatGroup chatGroup = chatRepository.findChatGroupById(chatGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Chat group not found"));

        // Create message
        ChatMessage chatMessage = new ChatMessage(
                chatGroupId,
                userId,
                message,
                ChatMessage.MessageType.valueOf(messageType != null ? messageType : "TEXT"),
                attachmentUrl);

        // Save message
        chatRepository.saveChatMessage(chatMessage);

        return chatMapper.toMessageResponse(chatMessage);
    }

    /**
     * Get chat group messages.
     */
    @Transactional
    public List<MessageResponse> getChatGroupMessages(UUID chatGroupId, int page, int pageSize) {
        List<ChatMessage> messages = chatRepository.findChatMessagesByGroup(chatGroupId, page, pageSize);
        return chatMapper.toMessageResponseList(messages);
    }

    /**
     * Add member to chat group.
     */
    @Transactional
    public void addGroupMember(UUID chatGroupId, UUID userId) {
        // Verify chat group exists
        ChatGroup chatGroup = chatRepository.findChatGroupById(chatGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Chat group not found"));

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Add member (implementation depends on your data model)
        // For now, this is a placeholder
    }

    /**
     * Remove member from chat group.
     */
    @Transactional
    public void removeGroupMember(UUID chatGroupId, UUID userId) {
        // Verify chat group exists
        ChatGroup chatGroup = chatRepository.findChatGroupById(chatGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Chat group not found"));

        // Remove member (implementation depends on your data model)
        // For now, this is a placeholder
    }

    /**
     * Delete chat group.
     */
    @Transactional
    public void deleteChatGroup(UUID userId, UUID chatGroupId) {
        ChatGroup chatGroup = chatRepository.findChatGroupById(chatGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Chat group not found"));

        // Verify ownership
        if (!chatGroup.getCreatedBy().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own chat groups");
        }

        // Delete chat group
        chatRepository.deleteChatGroupById(chatGroupId);
    }

    /**
     * Get or create conversation between two users.
     */
    @Transactional
    public UUID getOrCreateConversation(UUID userId1, UUID userId2) {
        // Verify users exist
        userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

        // Check if conversation already exists
        return chatRepository.findConversationByParticipants(userId1, userId2)
                .map(Conversation::getId)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation(userId1, userId2);
                    chatRepository.saveConversation(conversation);
                    return conversation.getId();
                });
    }

    /**
     * Get user's conversations.
     */
    @Transactional
    public List<Conversation> getUserConversations(UUID userId, int page, int pageSize) {
        return chatRepository.findConversationsByUser(userId, page, pageSize);
    }

    /**
     * Send direct message.
     */
    @Transactional
    public MessageResponse sendDirectMessage(UUID senderId, UUID conversationId, String message) {
        // Verify conversation exists
        Conversation conversation = chatRepository.findConversationById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // Verify sender is part of conversation
        if (!conversation.getParticipant1Id().equals(senderId) && !conversation.getParticipant2Id().equals(senderId)) {
            throw new IllegalArgumentException("You are not part of this conversation");
        }

        // Create direct message
        DirectMessage directMessage = new DirectMessage(
                conversationId,
                senderId,
                message);

        // Save message
        chatRepository.saveDirectMessage(directMessage);

        // Update conversation last message time
        conversation.updateLastMessageAt(java.time.LocalDateTime.now());
        chatRepository.saveConversation(conversation);

        // Mark message as read for sender
        directMessage.markAsRead();
        chatRepository.saveDirectMessage(directMessage);

        return chatMapper.toMessageResponse(directMessage);
    }

    /**
     * Get conversation messages.
     */
    @Transactional
    public List<MessageResponse> getConversationMessages(UUID conversationId, int page, int pageSize) {
        List<DirectMessage> messages = chatRepository.findDirectMessagesByConversation(conversationId, page, pageSize);
        return chatMapper.toDirectMessageResponseList(messages);
    }

    /**
     * Mark messages as read.
     */
    @Transactional
    public void markMessagesAsRead(UUID userId, UUID conversationId) {
        // Verify conversation exists
        Conversation conversation = chatRepository.findConversationById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // Verify user is part of conversation
        if (!conversation.getParticipant1Id().equals(userId) && !conversation.getParticipant2Id().equals(userId)) {
            throw new IllegalArgumentException("You are not part of this conversation");
        }

        // Mark all unread messages as read
        List<DirectMessage> unreadMessages = chatRepository.findUnreadDirectMessagesByConversationIdAndRecipient(conversationId, userId);
        for (DirectMessage message : unreadMessages) {
            message.markAsRead();
            chatRepository.saveDirectMessage(message);
        }

        // Reset unread count
        conversation.markAsRead(userId);
        chatRepository.saveConversation(conversation);
    }

    /**
     * Get unread message count.
     */
    @Transactional
    public int getUnreadMessageCount(UUID userId) {
        List<Conversation> conversations = chatRepository.findConversationsByUser(userId, 0, Integer.MAX_VALUE);
        int totalUnread = 0;
        for (Conversation conversation : conversations) {
            totalUnread += conversation.getUnreadCount(userId);
        }
        return totalUnread;
    }

    /**
     * Delete conversation.
     */
    @Transactional
    public void deleteConversation(UUID userId, UUID conversationId) {
        Conversation conversation = chatRepository.findConversationById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // Verify user is part of conversation
        if (!conversation.getParticipant1Id().equals(userId) && !conversation.getParticipant2Id().equals(userId)) {
            throw new IllegalArgumentException("You are not part of this conversation");
        }

        // Delete conversation
        chatRepository.deleteConversationById(conversationId);
    }
}
