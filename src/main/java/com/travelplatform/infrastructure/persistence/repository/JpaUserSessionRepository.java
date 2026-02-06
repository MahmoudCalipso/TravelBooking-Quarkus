package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.repository.UserSessionRepository;
import com.travelplatform.infrastructure.persistence.entity.UserSessionEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of UserSessionRepository.
 */
@ApplicationScoped
public class JpaUserSessionRepository implements UserSessionRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public void save(UserSessionEntity session) {
        if (entityManager.find(UserSessionEntity.class, session.getId()) == null) {
            entityManager.persist(session);
        } else {
            entityManager.merge(session);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UserSessionEntity session = entityManager.find(UserSessionEntity.class, id);
        if (session != null) {
            entityManager.remove(session);
        }
    }

    @Override
    public Optional<UserSessionEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserSessionEntity.class, id));
    }

    @Override
    public List<UserSessionEntity> findByUserId(UUID userId) {
        TypedQuery<UserSessionEntity> query = entityManager.createQuery(
                "SELECT s FROM UserSessionEntity s WHERE s.userId = :userId ORDER BY s.createdAt DESC",
                UserSessionEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<UserSessionEntity> findActiveByUserId(UUID userId) {
        TypedQuery<UserSessionEntity> query = entityManager.createQuery(
                "SELECT s FROM UserSessionEntity s WHERE s.userId = :userId AND s.expiresAt > :now ORDER BY s.createdAt DESC",
                UserSessionEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("now", LocalDateTime.now());
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteAllByUserId(UUID userId) {
        entityManager.createQuery("DELETE FROM UserSessionEntity s WHERE s.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public List<UserSessionEntity> findAll(int page, int pageSize) {
        TypedQuery<UserSessionEntity> query = entityManager.createQuery(
                "SELECT s FROM UserSessionEntity s ORDER BY s.createdAt DESC",
                UserSessionEntity.class);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(s) FROM UserSessionEntity s", Long.class)
                .getSingleResult();
    }
}
