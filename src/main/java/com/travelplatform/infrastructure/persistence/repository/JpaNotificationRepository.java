package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.NotificationType;
import com.travelplatform.domain.model.notification.Notification;
import com.travelplatform.domain.repository.NotificationRepository;
import com.travelplatform.infrastructure.persistence.entity.NotificationEntity;
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
 * JPA implementation of NotificationRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaNotificationRepository implements NotificationRepository, PanacheRepository<NotificationEntity> {

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
    public Optional<Notification> findById(UUID id) {
        NotificationEntity entity = entityManager.find(NotificationEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByUserIdAndType(UUID userId, NotificationType type) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findReadByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = true ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByRelatedEntity(String relatedEntityType, UUID relatedEntityId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.relatedEntityType = :relatedEntityType AND n.relatedEntityId = :relatedEntityId ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("relatedEntityType", relatedEntityType);
        query.setParameter("relatedEntityId", relatedEntityId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findRecentByUserId(UUID userId, int limit) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findRecentUnreadByUserId(UUID userId, int limit) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.createdAt >= :startDate AND n.createdAt <= :endDate ORDER BY n.createdAt DESC", NotificationEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Notification notification) {
        NotificationEntity entity = entityManager.find(NotificationEntity.class, notification.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
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
    @Transactional
    public void deleteByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId", NotificationEntity.class);
        query.setParameter("userId", userId);
        List<NotificationEntity> entities = query.getResultList();
        for (NotificationEntity entity : entities) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteReadByUserId(UUID userId) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = true", NotificationEntity.class);
        query.setParameter("userId", userId);
        List<NotificationEntity> entities = query.getResultList();
        for (NotificationEntity entity : entities) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteOlderThan(LocalDateTime date) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
            "SELECT n FROM NotificationEntity n WHERE n.createdAt < :date", NotificationEntity.class);
        query.setParameter("date", date);
        List<NotificationEntity> entities = query.getResultList();
        for (NotificationEntity entity : entities) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.find(NotificationEntity.class, id) != null;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n", Long.class);
        return query.getSingleResult();
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
    public long countUnreadByUserIdAndType(UUID userId, NotificationType type) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(n) FROM NotificationEntity n WHERE n.userId = :userId AND n.type = :type AND n.isRead = false", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
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
        entity.setRelatedEntityType(domain.getRelatedEntityType());
        entity.setRelatedEntityId(domain.getRelatedEntityId());
        entity.setActionUrl(domain.getActionUrl());
        entity.setRead(domain.isRead());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setReadAt(domain.getReadAt());
        return entity;
    }
}
