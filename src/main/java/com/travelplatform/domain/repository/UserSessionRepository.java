package com.travelplatform.domain.repository;

import com.travelplatform.infrastructure.persistence.entity.UserSessionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User Sessions.
 */
public interface UserSessionRepository {

    void save(UserSessionEntity session);

    void deleteById(UUID id);

    Optional<UserSessionEntity> findById(UUID id);

    List<UserSessionEntity> findByUserId(UUID userId);

    List<UserSessionEntity> findActiveByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);

    List<UserSessionEntity> findAll(int page, int pageSize);

    long count();
}
