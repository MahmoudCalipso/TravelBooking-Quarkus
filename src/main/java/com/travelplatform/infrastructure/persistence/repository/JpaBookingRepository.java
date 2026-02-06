package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.infrastructure.persistence.entity.BookingEntity;
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
public class JpaBookingRepository implements BookingRepository {

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
    @Transactional
    public Booking update(Booking booking) {
        BookingEntity entity = toEntity(booking);
        entity = entityManager.merge(entity);
        return toDomain(entity);
    }

    @Override
    public List<Booking> findAll() {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b", BookingEntity.class);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
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
    public List<Booking> findByUserIdPaginated(UUID userId, BookingStatus status, int page, int pageSize) {
        String jpql = "SELECT b FROM BookingEntity b WHERE b.userId = :userId";
        if (status != null) {
            jpql += " AND b.status = :status";
        }
        TypedQuery<BookingEntity> query = entityManager.createQuery(jpql, BookingEntity.class);
        query.setParameter("userId", userId);
        if (status != null) {
            query.setParameter("status", status);
        }
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByAccommodationIdPaginated(UUID accommodationId, BookingStatus status, int page, int pageSize) {
        String jpql = "SELECT b FROM BookingEntity b WHERE b.accommodationId = :accommodationId";
        if (status != null) {
            jpql += " AND b.status = :status";
        }
        TypedQuery<BookingEntity> query = entityManager.createQuery(jpql, BookingEntity.class);
        query.setParameter("accommodationId", accommodationId);
        if (status != null) {
            query.setParameter("status", status);
        }
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStatusPaginated(BookingStatus status, int page, int pageSize) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.status = :status", BookingEntity.class);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByCheckInDate(LocalDate checkInDate) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.checkInDate = :checkInDate", BookingEntity.class);
        query.setParameter("checkInDate", checkInDate);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByCheckOutDate(LocalDate checkOutDate) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.checkOutDate = :checkOutDate", BookingEntity.class);
        query.setParameter("checkOutDate", checkOutDate);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findActive() {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.status = 'CONFIRMED'", BookingEntity.class);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findUpcomingByUserId(UUID userId) {
        return findUpcomingBookings(userId);
    }

    @Override
    public List<Booking> findPastByUserId(UUID userId) {
        return findPastBookings(userId);
    }

    @Override
    public List<Booking> findCancelledByUserId(UUID userId) {
        return findCancelledBookings(userId);
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
    public List<Booking> findOverlappingBookings(UUID accommodationId, LocalDate checkIn, LocalDate checkOut) {
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

    @Override
    public BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(b.totalPrice), 0) FROM BookingEntity b WHERE " +
            "b.status = :status AND b.checkInDate >= :startDate AND b.checkOutDate <= :endDate",
            BigDecimal.class);
        query.setParameter("status", BookingStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }

    @Override
    public BigDecimal calculateTotalRefunds(LocalDate startDate, LocalDate endDate) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(p.refundAmount), 0) FROM BookingPaymentEntity p WHERE " +
            "p.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') AND " +
            "p.refundedAt IS NOT NULL AND p.refundedAt >= :startDateTime AND p.refundedAt <= :endDateTime",
            BigDecimal.class);
        query.setParameter("startDateTime", startDate.atStartOfDay());
        query.setParameter("endDateTime", endDate.plusDays(1).atStartOfDay().minusNanos(1));
        return query.getSingleResult();
    }

    @Override
    public long countCompletedBookings(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE b.status = :status AND " +
            "b.checkInDate >= :startDate AND b.checkOutDate <= :endDate", Long.class);
        query.setParameter("status", BookingStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }

    @Override
    public long countCancelledBookings(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b WHERE b.status = :status AND " +
            "b.checkInDate >= :startDate AND b.checkOutDate <= :endDate", Long.class);
        query.setParameter("status", BookingStatus.CANCELLED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }

    @Override
    public long countAll() {
        return count();
    }

    @Override
    public List<Booking> findByTotalPriceRange(double minPrice, double maxPrice) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.totalPrice BETWEEN :minPrice AND :maxPrice",
            BookingEntity.class);
        query.setParameter("minPrice", BigDecimal.valueOf(minPrice));
        query.setParameter("maxPrice", BigDecimal.valueOf(maxPrice));
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByNumberOfGuests(int guests) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.numberOfGuests = :guests", BookingEntity.class);
        query.setParameter("guests", guests);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByCurrency(String currency) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.currency = :currency", BookingEntity.class);
        query.setParameter("currency", currency);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<com.travelplatform.domain.model.booking.BookingPayment> findPaymentsByBookingId(UUID bookingId) {
        TypedQuery<com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity> query = entityManager.createQuery(
            "SELECT p FROM BookingPaymentEntity p WHERE p.bookingId = :bookingId",
            com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity.class);
        query.setParameter("bookingId", bookingId);
        return query.getResultList().stream().map(this::toPaymentDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<com.travelplatform.domain.model.booking.BookingPayment> findPaymentByTransactionId(String transactionId) {
        TypedQuery<com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity> query = entityManager.createQuery(
            "SELECT p FROM BookingPaymentEntity p WHERE p.transactionId = :transactionId",
            com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity.class);
        query.setParameter("transactionId", transactionId);
        List<com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toPaymentDomain(results.get(0)));
    }

    @Override
    public List<com.travelplatform.domain.model.booking.BookingPayment> findPaymentsByStatus(PaymentStatus status) {
        TypedQuery<com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity> query = entityManager.createQuery(
            "SELECT p FROM BookingPaymentEntity p WHERE p.status = :status",
            com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toPaymentDomain).collect(Collectors.toList());
    }

    @Override
    public List<com.travelplatform.domain.model.booking.BookingPayment> findPaymentsByPaymentMethod(String paymentMethod) {
        TypedQuery<com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity> query = entityManager.createQuery(
            "SELECT p FROM BookingPaymentEntity p WHERE p.paymentMethod = :paymentMethod",
            com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity.class);
        query.setParameter("paymentMethod", paymentMethod);
        return query.getResultList().stream().map(this::toPaymentDomain).collect(Collectors.toList());
    }

    @Override
    public long countPaymentsByBookingId(UUID bookingId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM BookingPaymentEntity p WHERE p.bookingId = :bookingId", Long.class);
        query.setParameter("bookingId", bookingId);
        return query.getSingleResult();
    }

    @Override
    public double calculateTotalRevenueByAccommodation(UUID accommodationId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(b.totalPrice), 0) FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND b.status = :status", BigDecimal.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", BookingStatus.COMPLETED);
        return query.getSingleResult().doubleValue();
    }

    @Override
    public double calculateTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return calculateTotalRevenue(startDate, endDate).doubleValue();
    }

    @Override
    public double calculateTotalRevenueByAccommodationAndDateRange(UUID accommodationId, LocalDate startDate,
            LocalDate endDate) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(b.totalPrice), 0) FROM BookingEntity b WHERE " +
            "b.accommodationId = :accommodationId AND b.status = :status AND " +
            "b.checkInDate >= :startDate AND b.checkOutDate <= :endDate", BigDecimal.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", BookingStatus.COMPLETED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult().doubleValue();
    }

    @Override
    public List<Booking> findWithSpecialRequests() {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.specialRequests IS NOT NULL AND b.specialRequests <> ''",
            BookingEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByCancellationReason(String reason) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.cancellationReason = :reason", BookingEntity.class);
        query.setParameter("reason", reason);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByCancelledBy(UUID cancelledBy) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.cancelledBy = :cancelledBy", BookingEntity.class);
        query.setParameter("cancelledBy", cancelledBy);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findMostExpensive(int limit) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b ORDER BY b.totalPrice DESC", BookingEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findLeastExpensive(int limit) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b ORDER BY b.totalPrice ASC", BookingEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findWithFailedPayments() {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.paymentStatus = :status", BookingEntity.class);
        query.setParameter("status", PaymentStatus.FAILED);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findWithPendingPayments() {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.paymentStatus = :status", BookingEntity.class);
        query.setParameter("status", PaymentStatus.PENDING);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findWithRefunds() {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b WHERE b.paymentStatus IN ('REFUNDED', 'PARTIALLY_REFUNDED')",
            BookingEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findBySupplierIdPaginated(UUID supplierId, int page, int pageSize) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b JOIN AccommodationEntity a ON b.accommodationId = a.id " +
            "WHERE a.supplierId = :supplierId", BookingEntity.class);
        query.setParameter("supplierId", supplierId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findBySupplierIdAndStatus(UUID supplierId, BookingStatus status) {
        TypedQuery<BookingEntity> query = entityManager.createQuery(
            "SELECT b FROM BookingEntity b JOIN AccommodationEntity a ON b.accommodationId = a.id " +
            "WHERE a.supplierId = :supplierId AND b.status = :status", BookingEntity.class);
        query.setParameter("supplierId", supplierId);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countBySupplierId(UUID supplierId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM BookingEntity b JOIN AccommodationEntity a ON b.accommodationId = a.id " +
            "WHERE a.supplierId = :supplierId", Long.class);
        query.setParameter("supplierId", supplierId);
        return query.getSingleResult();
    }

    @Override
    public BigDecimal calculateServiceFeesBySupplierRoles(List<UserRole> roles, LocalDateTime start,
            LocalDateTime end, List<BookingStatus> statuses) {
        if (roles == null || roles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<BookingStatus> effectiveStatuses = (statuses == null || statuses.isEmpty())
                ? List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED)
                : statuses;

        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(b.serviceFee), 0) FROM BookingEntity b " +
                "JOIN AccommodationEntity a ON b.accommodationId = a.id " +
                "JOIN UserEntity u ON a.supplierId = u.id " +
                "WHERE u.role IN :roles " +
                "AND b.status IN :statuses " +
                "AND b.createdAt BETWEEN :start AND :end",
            BigDecimal.class);
        query.setParameter("roles", roles);
        query.setParameter("statuses", effectiveStatuses);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
    private Booking toDomain(BookingEntity entity) {
        com.travelplatform.domain.valueobject.DateRange dateRange = new com.travelplatform.domain.valueobject.DateRange(
            entity.getCheckInDate(), entity.getCheckOutDate());
        String currency = entity.getCurrency() != null ? entity.getCurrency() : "USD";
        com.travelplatform.domain.valueobject.Money basePricePerNight = toMoney(entity.getBasePricePerNight(), currency);
        com.travelplatform.domain.valueobject.Money totalBasePrice = toMoney(entity.getTotalBasePrice(), currency);
        com.travelplatform.domain.valueobject.Money serviceFee = toMoney(entity.getServiceFee(), currency);
        com.travelplatform.domain.valueobject.Money cleaningFee = toMoney(entity.getCleaningFee(), currency);
        com.travelplatform.domain.valueobject.Money taxAmount = toMoney(entity.getTaxAmount(), currency);
        com.travelplatform.domain.valueobject.Money discountAmount = toMoney(entity.getDiscountAmount(), currency);
        com.travelplatform.domain.valueobject.Money totalPrice = toMoney(entity.getTotalPrice(), currency);
        long totalNights = entity.getTotalNights() != null ? entity.getTotalNights().longValue() : dateRange.getNights();
        int numberOfGuests = entity.getNumberOfGuests() != null ? entity.getNumberOfGuests() : 0;
        int numberOfAdults = entity.getNumberOfAdults() != null ? entity.getNumberOfAdults() : 0;
        int numberOfChildren = entity.getNumberOfChildren() != null ? entity.getNumberOfChildren() : 0;
        int numberOfInfants = entity.getNumberOfInfants() != null ? entity.getNumberOfInfants() : 0;

        return new Booking(
            entity.getId(),
            entity.getUserId(),
            entity.getAccommodationId(),
            dateRange,
            numberOfGuests,
            numberOfAdults,
            numberOfChildren,
            numberOfInfants,
            totalNights,
            basePricePerNight,
            totalBasePrice,
            serviceFee,
            cleaningFee,
            taxAmount,
            discountAmount,
            totalPrice,
            currency,
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
        entity.setTotalNights(Math.toIntExact(domain.getTotalNights()));
        entity.setBasePricePerNight(amountOrZero(domain.getBasePricePerNight()));
        entity.setTotalBasePrice(amountOrZero(domain.getTotalBasePrice()));
        entity.setServiceFee(amountOrZero(domain.getServiceFee()));
        entity.setCleaningFee(amountOrZero(domain.getCleaningFee()));
        entity.setTaxAmount(amountOrZero(domain.getTaxAmount()));
        entity.setDiscountAmount(amountOrZero(domain.getDiscountAmount()));
        entity.setTotalPrice(amountOrZero(domain.getTotalPrice()));
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

    private com.travelplatform.domain.model.booking.BookingPayment toPaymentDomain(
            com.travelplatform.infrastructure.persistence.entity.BookingPaymentEntity entity) {
        String currency = entity.getCurrency() != null ? entity.getCurrency() : "USD";
        com.travelplatform.domain.valueobject.Money amount = toMoney(entity.getAmount(), currency);
        com.travelplatform.domain.valueobject.Money refundAmount = entity.getRefundAmount() != null
            ? toMoney(entity.getRefundAmount(), currency)
            : null;
        com.travelplatform.domain.model.booking.BookingPayment.PaymentMethod method =
            com.travelplatform.domain.model.booking.BookingPayment.PaymentMethod.valueOf(entity.getPaymentMethod());
        return new com.travelplatform.domain.model.booking.BookingPayment(
            entity.getId(),
            entity.getBookingId(),
            amount,
            currency,
            method,
            entity.getPaymentProvider(),
            entity.getTransactionId(),
            entity.getStatus(),
            entity.getFailureReason(),
            refundAmount,
            entity.getRefundReason(),
            entity.getCreatedAt(),
            entity.getPaidAt(),
            entity.getRefundedAt()
        );
    }

    private com.travelplatform.domain.valueobject.Money toMoney(BigDecimal amount, String currency) {
        return new com.travelplatform.domain.valueobject.Money(
            amount != null ? amount : BigDecimal.ZERO,
            currency != null ? currency : "USD");
    }

    private BigDecimal amountOrZero(com.travelplatform.domain.valueobject.Money money) {
        return money != null ? money.getAmount() : BigDecimal.ZERO;
    }
}
