package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.NotificationType;
import com.travelplatform.domain.model.notification.Notification;
import com.travelplatform.domain.repository.NotificationRepository;
import com.travelplatform.infrastructure.persistence.entity.NotificationEntity;
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
 * JPA implementation of NotificationRepository.
 */
@ApplicationScoped
public class JpaNotificationRepository implements NotificationRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Notification save(Notification notification) {
        NotificationEntity entity = toEntity(notification);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    @Transactional
    public Notification update(Notification notification) {
        NotificationEntity entity = entityManager.merge(toEntity(notification));
        return toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        NotificationEntity entity = entityManager.find(NotificationEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        NotificationEntity entity = entityManager.find(NotificationEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Notification> findAll() {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n", NotificationEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return findByUserIdPaginatedSortedByDateDesc(userId, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<Notification> findByUserIdPaginated(UUID userId, int page, int pageSize) {
        return findByUserIdPaginatedSortedByDateDesc(userId, page, pageSize);
    }

    @Override
    public List<Notification> findByType(NotificationType type) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.type = :type ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("type", type);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndType(UUID userId, NotificationType type) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        return findByUserIdAndIsRead(userId, false);
    }

    @Override
    public List<Notification> findReadByUserId(UUID userId) {
        return findByUserIdAndIsRead(userId, true);
    }

    @Override
    public List<Notification> findByUserIdAndIsRead(UUID userId, boolean isRead) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = :isRead ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isRead", isRead);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByRelatedEntity(String entityType, UUID entityId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("entityType", entityType);
        query.setParameter("entityId", entityId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndRelatedEntity(UUID userId, String entityType, UUID entityId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("entityType", entityType);
        query.setParameter("entityId", entityId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime date) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.createdAt >= :date ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndCreatedAtBefore(UUID userId, LocalDateTime date) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.createdAt <= :date ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findLatestByUserId(UUID userId, int limit) {
        return findByUserIdPaginatedSortedByDateDesc(userId, 0, limit);
    }

    @Override
    public List<Notification> findLatestUnreadByUserId(UUID userId, int limit) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    @Override
    public long countReadByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = true", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    @Override
    public long countByType(NotificationType type) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n WHERE n.type = :type", Long.class);
        query.setParameter("type", type);
        return query.getSingleResult();
    }

    @Override
    public long countByUserIdAndType(UUID userId, NotificationType type) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        return query.getSingleResult();
    }

    @Override
    public long countAll() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n", Long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public long markAllAsReadByUserId(UUID userId) {
        return entityManager.createQuery(
            "UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = false")
            .setParameter("readAt", LocalDateTime.now())
            .setParameter("userId", userId)
            .executeUpdate();
    }

    @Override
    @Transactional
    public long markAsReadByUserIdAndType(UUID userId, NotificationType type) {
        return entityManager.createQuery(
            "UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.type = :type AND n.isRead = false")
            .setParameter("readAt", LocalDateTime.now())
            .setParameter("userId", userId)
            .setParameter("type", type)
            .executeUpdate();
    }

    @Override
    @Transactional
    public long deleteOldNotificationsBeforeDate(LocalDateTime date) {
        return entityManager.createQuery(
            "DELETE FROM NotificationEntity n WHERE n.createdAt < :date")
            .setParameter("date", date)
            .executeUpdate();
    }

    @Override
    @Transactional
    public long deleteReadNotificationsByUserId(UUID userId) {
        return entityManager.createQuery(
            "DELETE FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = true")
            .setParameter("userId", userId)
            .executeUpdate();
    }

    @Override
    @Transactional
    public long deleteByRelatedEntity(String entityType, UUID entityId) {
        return entityManager.createQuery(
            "DELETE FROM NotificationEntity n WHERE n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId")
            .setParameter("entityType", entityType)
            .setParameter("entityId", entityId)
            .executeUpdate();
    }

    @Override
    public List<Notification> findByUserIdPaginatedWithFilter(UUID userId, int page, int pageSize, Boolean isRead) {
        String jpql = "SELECT n FROM NotificationEntity n WHERE n.userId = :userId";
        if (isRead != null) {
            jpql += " AND n.isRead = :isRead";
        }
        jpql += " ORDER BY n.createdAt DESC";
        TypedQuery<NotificationEntity> query = entityManager.createQuery(jpql, NotificationEntity.class);
        query.setParameter("userId", userId);
        if (isRead != null) {
            query.setParameter("isRead", isRead);
        }
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndTypeAndIsRead(UUID userId, NotificationType type, boolean isRead) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type AND n.isRead = :isRead ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        query.setParameter("isRead", isRead);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndTypes(UUID userId, List<NotificationType> types) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type IN :types ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("types", types);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndTypesAndIsRead(UUID userId, List<NotificationType> types, boolean isRead) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type IN :types AND n.isRead = :isRead ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("types", types);
        query.setParameter("isRead", isRead);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findWithActionUrlByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.actionUrl IS NOT NULL AND n.actionUrl <> '' ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findWithoutActionUrlByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND (n.actionUrl IS NULL OR n.actionUrl = '') ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndLastNDays(UUID userId, int days) {
        return findByUserIdAndCreatedAtAfter(userId, LocalDateTime.now().minusDays(days));
    }

    @Override
    public List<Notification> findByUserIdAndLastNHours(UUID userId, int hours) {
        return findByUserIdAndCreatedAtAfter(userId, LocalDateTime.now().minusHours(hours));
    }

    @Override
    public List<Notification> findByUserIdAndLastNMinutes(UUID userId, int minutes) {
        return findByUserIdAndCreatedAtAfter(userId, LocalDateTime.now().minusMinutes(minutes));
    }

    @Override
    public List<Notification> findOldestByUserId(UUID userId, int limit) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId ORDER BY n.createdAt ASC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndTitleContaining(UUID userId, String keyword) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND LOWER(n.title) LIKE LOWER(:keyword) ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndMessageContaining(UUID userId, String keyword) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND LOWER(n.message) LIKE LOWER(:keyword) ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndKeyword(UUID userId, String keyword) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND " +
            "(LOWER(n.title) LIKE LOWER(:keyword) OR LOWER(n.message) LIKE LOWER(:keyword)) ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdPaginatedSortedByDateDesc(UUID userId, int page, int pageSize) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdPaginatedSortedByDateAsc(UUID userId, int page, int pageSize) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId ORDER BY n.createdAt ASC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndIsReadPaginated(UUID userId, boolean isRead, int page, int pageSize) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = :isRead ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isRead", isRead);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndTypePaginated(UUID userId, NotificationType type, int page, int pageSize) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndTypeAndIsReadPaginated(UUID userId, NotificationType type, boolean isRead,
            int page, int pageSize) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type AND n.isRead = :isRead ORDER BY n.createdAt DESC",
            NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        query.setParameter("isRead", isRead);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Notification toDomain(NotificationEntity entity) {
        return new Notification(
            entity.getId(),
            entity.getUserId(),
            entity.getType(),
            entity.getTitle(),
            entity.getMessage(),
            entity.getRelatedEntityType(),
            entity.getRelatedEntityId(),
            entity.getActionUrl(),
            entity.isRead(),
            entity.getCreatedAt(),
            entity.getReadAt()
        );
    }

    private NotificationEntity toEntity(Notification domain) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setType(domain.getType());
        entity.setTitle(domain.getTitle());
        entity.setMessage(domain.getMessage());
        entity.setRelatedEntityType(domain.getEntityType());
        entity.setRelatedEntityId(domain.getEntityId());
        entity.setActionUrl(domain.getActionUrl());
        entity.setRead(domain.isRead());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setReadAt(domain.getReadAt());
        return entity;
    }
}
