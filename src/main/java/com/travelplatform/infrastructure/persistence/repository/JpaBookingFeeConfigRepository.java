package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.model.booking.BookingFeeConfig;
import com.travelplatform.domain.repository.BookingFeeConfigRepository;
import com.travelplatform.infrastructure.persistence.entity.BookingFeeConfigEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation for booking fee configuration.
 */
@ApplicationScoped
public class JpaBookingFeeConfigRepository implements BookingFeeConfigRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public BookingFeeConfig save(BookingFeeConfig config) {
        BookingFeeConfigEntity entity = toEntity(config);
        if (entityManager.find(BookingFeeConfigEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<BookingFeeConfig> findActive() {
        TypedQuery<BookingFeeConfigEntity> query = entityManager.createQuery(
                "SELECT c FROM BookingFeeConfigEntity c WHERE c.active = true ORDER BY c.updatedAt DESC",
                BookingFeeConfigEntity.class);
        List<BookingFeeConfigEntity> results = query.setMaxResults(1).getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toDomain(results.get(0)));
    }

    @Override
    public Optional<BookingFeeConfig> findById(UUID id) {
        BookingFeeConfigEntity entity = entityManager.find(BookingFeeConfigEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    @Transactional
    public void deactivateAll() {
        entityManager.createQuery("UPDATE BookingFeeConfigEntity c SET c.active = false").executeUpdate();
    }

    private BookingFeeConfig toDomain(BookingFeeConfigEntity entity) {
        return new BookingFeeConfig(
                entity.getId(),
                entity.getServiceFeePercentage(),
                entity.getServiceFeeMinimum(),
                entity.getServiceFeeMaximum(),
                entity.getCleaningFeePercentage(),
                entity.getTaxRate(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private BookingFeeConfigEntity toEntity(BookingFeeConfig domain) {
        BookingFeeConfigEntity entity = new BookingFeeConfigEntity();
        entity.setId(domain.getId());
        entity.setServiceFeePercentage(domain.getServiceFeePercentage());
        entity.setServiceFeeMinimum(domain.getServiceFeeMinimum());
        entity.setServiceFeeMaximum(domain.getServiceFeeMaximum());
        entity.setCleaningFeePercentage(domain.getCleaningFeePercentage());
        entity.setTaxRate(domain.getTaxRate());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
