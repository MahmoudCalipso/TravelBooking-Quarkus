package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.infrastructure.persistence.entity.ReferralEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaReferralRepository implements PanacheRepositoryBase<ReferralEntity, UUID> {

    public Optional<ReferralEntity> findByCode(String code) {
        return find("referralCode", code).firstResultOptional();
    }

    public Optional<ReferralEntity> findByOwnerId(UUID ownerId) {
        return find("ownerId", ownerId).firstResultOptional();
    }
}
