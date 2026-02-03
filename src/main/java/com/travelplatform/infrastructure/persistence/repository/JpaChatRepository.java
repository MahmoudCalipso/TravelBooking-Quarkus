package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.model.chat.ChatGroup;
import com.travelplatform.domain.model.chat.ChatMessage;
import com.travelplatform.domain.model.chat.Conversation;
import com.travelplatform.domain.model.chat.DirectMessage;
import com.travelplatform.domain.repository.ChatRepository;
import com.travelplatform.infrastructure.persistence.entity.ChatGroupEntity;
import com.travelplatform.infrastructure.persistence.entity.ChatMessageEntity;
import com.travelplatform.infrastructure.persistence.entity.ConversationEntity;
import com.travelplatform.infrastructure.persistence.entity.DirectMessageEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of ChatRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaChatRepository implements ChatRepository, PanacheRepository<ChatGroupEntity> {

    @Inject
    EntityManager entityManager;

    // ChatGroup methods
    @Override
    @Transactional
    public ChatGroup saveChatGroup(ChatGroup chatGroup) {
        ChatGroupEntity entity = toChatGroupEntity(chatGroup);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toChatGroupDomain(entity);
    }

    @Override
    public Optional<ChatGroup> findChatGroupById(UUID id) {
        ChatGroupEntity entity = entityManager.find(ChatGroupEntity.class, id);
        return entity != null ? Optional.of(toChatGroupDomain(entity)) : Optional.empty();
    }

    @Override
    public List<ChatGroup> findChatGroupsByUserId(UUID userId) {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g WHERE g.createdBy = :userId AND g.isActive = true", ChatGroupEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toChatGroupDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ChatGroup> findChatGroupsByReference(String referenceType, UUID referenceId) {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g WHERE g.referenceType = :referenceType AND g.referenceId = :referenceId", ChatGroupEntity.class);
        query.setParameter("referenceType", referenceType);
        query.setParameter("referenceId", referenceId);
        return query.getResultList().stream()
            .map(this::toChatGroupDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChatGroup(ChatGroup chatGroup) {
        ChatGroupEntity entity = entityManager.find(ChatGroupEntity.class, chatGroup.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    // ChatMessage methods
    @Override
    @Transactional
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        ChatMessageEntity entity = toChatMessageEntity(chatMessage);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toChatMessageDomain(entity);
    }

    @Override
    public List<ChatMessage> findChatMessagesByGroupId(UUID chatGroupId) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId ORDER BY m.createdAt ASC", ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        return query.getResultList().stream()
            .map(this::toChatMessageDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findRecentChatMessages(UUID chatGroupId, int limit) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId ORDER BY m.createdAt DESC", ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toChatMessageDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChatMessage(ChatMessage chatMessage) {
        ChatMessageEntity entity = entityManager.find(ChatMessageEntity.class, chatMessage.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    // Conversation methods
    @Override
    @Transactional
    public Conversation saveConversation(Conversation conversation) {
        ConversationEntity entity = toConversationEntity(conversation);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toConversationDomain(entity);
    }

    @Override
    public Optional<Conversation> findConversationById(UUID id) {
        ConversationEntity entity = entityManager.find(ConversationEntity.class, id);
        return entity != null ? Optional.of(toConversationDomain(entity)) : Optional.empty();
    }

    @Override
    public Optional<Conversation> findConversationByParticipants(UUID participant1Id, UUID participant2Id) {
        // Ensure participant1Id < participant2Id for consistent ordering
        UUID p1 = participant1Id.compareTo(participant2Id) < 0 ? participant1Id : participant2Id;
        UUID p2 = participant1Id.compareTo(participant2Id) < 0 ? participant2Id : participant1Id;
        
        TypedQuery<ConversationEntity> query = entityManager.createQuery(
            "SELECT c FROM ConversationEntity c WHERE c.participant1Id = :p1 AND c.participant2Id = :p2", ConversationEntity.class);
        query.setParameter("p1", p1);
        query.setParameter("p2", p2);
        List<ConversationEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toConversationDomain(results.get(0)));
    }

    @Override
    public List<Conversation> findConversationsByUserId(UUID userId) {
        TypedQuery<ConversationEntity> query = entityManager.createQuery(
            "SELECT c FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId ORDER BY c.lastMessageAt DESC", ConversationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toConversationDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Conversation> findConversationsWithUnreadMessages(UUID userId) {
        TypedQuery<ConversationEntity> query = entityManager.createQuery(
            "SELECT c FROM ConversationEntity c WHERE " +
            "(c.participant1Id = :userId AND c.unreadCountP1 > 0) OR " +
            "(c.participant2Id = :userId AND c.unreadCountP2 > 0) " +
            "ORDER BY c.lastMessageAt DESC", ConversationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toConversationDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteConversation(Conversation conversation) {
        ConversationEntity entity = entityManager.find(ConversationEntity.class, conversation.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    // DirectMessage methods
    @Override
    @Transactional
    public DirectMessage saveDirectMessage(DirectMessage directMessage) {
        DirectMessageEntity entity = toDirectMessageEntity(directMessage);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDirectMessageDomain(entity);
    }

    @Override
    public List<DirectMessage> findDirectMessagesByConversationId(UUID conversationId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdAt ASC", DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList().stream()
            .map(this::toDirectMessageDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findRecentDirectMessages(UUID conversationId, int limit) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdAt DESC", DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDirectMessageDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findUnreadDirectMessages(UUID conversationId, UUID userId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE " +
            "m.conversationId = :conversationId AND " +
            "m.senderId != :userId AND " +
            "m.isRead = false " +
            "ORDER BY m.createdAt ASC", DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDirectMessageDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDirectMessage(DirectMessage directMessage) {
        DirectMessageEntity entity = entityManager.find(DirectMessageEntity.class, directMessage.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public long countUnreadDirectMessages(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM DirectMessageEntity m WHERE " +
            "m.conversationId IN (SELECT c.id FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId) AND " +
            "m.senderId != :userId AND " +
            "m.isRead = false", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
    private ChatGroup toChatGroupDomain(ChatGroupEntity entity) {
        return new ChatGroup(
            entity.getId(),
            entity.getName(),
            entity.getReferenceType(),
            entity.getReferenceId(),
            entity.getCreatedBy(),
            entity.isActive(),
            entity.getCreatedAt()
        );
    }

    private ChatGroupEntity toChatGroupEntity(ChatGroup domain) {
        ChatGroupEntity entity = new ChatGroupEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setReferenceType(domain.getReferenceType());
        entity.setReferenceId(domain.getReferenceId());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private ChatMessage toChatMessageDomain(ChatMessageEntity entity) {
        return new ChatMessage(
            entity.getId(),
            entity.getChatGroupId(),
            entity.getSenderId(),
            entity.getMessage(),
            entity.getMessageType(),
            entity.getAttachmentUrl(),
            entity.getCreatedAt()
        );
    }

    private ChatMessageEntity toChatMessageEntity(ChatMessage domain) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setId(domain.getId());
        entity.setChatGroupId(domain.getChatGroupId());
        entity.setSenderId(domain.getSenderId());
        entity.setMessage(domain.getMessage());
        entity.setMessageType(domain.getMessageType());
        entity.setAttachmentUrl(domain.getAttachmentUrl());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private Conversation toConversationDomain(ConversationEntity entity) {
        return new Conversation(
            entity.getId(),
            entity.getParticipant1Id(),
            entity.getParticipant2Id(),
            entity.getLastMessageAt(),
            entity.getUnreadCountP1(),
            entity.getUnreadCountP2(),
            entity.getCreatedAt()
        );
    }

    private ConversationEntity toConversationEntity(Conversation domain) {
        ConversationEntity entity = new ConversationEntity();
        entity.setId(domain.getId());
        entity.setParticipant1Id(domain.getParticipant1Id());
        entity.setParticipant2Id(domain.getParticipant2Id());
        entity.setLastMessageAt(domain.getLastMessageAt());
        entity.setUnreadCountP1(domain.getUnreadCountP1());
        entity.setUnreadCountP2(domain.getUnreadCountP2());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private DirectMessage toDirectMessageDomain(DirectMessageEntity entity) {
        return new DirectMessage(
            entity.getId(),
            entity.getConversationId(),
            entity.getSenderId(),
            entity.getMessage(),
            entity.isRead(),
            entity.getCreatedAt(),
            entity.getReadAt()
        );
    }

    private DirectMessageEntity toDirectMessageEntity(DirectMessage domain) {
        DirectMessageEntity entity = new DirectMessageEntity();
        entity.setId(domain.getId());
        entity.setConversationId(domain.getConversationId());
        entity.setSenderId(domain.getSenderId());
        entity.setMessage(domain.getMessage());
        entity.setRead(domain.isRead());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setReadAt(domain.getReadAt());
        return entity;
    }
}
