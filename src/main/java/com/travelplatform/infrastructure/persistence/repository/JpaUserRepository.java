package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.model.user.UserPreferences;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.infrastructure.persistence.entity.UserEntity;
import com.travelplatform.infrastructure.persistence.entity.UserPreferencesEntity;
import com.travelplatform.infrastructure.persistence.entity.UserProfileEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of UserRepository.
 */
@ApplicationScoped
public class JpaUserRepository implements UserRepository {

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
    @Transactional
    public User update(User user) {
        UserEntity entity = entityManager.merge(toEntity(user));
        return toDomain(entity);
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
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<User> findAll() {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u", UserEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findAll(int page, int pageSize) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u", UserEntity.class);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findAll(UserRole role, UserStatus status, java.time.LocalDate startDate,
            java.time.LocalDate endDate, int page, int pageSize) {
        StringBuilder jpql = new StringBuilder("SELECT u FROM UserEntity u WHERE 1=1");
        if (role != null)
            jpql.append(" AND u.role = :role");
        if (status != null)
            jpql.append(" AND u.status = :status");
        if (startDate != null)
            jpql.append(" AND u.createdAt >= :startDate");
        if (endDate != null)
            jpql.append(" AND u.createdAt <= :endDate");
        jpql.append(" ORDER BY u.createdAt DESC");

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql.toString(), UserEntity.class);
        if (role != null)
            query.setParameter("role", role);
        if (status != null)
            query.setParameter("status", status);
        if (startDate != null)
            query.setParameter("startDate", startDate.atStartOfDay());
        if (endDate != null)
            query.setParameter("endDate", endDate.atTime(23, 59, 59));

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count(UserRole role, UserStatus status, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(u) FROM UserEntity u WHERE 1=1");
        if (role != null)
            jpql.append(" AND u.role = :role");
        if (status != null)
            jpql.append(" AND u.status = :status");
        if (startDate != null)
            jpql.append(" AND u.createdAt >= :startDate");
        if (endDate != null)
            jpql.append(" AND u.createdAt <= :endDate");

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        if (role != null)
            query.setParameter("role", role);
        if (status != null)
            query.setParameter("status", status);
        if (startDate != null)
            query.setParameter("startDate", startDate.atStartOfDay());
        if (endDate != null)
            query.setParameter("endDate", endDate.atTime(23, 59, 59));

        return query.getSingleResult();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.role = :role", UserEntity.class);
        query.setParameter("role", role);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.status = :status", UserEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByRoleAndStatus(UserRole role, UserStatus status) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.role = :role AND u.status = :status", UserEntity.class);
        query.setParameter("role", role);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByRolePaginated(UserRole role, int page, int pageSize) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.role = :role", UserEntity.class);
        query.setParameter("role", role);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByStatusPaginated(UserStatus status, int page, int pageSize) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.status = :status", UserEntity.class);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
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
    public long countAll() {
        return count();
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM UserEntity u", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Optional<UserProfile> findProfileByUserId(UUID userId) {
        TypedQuery<UserProfileEntity> query = entityManager.createQuery(
                "SELECT p FROM UserProfileEntity p WHERE p.userId = :userId", UserProfileEntity.class);
        query.setParameter("userId", userId);
        List<UserProfileEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toProfileDomain(results.get(0)));
    }

