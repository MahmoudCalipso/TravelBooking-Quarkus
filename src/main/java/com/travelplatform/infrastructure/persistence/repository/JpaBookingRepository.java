package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.infrastructure.persistence.entity.BookingEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of BookingRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaBookingRepository implements BookingRepository, PanacheRepository<BookingEntity> {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Booking save(Booking booking) {
        BookingEntity entity = toEntity(booking);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        BookingEntity entity = entityManager.find(BookingEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Booking> findByUserId(UUID userId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.userId = :userId", BookingEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByAccommodationId(UUID accommodationId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.accommodationId = :accommodationId", BookingEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.status = :status", BookingEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByPaymentStatus(PaymentStatus paymentStatus) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.paymentStatus = :paymentStatus", BookingEntity.class);
        query.setParameter("paymentStatus", paymentStatus);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserIdAndStatus(UUID userId, BookingStatus status) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.userId = :userId AND b.status = :status", BookingEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByAccommodationIdAndStatus(UUID accommodationId, BookingStatus status) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.accommodationId = :accommodationId AND b.status = :status", BookingEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.checkInDate >= :startDate AND b.checkOutDate <= :endDate", BookingEntity.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findOverlappingBookings(UUID accommodationId, DateRange dateRange) {
        LocalDate checkIn = dateRange.getStartDate();
        LocalDate checkOut = dateRange.getEndDate();
        
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND " +
            "b.status IN ('CONFIRMED', 'PENDING') AND " +
            "b.checkInDate < :checkOut AND " +
            "b.checkOutDate > :checkIn", BookingEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findActiveBookings(UUID userId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.userId = :userId AND " +
            "b.status = 'CONFIRMED' AND " +
            "b.checkInDate <= CURRENT_DATE AND " +
            "b.checkOutDate > CURRENT_DATE", BookingEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findUpcomingBookings(UUID userId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.userId = :userId AND " +
            "b.status = 'CONFIRMED' AND " +
            "b.checkInDate > CURRENT_DATE " +
            "ORDER BY b.checkInDate ASC", BookingEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findPastBookings(UUID userId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.userId = :userId AND " +
            "b.checkOutDate < CURRENT_DATE " +
            "ORDER BY b.checkOutDate DESC", BookingEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findCancelledBookings(UUID userId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.userId = :userId AND " +
            "b.status = 'CANCELLED' " +
            "ORDER BY b.cancelledAt DESC", BookingEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findCompletedBookings(UUID userId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.userId = :userId AND " +
            "b.status = 'COMPLETED' " +
            "ORDER BY b.checkOutDate DESC", BookingEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findPendingBookings(UUID accommodationId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND " +
            "b.status = 'PENDING' " +
            "ORDER BY b.createdAt ASC", BookingEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findConfirmedBookings(UUID accommodationId) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND " +
            "b.status = 'CONFIRMED' " +
            "ORDER BY b.checkInDate ASC", BookingEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateTotalRevenue(UUID accommodationId, LocalDate startDate, LocalDate endDate) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(b.totalPrice), 0) FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND " +
            "b.status = 'COMPLETED' AND " +
            "b.checkInDate >= :startDate AND " +
            "b.checkOutDate <= :endDate", BigDecimal.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }

    @Override
    public long countBookings(UUID accommodationId, LocalDate startDate, LocalDate endDate) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND " +
            "b.status = 'COMPLETED' AND " +
            "b.checkInDate >= :startDate AND " +
            "b.checkOutDate <= :endDate", Long.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void delete(Booking booking) {
        BookingEntity entity = entityManager.find(BookingEntity.class, booking.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        BookingEntity entity = entityManager.find(BookingEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.find(BookingEntity.class, id) != null;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b", Long.class);
        return query.getSingleResult();
    }

    @Override
    public long countByUserId(UUID userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE b.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    @Override
    public long countByAccommodationId(UUID accommodationId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE b.accommodationId = :accommodationId", Long.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getSingleResult();
    }

    @Override
    public long countByStatus(BookingStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE b.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public long countByPaymentStatus(PaymentStatus paymentStatus) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE b.paymentStatus = :paymentStatus", Long.class);
        query.setParameter("paymentStatus", paymentStatus);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
    private Booking toDomain(BookingEntity entity) {
        return new Booking(
            entity.getId(),
            entity.getUserId(),
            entity.getAccommodationId(),
            entity.getCheckInDate(),
            entity.getCheckOutDate(),
            entity.getNumberOfGuests(),
            entity.getNumberOfAdults(),
            entity.getNumberOfChildren(),
            entity.getNumberOfInfants(),
            entity.getTotalNights(),
            entity.getBasePricePerNight(),
            entity.getTotalBasePrice(),
            entity.getServiceFee(),
            entity.getCleaningFee(),
            entity.getTaxAmount(),
            entity.getDiscountAmount(),
            entity.getTotalPrice(),
            entity.getCurrency(),
            entity.getStatus(),
            entity.getPaymentStatus(),
            entity.getCancellationReason(),
            entity.getCancelledAt(),
            entity.getCancelledBy(),
            entity.getSpecialRequests(),
            entity.getGuestMessageToHost(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getConfirmedAt()
        );
    }

    private BookingEntity toEntity(Booking domain) {
        BookingEntity entity = new BookingEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setAccommodationId(domain.getAccommodationId());
        entity.setCheckInDate(domain.getCheckInDate());
        entity.setCheckOutDate(domain.getCheckOutDate());
        entity.setNumberOfGuests(domain.getNumberOfGuests());
        entity.setNumberOfAdults(domain.getNumberOfAdults());
        entity.setNumberOfChildren(domain.getNumberOfChildren());
        entity.setNumberOfInfants(domain.getNumberOfInfants());
        entity.setTotalNights(domain.getTotalNights());
        entity.setBasePricePerNight(domain.getBasePricePerNight());
        entity.setTotalBasePrice(domain.getTotalBasePrice());
        entity.setServiceFee(domain.getServiceFee());
        entity.setCleaningFee(domain.getCleaningFee());
        entity.setTaxAmount(domain.getTaxAmount());
        entity.setDiscountAmount(domain.getDiscountAmount());
        entity.setTotalPrice(domain.getTotalPrice());
        entity.setCurrency(domain.getCurrency());
        entity.setStatus(domain.getStatus());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setCancellationReason(domain.getCancellationReason());
        entity.setCancelledAt(domain.getCancelledAt());
        entity.setCancelledBy(domain.getCancelledBy());
        entity.setSpecialRequests(domain.getSpecialRequests());
        entity.setGuestMessageToHost(domain.getGuestMessageToHost());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setConfirmedAt(domain.getConfirmedAt());
        return entity;
    }
}
