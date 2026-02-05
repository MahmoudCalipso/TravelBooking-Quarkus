package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.model.currency.Currency;
import com.travelplatform.domain.repository.CurrencyRepository;
import com.travelplatform.infrastructure.persistence.entity.CurrencyEntity;
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
 * JPA implementation of CurrencyRepository.
 */
@ApplicationScoped
public class JpaCurrencyRepository implements CurrencyRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Currency save(Currency currency) {
        CurrencyEntity entity = toEntity(currency);
        entityManager.persist(entity);
        return toDomain(entity);
    }

    @Override
    @Transactional
    public Currency update(Currency currency) {
        CurrencyEntity merged = entityManager.merge(toEntity(currency));
        return toDomain(merged);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        CurrencyEntity entity = entityManager.find(CurrencyEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<Currency> findById(UUID id) {
        CurrencyEntity entity = entityManager.find(CurrencyEntity.class, id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public Optional<Currency> findByCode(String currencyCode) {
        TypedQuery<CurrencyEntity> query = entityManager.createQuery(
                "SELECT c FROM CurrencyEntity c WHERE c.currencyCode = :code",
                CurrencyEntity.class);
        query.setParameter("code", currencyCode);
        return query.getResultStream().findFirst().map(this::toDomain);
    }

    @Override
    public List<Currency> findAll() {
        TypedQuery<CurrencyEntity> query = entityManager.createQuery(
                "SELECT c FROM CurrencyEntity c ORDER BY c.countryName ASC",
                CurrencyEntity.class);
        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Currency> findByCountryName(String countryName) {
        TypedQuery<CurrencyEntity> query = entityManager.createQuery(
                "SELECT c FROM CurrencyEntity c WHERE LOWER(c.countryName) = LOWER(:countryName)",
                CurrencyEntity.class);
        query.setParameter("countryName", countryName);
        return query.getResultList().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String currencyCode) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(c) FROM CurrencyEntity c WHERE c.currencyCode = :code",
                Long.class)
                .setParameter("code", currencyCode)
                .getSingleResult();
        return count != null && count > 0;
    }

    private Currency toDomain(CurrencyEntity entity) {
        return new Currency(
                entity.getId(),
                entity.getCountryName(),
                entity.getCurrencyCode(),
                entity.getCurrencySymbol(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private CurrencyEntity toEntity(Currency currency) {
        CurrencyEntity entity = new CurrencyEntity();
        entity.setId(currency.getId());
        entity.setCountryName(currency.getCountryName());
        entity.setCurrencyCode(currency.getCurrencyCode());
        entity.setCurrencySymbol(currency.getCurrencySymbol());
        entity.setCreatedAt(currency.getCreatedAt());
        entity.setUpdatedAt(currency.getUpdatedAt());
        return entity;
    }
}