    @Override
    public Optional<UserPreferences> findPreferencesByUserId(UUID userId) {
        TypedQuery<UserPreferencesEntity> query = entityManager.createQuery(
                "SELECT p FROM UserPreferencesEntity p WHERE p.userId = :userId", UserPreferencesEntity.class);
        query.setParameter("userId", userId);
        List<UserPreferencesEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toPreferencesDomain(results.get(0)));
    }

    @Override
    public List<User> searchByNameOrEmail(String searchTerm) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE " +
                        "LOWER(u.email) LIKE LOWER(:search) OR " +
                        "EXISTS (SELECT p FROM UserProfileEntity p WHERE p.userId = u.id AND LOWER(p.fullName) LIKE LOWER(:search))",
                UserEntity.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> search(String queryText, int page, int pageSize) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE " +
                        "LOWER(u.email) LIKE LOWER(:search) OR " +
                        "EXISTS (SELECT p FROM UserProfileEntity p WHERE p.userId = u.id AND LOWER(p.fullName) LIKE LOWER(:search))",
                UserEntity.class);
        query.setParameter("search", "%" + queryText + "%");
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findFollowersByUserId(UUID userId) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.id IN " +
                        "(SELECT f.followerId FROM UserFollowEntity f WHERE f.followingId = :userId)",
                UserEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findFollowingByUserId(UUID userId) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.id IN " +
                        "(SELECT f.followingId FROM UserFollowEntity f WHERE f.followerId = :userId)",
                UserEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countFollowersByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(f) FROM UserFollowEntity f WHERE f.followingId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    @Override
    public long countFollowingByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(f) FROM UserFollowEntity f WHERE f.followerId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    @Override
    public boolean isFollowing(UUID followerId, UUID followingId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(f) FROM UserFollowEntity f WHERE f.followerId = :followerId AND f.followingId = :followingId",
                Long.class);
        query.setParameter("followerId", followerId);
        query.setParameter("followingId", followingId);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public void addFollow(UUID followerId, UUID followingId) {
        com.travelplatform.infrastructure.persistence.entity.UserFollowEntity entity = new com.travelplatform.infrastructure.persistence.entity.UserFollowEntity(
                UUID.randomUUID(), followerId, followingId);
        entityManager.persist(entity);
    }

    @Override
    @Transactional
    public void removeFollow(UUID followerId, UUID followingId) {
        entityManager.createQuery(
                "DELETE FROM UserFollowEntity f WHERE f.followerId = :followerId AND f.followingId = :followingId")
                .setParameter("followerId", followerId)
                .setParameter("followingId", followingId)
                .executeUpdate();
    }

    @Override
    public List<User> findFollowers(UUID userId, int page, int pageSize) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.id IN " +
                        "(SELECT f.followerId FROM UserFollowEntity f WHERE f.followingId = :userId)",
                UserEntity.class);
        query.setParameter("userId", userId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findFollowing(UUID userId, int page, int pageSize) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.id IN " +
                        "(SELECT f.followingId FROM UserFollowEntity f WHERE f.followerId = :userId)",
                UserEntity.class);
        query.setParameter("userId", userId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getStatus(),
                entity.isEmailVerified(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastLoginAt());
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

    private UserProfile toProfileDomain(UserProfileEntity entity) {
        com.travelplatform.domain.model.user.DrivingLicenseCategory dlc = null;
        if (entity.getDrivingLicenseCategory() != null && !entity.getDrivingLicenseCategory().isBlank()) {
            dlc = com.travelplatform.domain.model.user.DrivingLicenseCategory
                    .valueOf(entity.getDrivingLicenseCategory());
        }
        com.travelplatform.domain.model.user.WorkStatus occupation = null;
        if (entity.getOccupation() != null && !entity.getOccupation().isBlank()) {
            occupation = com.travelplatform.domain.model.user.WorkStatus.valueOf(entity.getOccupation());
        }
        return new UserProfile(
                entity.getId(),
                entity.getUserId(),
                entity.getFullName(),
                entity.getPhotoUrl(),
                entity.getBirthDate(),
                entity.getGender(),
                entity.getBio(),
                entity.getLocation(),
                entity.getPhoneNumber(),
                dlc,
                occupation,
                entity.getStripeConnectAccountId(),
                entity.getBankName(),
                entity.getBankAccountIban(),
                entity.getBankAccountBic(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private UserPreferences toPreferencesDomain(UserPreferencesEntity entity) {
        UserPreferences.BudgetRange budgetRange = null;
        if (entity.getBudgetRange() != null && !entity.getBudgetRange().isBlank()) {
            budgetRange = UserPreferences.BudgetRange.valueOf(entity.getBudgetRange());
        }
        UserPreferences.TravelStyle travelStyle = null;
        if (entity.getTravelStyle() != null && !entity.getTravelStyle().isBlank()) {
            travelStyle = UserPreferences.TravelStyle.valueOf(entity.getTravelStyle());
        }
        return new UserPreferences(
                entity.getId(),
                entity.getUserId(),
                entity.getPreferredDestinations(),
                budgetRange,
                travelStyle,
                entity.getInterests(),
                entity.isEmailNotifications(),
                entity.isPushNotifications(),
                entity.isSmsNotifications(),
                entity.getNotificationTypes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
