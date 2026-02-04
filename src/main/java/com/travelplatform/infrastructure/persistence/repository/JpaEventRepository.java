package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.model.event.EventParticipant;
import com.travelplatform.domain.repository.EventRepository;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.domain.valueobject.Money;
import com.travelplatform.infrastructure.persistence.entity.EventEntity;
import com.travelplatform.infrastructure.persistence.entity.EventParticipantEntity;
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
 */
@ApplicationScoped
public class JpaEventRepository implements EventRepository {

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
    @Transactional
    public Event update(Event event) {
        EventEntity entity = entityManager.merge(toEntity(event));
        return toDomain(entity);
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
    public Optional<Event> findById(UUID id) {
        EventEntity entity = entityManager.find(EventEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Event> findAll() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e", EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatorId(UUID creatorId) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.creatorId = :creatorId", EventEntity.class);
        query.setParameter("creatorId", creatorId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatus(ApprovalStatus status) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.status = :status", EventEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByEventType(String eventType) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.eventType = :eventType", EventEntity.class);
        query.setParameter("eventType", eventType);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatorIdPaginated(UUID creatorId, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.creatorId = :creatorId", EventEntity.class);
        query.setParameter("creatorId", creatorId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatusPaginated(ApprovalStatus status, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.status = :status", EventEntity.class);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatorIdAndStatus(UUID creatorId, ApprovalStatus status) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.creatorId = :creatorId AND e.status = :status", EventEntity.class);
        query.setParameter("creatorId", creatorId);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcoming() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.startDate > CURRENT_TIMESTAMP ORDER BY e.startDate ASC",
            EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findPast() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.endDate < CURRENT_TIMESTAMP ORDER BY e.endDate DESC",
            EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findOngoing() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.startDate <= CURRENT_TIMESTAMP AND e.endDate >= CURRENT_TIMESTAMP",
            EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStartDateAfter(LocalDateTime date) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.startDate >= :date", EventEntity.class);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByEndDateBefore(LocalDateTime date) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.endDate <= :date", EventEntity.class);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.startDate >= :startDate AND e.endDate <= :endDate", EventEntity.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByLocationName(String locationName) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE LOWER(e.locationName) LIKE LOWER(:locationName)", EventEntity.class);
        query.setParameter("locationName", "%" + locationName + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByPriceRange(double minPrice, double maxPrice) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.pricePerPerson BETWEEN :minPrice AND :maxPrice", EventEntity.class);
        query.setParameter("minPrice", BigDecimal.valueOf(minPrice));
        query.setParameter("maxPrice", BigDecimal.valueOf(maxPrice));
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findFree() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.pricePerPerson IS NULL OR e.pricePerPerson = 0", EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findPaid() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.pricePerPerson > 0", EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findWithAvailableSpots() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.maxParticipants IS NULL OR e.currentParticipants < e.maxParticipants",
            EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findFullyBooked() {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.maxParticipants IS NOT NULL AND e.currentParticipants >= e.maxParticipants",
            EventEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
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
    public long countAll() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM EventEntity e", Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Event> searchByKeyword(String keyword) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "LOWER(e.title) LIKE LOWER(:keyword) OR " +
            "LOWER(e.description) LIKE LOWER(:keyword) OR " +
            "LOWER(e.locationName) LIKE LOWER(:keyword)", EventEntity.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcomingSortedByStartDate(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.startDate > CURRENT_TIMESTAMP ORDER BY e.startDate ASC",
            EventEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findMostPopular(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e ORDER BY e.currentParticipants DESC", EventEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCurrency(String currency) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.currency = :currency", EventEntity.class);
        query.setParameter("currency", currency);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<EventParticipant> findParticipantsByEventId(UUID eventId) {
        TypedQuery<EventParticipantEntity> query = entityManager.createQuery(
            "SELECT p FROM EventParticipantEntity p WHERE p.eventId = :eventId", EventParticipantEntity.class);
        query.setParameter("eventId", eventId);
        return query.getResultList().stream().map(this::toParticipantDomain).collect(Collectors.toList());
    }

    @Override
    public List<EventParticipant> findParticipantsByUserId(UUID userId) {
        TypedQuery<EventParticipantEntity> query = entityManager.createQuery(
            "SELECT p FROM EventParticipantEntity p WHERE p.userId = :userId", EventParticipantEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toParticipantDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<EventParticipant> findParticipantByEventIdAndUserId(UUID eventId, UUID userId) {
        TypedQuery<EventParticipantEntity> query = entityManager.createQuery(
            "SELECT p FROM EventParticipantEntity p WHERE p.eventId = :eventId AND p.userId = :userId",
            EventParticipantEntity.class);
        query.setParameter("eventId", eventId);
        query.setParameter("userId", userId);
        List<EventParticipantEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toParticipantDomain(results.get(0)));
    }

    @Override
    public long countParticipantsByEventId(UUID eventId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM EventParticipantEntity p WHERE p.eventId = :eventId", Long.class);
        query.setParameter("eventId", eventId);
        return query.getSingleResult();
    }

    @Override
    public List<Event> findEventsByUserId(UUID userId) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e JOIN EventParticipantEntity p ON p.eventId = e.id WHERE p.userId = :userId",
            EventEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcomingEventsByUserId(UUID userId) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e JOIN EventParticipantEntity p ON p.eventId = e.id " +
            "WHERE p.userId = :userId AND e.startDate > CURRENT_TIMESTAMP", EventEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findPastEventsByUserId(UUID userId) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e JOIN EventParticipantEntity p ON p.eventId = e.id " +
            "WHERE p.userId = :userId AND e.endDate < CURRENT_TIMESTAMP", EventEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByEventTypePaginated(String eventType, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.eventType = :eventType", EventEntity.class);
        query.setParameter("eventType", eventType);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByLocationNamePaginated(String locationName, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE LOWER(e.locationName) LIKE LOWER(:locationName)", EventEntity.class);
        query.setParameter("locationName", "%" + locationName + "%");
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByPriceRangePaginated(double minPrice, double maxPrice, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.pricePerPerson BETWEEN :minPrice AND :maxPrice", EventEntity.class);
        query.setParameter("minPrice", BigDecimal.valueOf(minPrice));
        query.setParameter("maxPrice", BigDecimal.valueOf(maxPrice));
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatusAndEventType(ApprovalStatus status, String eventType) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.status = :status AND e.eventType = :eventType", EventEntity.class);
        query.setParameter("status", status);
        query.setParameter("eventType", eventType);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatusAndLocationName(ApprovalStatus status, String locationName) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.status = :status AND LOWER(e.locationName) LIKE LOWER(:locationName)",
            EventEntity.class);
        query.setParameter("status", status);
        query.setParameter("locationName", "%" + locationName + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatusAndDateRange(ApprovalStatus status, LocalDateTime startDate,
            LocalDateTime endDate) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.status = :status AND e.startDate >= :startDate AND e.endDate <= :endDate",
            EventEntity.class);
        query.setParameter("status", status);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatorIdAndDateRange(UUID creatorId, LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.creatorId = :creatorId AND e.startDate >= :startDate AND e.endDate <= :endDate",
            EventEntity.class);
        query.setParameter("creatorId", creatorId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findCheapest(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.pricePerPerson IS NOT NULL ORDER BY e.pricePerPerson ASC",
            EventEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findMostExpensive(int limit) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.pricePerPerson IS NOT NULL ORDER BY e.pricePerPerson DESC",
            EventEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByCreatorIdAndStatusPaginated(UUID creatorId, ApprovalStatus status, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.creatorId = :creatorId AND e.status = :status", EventEntity.class);
        query.setParameter("creatorId", creatorId);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findByLocation(double latitude, double longitude, double radiusKm, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE " +
            "e.latitude IS NOT NULL AND e.longitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(e.latitude)) * " +
            "cos(radians(e.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(e.latitude)))) <= :radius",
            EventEntity.class);
        query.setParameter("lat", latitude);
        query.setParameter("lng", longitude);
        query.setParameter("radius", radiusKm);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean isParticipant(UUID userId, UUID eventId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM EventParticipantEntity p WHERE p.userId = :userId AND p.eventId = :eventId",
            Long.class);
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public void saveParticipant(EventParticipant participant) {
        EventParticipantEntity entity = new EventParticipantEntity(
            participant.getId(),
            participant.getEventId(),
            participant.getUserId());
        if (entityManager.find(EventParticipantEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    public Optional<EventParticipant> findParticipant(UUID userId, UUID eventId) {
        return findParticipantByEventIdAndUserId(eventId, userId);
    }

    @Override
    @Transactional
    public void deleteParticipant(UUID participantId) {
        EventParticipantEntity entity = entityManager.find(EventParticipantEntity.class, participantId);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public List<EventParticipant> findParticipantsByEvent(UUID eventId, int page, int pageSize) {
        TypedQuery<EventParticipantEntity> query = entityManager.createQuery(
            "SELECT p FROM EventParticipantEntity p WHERE p.eventId = :eventId", EventParticipantEntity.class);
        query.setParameter("eventId", eventId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toParticipantDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findUserRegisteredEvents(UUID userId, int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e JOIN EventParticipantEntity p ON p.eventId = e.id WHERE p.userId = :userId",
            EventEntity.class);
        query.setParameter("userId", userId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcoming(int page, int pageSize) {
        TypedQuery<EventEntity> query = entityManager.createQuery(
            "SELECT e FROM EventEntity e WHERE e.startDate > CURRENT_TIMESTAMP ORDER BY e.startDate ASC",
            EventEntity.class);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Event toDomain(EventEntity entity) {
        Event.EventType eventType = Event.EventType.OTHER;
        if (entity.getEventType() != null && !entity.getEventType().isBlank()) {
            eventType = Event.EventType.valueOf(entity.getEventType());
        }

        Location location = null;
        if (entity.getLatitude() != null && entity.getLongitude() != null) {
            location = new Location(entity.getLatitude().doubleValue(), entity.getLongitude().doubleValue());
        }

        String currency = entity.getCurrency() != null ? entity.getCurrency() : "USD";
        Money pricePerPerson = new Money(entity.getPricePerPerson() != null ? entity.getPricePerPerson() : BigDecimal.ZERO, currency);

        return new Event(
            entity.getId(),
            entity.getCreatorId(),
            entity.getTitle(),
            entity.getDescription(),
            eventType,
            entity.getLocationName(),
            location,
            entity.getStartDate(),
            entity.getEndDate(),
            pricePerPerson,
            currency,
            entity.getMaxParticipants(),
            entity.getCurrentParticipants() != null ? entity.getCurrentParticipants() : 0,
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
        entity.setEventType(domain.getEventType() != null ? domain.getEventType().name() : null);
        entity.setLocationName(domain.getLocationName());
        if (domain.getLocation() != null) {
            entity.setLatitude(BigDecimal.valueOf(domain.getLocation().getLatitude()));
            entity.setLongitude(BigDecimal.valueOf(domain.getLocation().getLongitude()));
        }
        if (domain.getPricePerPerson() != null) {
            entity.setPricePerPerson(domain.getPricePerPerson().getAmount());
        }
        entity.setCurrency(domain.getCurrency());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setMaxParticipants(domain.getMaxParticipants());
        entity.setCurrentParticipants(domain.getCurrentParticipants());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setApprovedBy(domain.getApprovedBy());
        return entity;
    }

    private EventParticipant toParticipantDomain(EventParticipantEntity entity) {
        return new EventParticipant(
            entity.getId(),
            entity.getEventId(),
            entity.getUserId(),
            entity.getRegisteredAt(),
            null,
            null,
            false
        );
    }
}
