package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.infrastructure.persistence.entity.AccommodationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of AccommodationRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaAccommodationRepository implements AccommodationRepository, PanacheRepository<AccommodationEntity> {

        @Inject
        EntityManager entityManager;

        @Override
        @Transactional
        public Accommodation save(Accommodation accommodation) {
                AccommodationEntity entity = toEntity(accommodation);
                if (entity.getId() == null) {
                        entityManager.persist(entity);
                } else {
                        entity = entityManager.merge(entity);
                }
                return toDomain(entity);
        }

        @Override
        public Optional<Accommodation> findById(UUID id) {
                AccommodationEntity entity = entityManager.find(AccommodationEntity.class, id);
                return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
        }

        @Override
        public List<Accommodation> findBySupplierId(UUID supplierId) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.supplierId = :supplierId",
                                AccommodationEntity.class);
                query.setParameter("supplierId", supplierId);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByStatus(ApprovalStatus status) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.status = :status",
                                AccommodationEntity.class);
                query.setParameter("status", status);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByType(AccommodationType type) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.type = :type", AccommodationEntity.class);
                query.setParameter("type", type);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByCity(String city) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE LOWER(a.city) = LOWER(:city)",
                                AccommodationEntity.class);
                query.setParameter("city", city);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByCountry(String country) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE LOWER(a.country) = LOWER(:country)",
                                AccommodationEntity.class);
                query.setParameter("country", country);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByCityAndCountry(String city, String country) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE LOWER(a.city) = LOWER(:city) AND LOWER(a.country) = LOWER(:country)",
                                AccommodationEntity.class);
                query.setParameter("city", city);
                query.setParameter("country", country);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.basePrice BETWEEN :minPrice AND :maxPrice",
                                AccommodationEntity.class);
                query.setParameter("minPrice", minPrice);
                query.setParameter("maxPrice", maxPrice);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findByMaxGuests(int maxGuests) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.maxGuests >= :maxGuests",
                                AccommodationEntity.class);
                query.setParameter("maxGuests", maxGuests);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findPremiumAccommodations() {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.isPremium = true AND a.status = :status",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findNearby(Location location, double radiusKm) {
                // Using Haversine formula for distance calculation
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                double radius = radiusKm;

                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE " +
                                                "a.status = :status AND " +
                                                "a.latitude IS NOT NULL AND a.longitude IS NOT NULL AND " +
                                                "(6371 * acos(cos(radians(:lat)) * cos(radians(a.latitude)) * " +
                                                "cos(radians(a.longitude) - radians(:lng)) + " +
                                                "sin(radians(:lat)) * sin(radians(a.latitude)))) <= :radius",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setParameter("lat", lat);
                query.setParameter("lng", lng);
                query.setParameter("radius", radius);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> search(String city, String country, String type, java.math.BigDecimal minPrice,
                        java.math.BigDecimal maxPrice, Integer guests, Integer bedrooms, List<String> amenities,
                        Boolean isInstantBook, ApprovalStatus status, int page, int pageSize, String sortBy,
                        String sortOrder) {

                String jpql = "SELECT a FROM AccommodationEntity a WHERE 1=1";
                java.util.Map<String, Object> params = new java.util.HashMap<>();

                if (city != null) {
                        jpql += " AND LOWER(a.city) LIKE LOWER(:city)";
                        params.put("city", "%" + city + "%");
                }
                if (country != null) {
                        jpql += " AND LOWER(a.country) LIKE LOWER(:country)";
                        params.put("country", "%" + country + "%");
                }
                if (type != null) {
                        jpql += " AND a.type = :type"; // Assuming enum or string match
                        try {
                                params.put("type", com.travelplatform.domain.enums.AccommodationType.valueOf(type));
                        } catch (Exception e) {
                                // ignore invalid type
                        }
                }
                if (minPrice != null) {
                        jpql += " AND a.basePriceAmount >= :minPrice";
                        params.put("minPrice", minPrice);
                }
                if (maxPrice != null) {
                        jpql += " AND a.basePriceAmount <= :maxPrice";
                        params.put("maxPrice", maxPrice);
                }
                if (guests != null) {
                        jpql += " AND a.maxGuests >= :guests";
                        params.put("guests", guests);
                }
                if (bedrooms != null) {
                        jpql += " AND a.bedrooms >= :bedrooms";
                        params.put("bedrooms", bedrooms);
                }
                if (isInstantBook != null) {
                        jpql += " AND a.isInstantBook = :isInstantBook";
                        params.put("isInstantBook", isInstantBook);
                }
                if (status != null) {
                        jpql += " AND a.status = :status";
                        params.put("status", status);
                }

                // Sorting
                if (sortBy != null && !sortBy.isEmpty()) {
                        String order = "ASC";
                        if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) {
                                order = "DESC";
                        }
                        jpql += " ORDER BY a." + sortBy + " " + order;
                } else {
                        jpql += " ORDER BY a.createdAt DESC";
                }

                io.quarkus.hibernate.orm.panache.PanacheQuery<AccommodationEntity> query = find(jpql, params);
                return query.page(page, pageSize).list().stream()
                                .map(this::toDomain)
                                .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public List<Accommodation> findNearbyPaginated(Location location, double radiusKm, int page, int pageSize) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();
                double radius = radiusKm;

                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE " +
                                                "a.status = :status AND " +
                                                "a.latitude IS NOT NULL AND a.longitude IS NOT NULL AND " +
                                                "(6371 * acos(cos(radians(:lat)) * cos(radians(a.latitude)) * " +
                                                "cos(radians(a.longitude) - radians(:lng)) + " +
                                                "sin(radians(:lat)) * sin(radians(a.latitude)))) <= :radius",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setParameter("lat", lat);
                query.setParameter("lng", lng);
                query.setParameter("radius", radius);

                query.setFirstResult(page * pageSize);
                query.setMaxResults(pageSize);

                return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> search(String searchTerm) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE " +
                                                "LOWER(a.title) LIKE LOWER(:search) OR " +
                                                "LOWER(a.description) LIKE LOWER(:search) OR " +
                                                "LOWER(a.city) LIKE LOWER(:search) OR " +
                                                "LOWER(a.country) LIKE LOWER(:search)",
                                AccommodationEntity.class);
                query.setParameter("search", "%" + searchTerm + "%");
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findAvailable(UUID accommodationId, DateRange dateRange) {
                LocalDate checkIn = dateRange.getStartDate();
                LocalDate checkOut = dateRange.getEndDate();

                // Find accommodations that don't have overlapping confirmed bookings
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.id = :accommodationId AND " +
                                                "NOT EXISTS (" +
                                                "  SELECT b FROM BookingEntity b WHERE " +
                                                "  b.accommodationId = a.id AND " +
                                                "  b.status IN ('CONFIRMED', 'PENDING') AND " +
                                                "  b.checkInDate < :checkOut AND " +
                                                "  b.checkOutDate > :checkIn" +
                                                ")",
                                AccommodationEntity.class);
                query.setParameter("accommodationId", accommodationId);
                query.setParameter("checkIn", checkIn);
                query.setParameter("checkOut", checkOut);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findAvailableAccommodations(DateRange dateRange, int guests) {
                LocalDate checkIn = dateRange.getStartDate();
                LocalDate checkOut = dateRange.getEndDate();

                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE " +
                                                "a.status = :status AND " +
                                                "a.maxGuests >= :guests AND " +
                                                "NOT EXISTS (" +
                                                "  SELECT b FROM BookingEntity b WHERE " +
                                                "  b.accommodationId = a.id AND " +
                                                "  b.status IN ('CONFIRMED', 'PENDING') AND " +
                                                "  b.checkInDate < :checkOut AND " +
                                                "  b.checkOutDate > :checkIn" +
                                                ")",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setParameter("guests", guests);
                query.setParameter("checkIn", checkIn);
                query.setParameter("checkOut", checkOut);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findTopRated(int limit) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.status = :status AND a.averageRating IS NOT NULL "
                                                +
                                                "ORDER BY a.averageRating DESC, a.reviewCount DESC",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setMaxResults(limit);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findMostPopular(int limit) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.status = :status " +
                                                "ORDER BY a.bookingCount DESC, a.viewCount DESC",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setMaxResults(limit);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findRecentlyAdded(int limit) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.status = :status " +
                                                "ORDER BY a.createdAt DESC",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setMaxResults(limit);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void delete(Accommodation accommodation) {
                AccommodationEntity entity = entityManager.find(AccommodationEntity.class, accommodation.getId());
                if (entity != null) {
                        entityManager.remove(entity);
                }
        }

        @Override
        @Transactional
        public void deleteById(UUID id) {
                AccommodationEntity entity = entityManager.find(AccommodationEntity.class, id);
                if (entity != null) {
                        entityManager.remove(entity);
                }
        }

        @Override
        public boolean existsById(UUID id) {
                return entityManager.find(AccommodationEntity.class, id) != null;
        }

        @Override
        public long count() {
                TypedQuery<Long> query = entityManager.createQuery(
                                "SELECT COUNT(a) FROM AccommodationEntity a", Long.class);
                return query.getSingleResult();
        }

        @Override
        public long countBySupplierId(UUID supplierId) {
                TypedQuery<Long> query = entityManager.createQuery(
                                "SELECT COUNT(a) FROM AccommodationEntity a WHERE a.supplierId = :supplierId",
                                Long.class);
                query.setParameter("supplierId", supplierId);
                return query.getSingleResult();
        }

        @Override
        public long countByStatus(ApprovalStatus status) {
                TypedQuery<Long> query = entityManager.createQuery(
                                "SELECT COUNT(a) FROM AccommodationEntity a WHERE a.status = :status", Long.class);
                query.setParameter("status", status);
                return query.getSingleResult();
        }

        @Override
        public long countByType(AccommodationType type) {
                TypedQuery<Long> query = entityManager.createQuery(
                                "SELECT COUNT(a) FROM AccommodationEntity a WHERE a.type = :type", Long.class);
                query.setParameter("type", type);
                return query.getSingleResult();
        }

        // Helper methods for Entity <-> Domain conversion
        private Accommodation toDomain(AccommodationEntity entity) {
                return new Accommodation(
                                entity.getId(),
                                entity.getSupplierId(),
                                entity.getType(),
                                entity.getTitle(),
                                entity.getDescription(),
                                entity.getAddress(),
                                entity.getCity(),
                                entity.getStateProvince(),
                                entity.getCountry(),
                                entity.getPostalCode(),
                                entity.getLatitude() != null ? new Location(entity.getLatitude(), entity.getLongitude())
                                                : null,
                                entity.getBasePrice(),
                                entity.getCurrency(),
                                entity.getMaxGuests(),
                                entity.getBedrooms(),
                                entity.getBeds(),
                                entity.getBathrooms(),
                                entity.getSquareMeters(),
                                entity.getStatus(),
                                entity.getVisibilityStart(),
                                entity.getVisibilityEnd(),
                                entity.isPremium(),
                                entity.isInstantBook(),
                                entity.getCheckInTime(),
                                entity.getCheckOutTime(),
                                entity.getMinimumNights(),
                                entity.getMaximumNights(),
                                entity.getCancellationPolicy(),
                                entity.getViewCount(),
                                entity.getBookingCount(),
                                entity.getAverageRating(),
                                entity.getReviewCount(),
                                entity.getCreatedAt(),
                                entity.getUpdatedAt(),
                                entity.getApprovedAt(),
                                entity.getApprovedBy());
        }

        private AccommodationEntity toEntity(Accommodation domain) {
                AccommodationEntity entity = new AccommodationEntity();
                entity.setId(domain.getId());
                entity.setSupplierId(domain.getSupplierId());
                entity.setType(domain.getType());
                entity.setTitle(domain.getTitle());
                entity.setDescription(domain.getDescription());
                entity.setAddress(domain.getAddress());
                entity.setCity(domain.getCity());
                entity.setStateProvince(domain.getStateProvince());
                entity.setCountry(domain.getCountry());
                entity.setPostalCode(domain.getPostalCode());
                if (domain.getLocation() != null) {
                        entity.setLatitude(domain.getLocation().getLatitude());
                        entity.setLongitude(domain.getLocation().getLongitude());
                }
                entity.setBasePrice(domain.getBasePrice());
                entity.setCurrency(domain.getCurrency());
                entity.setMaxGuests(domain.getMaxGuests());
                entity.setBedrooms(domain.getBedrooms());
                entity.setBeds(domain.getBeds());
                entity.setBathrooms(domain.getBathrooms());
                entity.setSquareMeters(domain.getSquareMeters());
                entity.setStatus(domain.getStatus());
                entity.setVisibilityStart(domain.getVisibilityStart());
                entity.setVisibilityEnd(domain.getVisibilityEnd());
                entity.setPremium(domain.isPremium());
                entity.setInstantBook(domain.isInstantBook());
                entity.setCheckInTime(domain.getCheckInTime());
                entity.setCheckOutTime(domain.getCheckOutTime());
                entity.setMinimumNights(domain.getMinimumNights());
                entity.setMaximumNights(domain.getMaximumNights());
                entity.setCancellationPolicy(domain.getCancellationPolicy());
                entity.setViewCount(domain.getViewCount());
                entity.setBookingCount(domain.getBookingCount());
                entity.setAverageRating(domain.getAverageRating());
                entity.setReviewCount(domain.getReviewCount());
                entity.setCreatedAt(domain.getCreatedAt());
                entity.setUpdatedAt(domain.getUpdatedAt());
                entity.setApprovedAt(domain.getApprovedAt());
                entity.setApprovedBy(domain.getApprovedBy());
                return entity;
        }
}
