package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.repository.EventRepository;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.infrastructure.persistence.entity.EventEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of EventRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaEventRepository implements EventRepository, PanacheRepository<EventEntity> {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Event save(Event event) {
        EventEntity entity = toEntity(event);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Event> findById(UUID id) {
        EventEntity entity = entityManager.find(EventEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Event> findByCreatorId(UUID creatorId) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.creatorId = :creatorId", EventEntity.class);
        query.setParameter("creatorId", creatorId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatus(ApprovalStatus status) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.status = :status", EventEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByEventType(String eventType) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.eventType = :eventType", EventEntity.class);
        query.setParameter("eventType", eventType);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByLocation(String locationName) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE LOWER(e.locationName) LIKE LOWER(:locationName)", EventEntity.class);
        query.setParameter("locationName", "%" + locationName + "%");
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findNearby(Location location, double radiusKm) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double radius = radiusKm;
        
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.latitude IS NOT NULL AND e.longitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(e.latitude)) * " +
            "cos(radians(e.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(e.latitude)))) <= :radius", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("lat", lat);
        query.setParameter("lng", lng);
        query.setParameter("radius", radius);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcomingEvents(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY e.startDate ASC", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findOngoingEvents() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.startDate <= CURRENT_TIMESTAMP AND " +
            "e.endDate >= CURRENT_TIMESTAMP", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findPastEvents(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY e.endDate DESC", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findPopularEvents(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status " +
            "ORDER BY e.currentParticipants DESC", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findFreeEvents() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.pricePerPerson IS NULL OR e.pricePerPerson = 0", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.pricePerPerson BETWEEN :minPrice AND :maxPrice", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> search(String searchTerm) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "LOWER(e.title) LIKE LOWER(:search) OR " +
            "LOWER(e.description) LIKE LOWER(:search) OR " +
            "LOWER(e.locationName) LIKE LOWER(:search)", EventEntity.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.status = :status AND " +
            "e.startDate >= :startDate AND e.endDate <= :endDate", EventEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> findEventsWithAvailableSlots(UUID eventId) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.id = :eventId AND " +
            "e.status = :status AND " +
            "e.currentParticipants < e.maxParticipants", EventEntity.class);
        query.setParameter("eventId", eventId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Event event) {
        EventEntity entity = entityManager.find(EventEntity.class, event.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        EventEntity entity = entityManager.find(EventEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.find(EventEntity.class, id) != null;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM EventEntity e", Long.class);
        return query.getSingleResult();
    }

    @Override
    public long countByCreatorId(UUID creatorId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM EventEntity e WHERE e.creatorId = :creatorId", Long.class);
        query.setParameter("creatorId", creatorId);
        return query.getSingleResult();
    }

    @Override
    public long countByStatus(ApprovalStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM EventEntity e WHERE e.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public long countByEventType(String eventType) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM EventEntity e WHERE e.eventType = :eventType", Long.class);
        query.setParameter("eventType", eventType);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
    private Event toDomain(EventEntity entity) {
        return new Event(
            entity.getId(),
            entity.getCreatorId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getEventType(),
            entity.getLocationName(),
            entity.getLatitude() != null && entity.getLongitude() != null 
                ? new Location(entity.getLatitude(), entity.getLongitude()) 
                : null,
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getPricePerPerson(),
            entity.getCurrency(),
            entity.getMaxParticipants(),
            entity.getCurrentParticipants(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getApprovedAt(),
            entity.getApprovedBy()
        );
    }

    private EventEntity toEntity(Event domain) {
        EventEntity entity = new EventEntity();
        entity.setId(domain.getId());
        entity.setCreatorId(domain.getCreatorId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setEventType(domain.getEventType());
        entity.setLocationName(domain.getLocationName());
        if (domain.getLocation() != null) {
            entity.setLatitude(domain.getLocation().getLatitude());
            entity.setLongitude(domain.getLocation().getLongitude());
        }
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setPricePerPerson(domain.getPricePerPerson());
        entity.setCurrency(domain.getCurrency());
        entity.setMaxParticipants(domain.getMaxParticipants());
        entity.setCurrentParticipants(domain.getCurrentParticipants());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setApprovedBy(domain.getApprovedBy());
        return entity;
    }
}
