package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.infrastructure.persistence.entity.UserEntity;
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
 * JPA implementation of UserRepository.
 * This class implements the repository interface defined in the Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaUserRepository implements UserRepository, PanacheRepository<UserEntity> {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = toEntity(user);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        UserEntity entity = entityManager.find(UserEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class);
        query.setParameter("email", email);
        List<UserEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toDomain(results.get(0)));
    }

    @Override
    public List<User> findAll() {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u", UserEntity.class);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRole(UserRole role) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.role = :role", UserEntity.class);
        query.setParameter("role", role);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.status = :status", UserEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRoleAndStatus(UserRole role, UserStatus status) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.role = :role AND u.status = :status", UserEntity.class);
        query.setParameter("role", role);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.email) LIKE LOWER(:search) OR " +
            "EXISTS (SELECT p FROM UserProfileEntity p WHERE p.userId = u.id AND " +
            "LOWER(p.fullName) LIKE LOWER(:search))", UserEntity.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findFollowers(UUID userId) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.id IN " +
            "(SELECT f.followerId FROM UserFollowEntity f WHERE f.followingId = :userId)", UserEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findFollowing(UUID userId) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.id IN " +
            "(SELECT f.followingId FROM UserFollowEntity f WHERE f.followerId = :userId)", UserEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isFollowing(UUID followerId, UUID followingId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(f) FROM UserFollowEntity f WHERE f.followerId = :followerId AND f.followingId = :followingId", Long.class);
        query.setParameter("followerId", followerId);
        query.setParameter("followingId", followingId);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public void delete(User user) {
        UserEntity entity = entityManager.find(UserEntity.class, user.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UserEntity entity = entityManager.find(UserEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.find(UserEntity.class, id) != null;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM UserEntity u", Long.class);
        return query.getSingleResult();
    }

    @Override
    public long countByRole(UserRole role) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role", Long.class);
        query.setParameter("role", role);
        return query.getSingleResult();
    }

    @Override
    public long countByStatus(UserStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM UserEntity u WHERE u.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public List<User> findRecentRegistrations(LocalDateTime since) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC", UserEntity.class);
        query.setParameter("since", since);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findActiveUsers(LocalDateTime activeSince) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.lastLoginAt >= :activeSince ORDER BY u.lastLoginAt DESC", UserEntity.class);
        query.setParameter("activeSince", activeSince);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    // Helper methods for Entity <-> Domain conversion
    private User toDomain(UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getRole(),
            entity.getStatus(),
            entity.getEmailVerified(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getLastLoginAt()
        );
    }

    private UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRole(domain.getRole());
        entity.setStatus(domain.getStatus());
        entity.setEmailVerified(domain.isEmailVerified());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setLastLoginAt(domain.getLastLoginAt());
        return entity;
    }
}
