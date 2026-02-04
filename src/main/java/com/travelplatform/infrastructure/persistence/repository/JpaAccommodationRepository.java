package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.infrastructure.persistence.entity.AccommodationEntity;
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
public class JpaAccommodationRepository implements AccommodationRepository {

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
        @Transactional
        public Accommodation update(Accommodation accommodation) {
                AccommodationEntity entity = toEntity(accommodation);
                entity = entityManager.merge(entity);
                return toDomain(entity);
        }

        @Override
        public List<Accommodation> findAll() {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a", AccommodationEntity.class);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
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
        public List<Accommodation> findByPriceRange(double minPrice, double maxPrice) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.basePrice BETWEEN :minPrice AND :maxPrice",
                                AccommodationEntity.class);
                query.setParameter("minPrice", BigDecimal.valueOf(minPrice));
                query.setParameter("maxPrice", BigDecimal.valueOf(maxPrice));
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        
        public List<Accommodation> findByMaxGuests(int maxGuests) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.maxGuests >= :maxGuests",
                                AccommodationEntity.class);
                query.setParameter("maxGuests", maxGuests);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        
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

                TypedQuery<AccommodationEntity> query = entityManager.createQuery(jpql, AccommodationEntity.class);
                for (java.util.Map.Entry<String, Object> entry : params.entrySet()) {
                        query.setParameter(entry.getKey(), entry.getValue());
                }
                query.setFirstResult(page * pageSize);
                query.setMaxResults(pageSize);
                return query.getResultList().stream()
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
        public List<Accommodation> findByStatusPaginated(ApprovalStatus status, int page, int pageSize) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.status = :status",
                                AccommodationEntity.class);
                query.setParameter("status", status);
                query.setFirstResult(page * pageSize);
                query.setMaxResults(pageSize);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findPremium() {
                return findPremiumAccommodations();
        }

        @Override
        public List<Accommodation> findByAmenities(List<String> amenityNames) {
                if (amenityNames == null || amenityNames.isEmpty()) {
                        return List.of();
                }
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT DISTINCT a FROM AccommodationEntity a " +
                                                "JOIN AccommodationAmenityEntity am ON am.accommodationId = a.id " +
                                                "WHERE am.amenityName IN :amenities",
                                AccommodationEntity.class);
                query.setParameter("amenities", amenityNames);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<com.travelplatform.domain.model.accommodation.AccommodationImage> findImagesByAccommodationId(
                        UUID accommodationId) {
                TypedQuery<com.travelplatform.infrastructure.persistence.entity.AccommodationImageEntity> query = entityManager
                                .createQuery(
                                                "SELECT i FROM AccommodationImageEntity i WHERE i.accommodationId = :accommodationId ORDER BY i.displayOrder",
                                                com.travelplatform.infrastructure.persistence.entity.AccommodationImageEntity.class);
                query.setParameter("accommodationId", accommodationId);
                return query.getResultList().stream()
                                .map(entity -> new com.travelplatform.domain.model.accommodation.AccommodationImage(
                                                entity.getId(),
                                                entity.getAccommodationId(),
                                                entity.getImageUrl(),
                                                entity.getDisplayOrder() != null ? entity.getDisplayOrder() : 0,
                                                entity.isPrimary(),
                                                entity.getCaption(),
                                                entity.getCreatedAt()))
                                .collect(Collectors.toList());
        }

        @Override
        public List<com.travelplatform.domain.model.accommodation.AccommodationAmenity> findAmenitiesByAccommodationId(
                        UUID accommodationId) {
                TypedQuery<com.travelplatform.infrastructure.persistence.entity.AccommodationAmenityEntity> query = entityManager
                                .createQuery(
                                                "SELECT a FROM AccommodationAmenityEntity a WHERE a.accommodationId = :accommodationId",
                                                com.travelplatform.infrastructure.persistence.entity.AccommodationAmenityEntity.class);
                query.setParameter("accommodationId", accommodationId);
                return query.getResultList().stream()
                                .map(entity -> new com.travelplatform.domain.model.accommodation.AccommodationAmenity(
                                                entity.getId(),
                                                entity.getAccommodationId(),
                                                entity.getAmenityName(),
                                                entity.getCategory()))
                                .collect(Collectors.toList());
        }

        @Override
        public Optional<com.travelplatform.domain.model.accommodation.AccommodationImage> findPrimaryImageByAccommodationId(
                        UUID accommodationId) {
                TypedQuery<com.travelplatform.infrastructure.persistence.entity.AccommodationImageEntity> query = entityManager
                                .createQuery(
                                                "SELECT i FROM AccommodationImageEntity i WHERE i.accommodationId = :accommodationId AND i.isPrimary = true",
                                                com.travelplatform.infrastructure.persistence.entity.AccommodationImageEntity.class);
                query.setParameter("accommodationId", accommodationId);
                List<com.travelplatform.infrastructure.persistence.entity.AccommodationImageEntity> results = query
                                .getResultList();
                if (results.isEmpty()) {
                        return Optional.empty();
                }
                com.travelplatform.infrastructure.persistence.entity.AccommodationImageEntity entity = results.get(0);
                return Optional.of(new com.travelplatform.domain.model.accommodation.AccommodationImage(
                                entity.getId(),
                                entity.getAccommodationId(),
                                entity.getImageUrl(),
                                entity.getDisplayOrder() != null ? entity.getDisplayOrder() : 0,
                                entity.isPrimary(),
                                entity.getCaption(),
                                entity.getCreatedAt()));
        }

        @Override
        public List<Accommodation> findByMinAverageRating(double minRating) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.averageRating >= :minRating",
                                AccommodationEntity.class);
                query.setParameter("minRating", BigDecimal.valueOf(minRating));
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findMostBooked(int limit) {
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
        public List<Accommodation> findMostViewed(int limit) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.status = :status " +
                                                "ORDER BY a.viewCount DESC",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setMaxResults(limit);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public long countAll() {
                return count();
        }

        @Override
        public long countByIsPremium(boolean isPremium) {
                TypedQuery<Long> query = entityManager.createQuery(
                                "SELECT COUNT(a) FROM AccommodationEntity a WHERE a.isPremium = :isPremium",
                                Long.class);
                query.setParameter("isPremium", isPremium);
                return query.getSingleResult();
        }

        @Override
        public List<Accommodation> findBySupplierIdPaginated(UUID supplierId, int page, int pageSize) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE a.supplierId = :supplierId",
                                AccommodationEntity.class);
                query.setParameter("supplierId", supplierId);
                query.setFirstResult(page * pageSize);
                query.setMaxResults(pageSize);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> searchByKeyword(String searchTerm) {
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
        public List<Accommodation> findAvailableForDates(LocalDate checkInDate, LocalDate checkOutDate) {
                TypedQuery<AccommodationEntity> query = entityManager.createQuery(
                                "SELECT a FROM AccommodationEntity a WHERE " +
                                                "a.status = :status AND " +
                                                "NOT EXISTS (" +
                                                "  SELECT b FROM BookingEntity b WHERE " +
                                                "  b.accommodationId = a.id AND " +
                                                "  b.status IN ('CONFIRMED', 'PENDING') AND " +
                                                "  b.checkInDate < :checkOut AND " +
                                                "  b.checkOutDate > :checkIn" +
                                                ")",
                                AccommodationEntity.class);
                query.setParameter("status", ApprovalStatus.APPROVED);
                query.setParameter("checkIn", checkInDate);
                query.setParameter("checkOut", checkOutDate);
                return query.getResultList().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Accommodation> findAvailableForDatesAndGuests(LocalDate checkInDate, LocalDate checkOutDate,
                        int guests) {
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
                query.setParameter("checkIn", checkInDate);
                query.setParameter("checkOut", checkOutDate);
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

        public long countByType(AccommodationType type) {
                TypedQuery<Long> query = entityManager.createQuery(
                                "SELECT COUNT(a) FROM AccommodationEntity a WHERE a.type = :type", Long.class);
                query.setParameter("type", type);
                return query.getSingleResult();
        }

        // Helper methods for Entity <-> Domain conversion
        private Accommodation toDomain(AccommodationEntity entity) {
                com.travelplatform.domain.valueobject.Address address = new com.travelplatform.domain.valueobject.Address(
                                entity.getAddress(),
                                entity.getCity(),
                                entity.getStateProvince(),
                                entity.getCountry(),
                                entity.getPostalCode());

                Location location = null;
                if (entity.getLatitude() != null && entity.getLongitude() != null) {
                        location = new Location(entity.getLatitude().doubleValue(),
                                        entity.getLongitude().doubleValue());
                }

                com.travelplatform.domain.valueobject.Money basePrice = new com.travelplatform.domain.valueobject.Money(
                                entity.getBasePrice(),
                                entity.getCurrency());

                Double bathrooms = entity.getBathrooms() != null ? entity.getBathrooms().doubleValue() : null;
                Double averageRating = entity.getAverageRating() != null ? entity.getAverageRating().doubleValue() : null;
                int minimumNights = entity.getMinimumNights() != null ? entity.getMinimumNights() : 1;
                long viewCount = entity.getViewCount() != null ? entity.getViewCount() : 0L;
                int bookingCount = entity.getBookingCount() != null ? entity.getBookingCount() : 0;
                int reviewCount = entity.getReviewCount() != null ? entity.getReviewCount() : 0;

                return new Accommodation(
                                entity.getId(),
                                entity.getSupplierId(),
                                entity.getType(),
                                entity.getTitle(),
                                entity.getDescription(),
                                address,
                                location,
                                basePrice,
                                entity.getMaxGuests(),
                                entity.getBedrooms(),
                                entity.getBeds(),
                                bathrooms,
                                entity.getSquareMeters(),
                                entity.getStatus(),
                                entity.getVisibilityStart(),
                                entity.getVisibilityEnd(),
                                entity.isPremium(),
                                entity.isInstantBook(),
                                entity.getCheckInTime(),
                                entity.getCheckOutTime(),
                                minimumNights,
                                entity.getMaximumNights(),
                                entity.getCancellationPolicy(),
                                viewCount,
                                bookingCount,
                                averageRating,
                                reviewCount,
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
                if (domain.getAddress() != null) {
                        entity.setAddress(domain.getAddress().getStreetAddress());
                        entity.setCity(domain.getAddress().getCity());
                        entity.setStateProvince(domain.getAddress().getStateProvince());
                        entity.setCountry(domain.getAddress().getCountry());
                        entity.setPostalCode(domain.getAddress().getPostalCode());
                } else {
                        entity.setAddress(null);
                        entity.setCity(domain.getCity());
                        entity.setStateProvince(domain.getStateProvince());
                        entity.setCountry(domain.getCountry());
                        entity.setPostalCode(domain.getPostalCode());
                }
                if (domain.getLocation() != null) {
                        entity.setLatitude(BigDecimal.valueOf(domain.getLocation().getLatitude()));
                        entity.setLongitude(BigDecimal.valueOf(domain.getLocation().getLongitude()));
                }
                if (domain.getBasePrice() != null) {
                        entity.setBasePrice(domain.getBasePrice().getAmount());
                        entity.setCurrency(domain.getBasePrice().getCurrencyCode());
                }
                entity.setMaxGuests(domain.getMaxGuests());
                entity.setBedrooms(domain.getBedrooms());
                entity.setBeds(domain.getBeds());
                if (domain.getBathrooms() != null) {
                        entity.setBathrooms(BigDecimal.valueOf(domain.getBathrooms()));
                } else {
                        entity.setBathrooms(null);
                }
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
                if (domain.getAverageRating() != null) {
                        entity.setAverageRating(BigDecimal.valueOf(domain.getAverageRating()));
                } else {
                        entity.setAverageRating(null);
                }
                entity.setReviewCount(domain.getReviewCount());
                entity.setCreatedAt(domain.getCreatedAt());
                entity.setUpdatedAt(domain.getUpdatedAt());
                entity.setApprovedAt(domain.getApprovedAt());
                entity.setApprovedBy(domain.getApprovedBy());
                return entity;
        }
}
