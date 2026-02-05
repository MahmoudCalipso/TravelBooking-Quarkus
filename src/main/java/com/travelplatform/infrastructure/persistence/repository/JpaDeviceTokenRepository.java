package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.infrastructure.persistence.entity.DeviceTokenEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaDeviceTokenRepository implements PanacheRepositoryBase<DeviceTokenEntity, UUID> {

    public List<DeviceTokenEntity> findByUserId(UUID userId) {
        return list("userId", userId);
    }

    public Optional<DeviceTokenEntity> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public void deleteByToken(String token) {
        delete("token", token);
    }
}
