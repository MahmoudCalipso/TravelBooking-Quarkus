package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.infrastructure.persistence.entity.LoyaltyAccountEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaLoyaltyAccountRepository implements PanacheRepositoryBase<LoyaltyAccountEntity, UUID> {

    public Optional<LoyaltyAccountEntity> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }
}
