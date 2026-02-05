package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.infrastructure.persistence.entity.DisputeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class JpaDisputeRepository implements PanacheRepositoryBase<DisputeEntity, UUID> {
}
