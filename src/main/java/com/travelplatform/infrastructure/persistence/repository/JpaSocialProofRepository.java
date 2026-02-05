package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.infrastructure.persistence.entity.SocialProofEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class JpaSocialProofRepository implements PanacheRepositoryBase<SocialProofEntity, UUID> {
}
