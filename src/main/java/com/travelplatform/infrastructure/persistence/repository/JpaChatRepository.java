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
public class JpaChatRepository implements ChatRepository {

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
    @Transactional
    public ChatGroup updateChatGroup(ChatGroup chatGroup) {
        return saveChatGroup(chatGroup);
    }

    @Override
    @Transactional
    public void deleteChatGroupById(UUID id) {
        ChatGroupEntity entity = entityManager.find(ChatGroupEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<ChatGroup> findChatGroupById(UUID id) {
        ChatGroupEntity entity = entityManager.find(ChatGroupEntity.class, id);
        return entity != null ? Optional.of(toChatGroupDomain(entity)) : Optional.empty();
    }

    @Override
    public List<ChatGroup> findAllChatGroups() {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g", ChatGroupEntity.class);
        return query.getResultList().stream().map(this::toChatGroupDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatGroup> findChatGroupsByCreatorId(UUID creatorId) {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g WHERE g.createdBy = :creatorId", ChatGroupEntity.class);
        query.setParameter("creatorId", creatorId);
        return query.getResultList().stream().map(this::toChatGroupDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatGroup> findChatGroupsByCreatorIdPaginated(UUID creatorId, int page, int pageSize) {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g WHERE g.createdBy = :creatorId", ChatGroupEntity.class);
        query.setParameter("creatorId", creatorId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toChatGroupDomain).collect(Collectors.toList());
    }

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
    public List<ChatGroup> findActiveChatGroups() {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g WHERE g.isActive = true", ChatGroupEntity.class);
        return query.getResultList().stream().map(this::toChatGroupDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatGroup> findInactiveChatGroups() {
        TypedQuery<ChatGroupEntity> query = entityManager.createQuery(
            "SELECT g FROM ChatGroupEntity g WHERE g.isActive = false", ChatGroupEntity.class);
        return query.getResultList().stream().map(this::toChatGroupDomain).collect(Collectors.toList());
    }

    @Override
    public long countChatGroupsByCreatorId(UUID creatorId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(g) FROM ChatGroupEntity g WHERE g.createdBy = :creatorId", Long.class);
        query.setParameter("creatorId", creatorId);
        return query.getSingleResult();
    }

    @Override
    public long countAllChatGroups() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(g) FROM ChatGroupEntity g", Long.class);
        return query.getSingleResult();
    }

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
    @Transactional
    public ChatMessage updateChatMessage(ChatMessage chatMessage) {
        return saveChatMessage(chatMessage);
    }

    @Override
    @Transactional
    public void deleteChatMessageById(UUID id) {
        ChatMessageEntity entity = entityManager.find(ChatMessageEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<ChatMessage> findChatMessageById(UUID id) {
        ChatMessageEntity entity = entityManager.find(ChatMessageEntity.class, id);
        return entity != null ? Optional.of(toChatMessageDomain(entity)) : Optional.empty();
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupId(UUID chatGroupId) {
        return findChatMessagesByGroupId(chatGroupId);
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupIdPaginated(UUID chatGroupId, int page, int pageSize) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId ORDER BY m.createdAt ASC",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesBySenderId(UUID senderId) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.senderId = :senderId", ChatMessageEntity.class);
        query.setParameter("senderId", senderId);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupIdAndSenderId(UUID chatGroupId, UUID senderId) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId AND m.senderId = :senderId",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setParameter("senderId", senderId);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesByMessageType(String messageType) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.messageType = :messageType", ChatMessageEntity.class);
        query.setParameter("messageType", messageType);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupIdAndMessageType(UUID chatGroupId, String messageType) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId AND m.messageType = :messageType",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setParameter("messageType", messageType);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupIdAfterDate(UUID chatGroupId, LocalDateTime date) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId AND m.createdAt >= :date",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupIdBeforeDate(UUID chatGroupId, LocalDateTime date) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId AND m.createdAt <= :date",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesByChatGroupIdBetweenDates(UUID chatGroupId, LocalDateTime startDate,
            LocalDateTime endDate) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId AND m.createdAt BETWEEN :startDate AND :endDate",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public long countChatMessagesByChatGroupId(UUID chatGroupId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId", Long.class);
        query.setParameter("chatGroupId", chatGroupId);
        return query.getSingleResult();
    }

    @Override
    public long countChatMessagesBySenderId(UUID senderId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM ChatMessageEntity m WHERE m.senderId = :senderId", Long.class);
        query.setParameter("senderId", senderId);
        return query.getSingleResult();
    }

    @Override
    public List<ChatMessage> findLatestChatMessagesByChatGroupId(UUID chatGroupId, int limit) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId ORDER BY m.createdAt DESC",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findChatMessagesWithAttachmentsByChatGroupId(UUID chatGroupId) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId AND m.attachmentUrl IS NOT NULL AND m.attachmentUrl <> ''",
            ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        return query.getResultList().stream().map(this::toChatMessageDomain).collect(Collectors.toList());
    }

    public List<ChatMessage> findChatMessagesByGroupId(UUID chatGroupId) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId ORDER BY m.createdAt ASC", ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        return query.getResultList().stream()
            .map(this::toChatMessageDomain)
            .collect(Collectors.toList());
    }

    public List<ChatMessage> findRecentChatMessages(UUID chatGroupId, int limit) {
        TypedQuery<ChatMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM ChatMessageEntity m WHERE m.chatGroupId = :chatGroupId ORDER BY m.createdAt DESC", ChatMessageEntity.class);
        query.setParameter("chatGroupId", chatGroupId);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toChatMessageDomain)
            .collect(Collectors.toList());
    }

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
    @Transactional
    public Conversation updateConversation(Conversation conversation) {
        return saveConversation(conversation);
    }

    @Override
    @Transactional
    public void deleteConversationById(UUID id) {
        ConversationEntity entity = entityManager.find(ConversationEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<Conversation> findConversationById(UUID id) {
        ConversationEntity entity = entityManager.find(ConversationEntity.class, id);
        return entity != null ? Optional.of(toConversationDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Conversation> findAllConversations() {
        TypedQuery<ConversationEntity> query = entityManager.createQuery(
            "SELECT c FROM ConversationEntity c", ConversationEntity.class);
        return query.getResultList().stream().map(this::toConversationDomain).collect(Collectors.toList());
    }

    @Override
    public List<Conversation> findConversationsByParticipantId(UUID participantId) {
        return findConversationsByUserId(participantId);
    }

    @Override
    public List<Conversation> findConversationsByParticipantIdPaginated(UUID participantId, int page, int pageSize) {
        TypedQuery<ConversationEntity> query = entityManager.createQuery(
            "SELECT c FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId ORDER BY c.lastMessageAt DESC",
            ConversationEntity.class);
        query.setParameter("userId", participantId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toConversationDomain).collect(Collectors.toList());
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
    public long countConversationsByParticipantId(UUID participantId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId",
            Long.class);
        query.setParameter("userId", participantId);
        return query.getSingleResult();
    }

    @Override
    public long countAllConversations() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ConversationEntity c", Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Conversation> findRecentConversationsByParticipantId(UUID participantId, int limit) {
        TypedQuery<ConversationEntity> query = entityManager.createQuery(
            "SELECT c FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId ORDER BY c.lastMessageAt DESC",
            ConversationEntity.class);
        query.setParameter("userId", participantId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toConversationDomain).collect(Collectors.toList());
    }

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
    @Transactional
    public DirectMessage updateDirectMessage(DirectMessage directMessage) {
        return saveDirectMessage(directMessage);
    }

    @Override
    @Transactional
    public void deleteDirectMessageById(UUID id) {
        DirectMessageEntity entity = entityManager.find(DirectMessageEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<DirectMessage> findDirectMessageById(UUID id) {
        DirectMessageEntity entity = entityManager.find(DirectMessageEntity.class, id);
        return entity != null ? Optional.of(toDirectMessageDomain(entity)) : Optional.empty();
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
    public List<DirectMessage> findDirectMessagesByConversationIdPaginated(UUID conversationId, int page, int pageSize) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdAt ASC",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findDirectMessagesBySenderId(UUID senderId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.senderId = :senderId", DirectMessageEntity.class);
        query.setParameter("senderId", senderId);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findDirectMessagesByConversationIdAndSenderId(UUID conversationId, UUID senderId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.senderId = :senderId",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("senderId", senderId);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findUnreadDirectMessagesByConversationId(UUID conversationId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.isRead = false ORDER BY m.createdAt ASC",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findUnreadDirectMessagesByConversationIdAndRecipient(UUID conversationId, UUID recipientId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.senderId != :recipientId AND m.isRead = false ORDER BY m.createdAt ASC",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("recipientId", recipientId);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findDirectMessagesByConversationIdAfterDate(UUID conversationId, LocalDateTime date) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.createdAt >= :date",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findDirectMessagesByConversationIdBeforeDate(UUID conversationId, LocalDateTime date) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.createdAt <= :date",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findDirectMessagesByConversationIdBetweenDates(UUID conversationId,
            LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.createdAt BETWEEN :startDate AND :endDate",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public long countDirectMessagesByConversationId(UUID conversationId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM DirectMessageEntity m WHERE m.conversationId = :conversationId", Long.class);
        query.setParameter("conversationId", conversationId);
        return query.getSingleResult();
    }

    @Override
    public long countDirectMessagesBySenderId(UUID senderId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM DirectMessageEntity m WHERE m.senderId = :senderId", Long.class);
        query.setParameter("senderId", senderId);
        return query.getSingleResult();
    }

    @Override
    public long countUnreadDirectMessagesByConversationId(UUID conversationId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.isRead = false",
            Long.class);
        query.setParameter("conversationId", conversationId);
        return query.getSingleResult();
    }

    @Override
    public long countUnreadDirectMessagesByConversationIdAndRecipient(UUID conversationId, UUID recipientId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM DirectMessageEntity m WHERE m.conversationId = :conversationId AND m.senderId != :recipientId AND m.isRead = false",
            Long.class);
        query.setParameter("conversationId", conversationId);
        query.setParameter("recipientId", recipientId);
        return query.getSingleResult();
    }

    @Override
    public List<DirectMessage> findLatestDirectMessagesByConversationId(UUID conversationId, int limit) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdAt DESC",
            DirectMessageEntity.class);
        query.setParameter("conversationId", conversationId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findDirectMessagesByConversationIdSortedByDate(UUID conversationId, int page,
            int pageSize) {
        return findDirectMessagesByConversationIdPaginated(conversationId, page, pageSize);
    }

    @Override
    public List<DirectMessage> findDirectMessagesByUserId(UUID userId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.senderId = :userId OR m.conversationId IN " +
            "(SELECT c.id FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId)",
            DirectMessageEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public List<DirectMessage> findUnreadDirectMessagesByUserId(UUID userId) {
        TypedQuery<DirectMessageEntity> query = entityManager.createQuery(
            "SELECT m FROM DirectMessageEntity m WHERE m.conversationId IN " +
            "(SELECT c.id FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId) " +
            "AND m.senderId != :userId AND m.isRead = false",
            DirectMessageEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDirectMessageDomain).collect(Collectors.toList());
    }

    @Override
    public long countUnreadDirectMessagesByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM DirectMessageEntity m WHERE m.conversationId IN " +
            "(SELECT c.id FROM ConversationEntity c WHERE c.participant1Id = :userId OR c.participant2Id = :userId) " +
            "AND m.senderId != :userId AND m.isRead = false",
            Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

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

    @Transactional
    public void deleteDirectMessage(DirectMessage directMessage) {
        DirectMessageEntity entity = entityManager.find(DirectMessageEntity.class, directMessage.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

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
        ChatGroup.ReferenceType referenceType = null;
        if (entity.getReferenceType() != null && !entity.getReferenceType().isBlank()) {
            referenceType = ChatGroup.ReferenceType.valueOf(entity.getReferenceType());
        }
        return new ChatGroup(
            entity.getId(),
            entity.getName(),
            referenceType,
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
        entity.setReferenceType(domain.getReferenceType() != null ? domain.getReferenceType().name() : null);
        entity.setReferenceId(domain.getReferenceId());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private ChatMessage toChatMessageDomain(ChatMessageEntity entity) {
        ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;
        if (entity.getMessageType() != null && !entity.getMessageType().isBlank()) {
            messageType = ChatMessage.MessageType.valueOf(entity.getMessageType());
        }
        return new ChatMessage(
            entity.getId(),
            entity.getChatGroupId(),
            entity.getSenderId(),
            entity.getMessage(),
            messageType,
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
        entity.setMessageType(domain.getMessageType() != null ? domain.getMessageType().name() : null);
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
