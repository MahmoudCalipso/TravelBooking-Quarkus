package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.model.review.ReviewHelpful;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.infrastructure.persistence.entity.ReviewEntity;
import com.travelplatform.infrastructure.persistence.entity.ReviewHelpfulEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of ReviewRepository.
 */
@ApplicationScoped
public class JpaReviewRepository implements ReviewRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public Review save(Review review) {
        ReviewEntity entity = toEntity(review);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    @Transactional
    public ReviewHelpful saveHelpful(ReviewHelpful helpful) {
        ReviewHelpfulEntity entity = new ReviewHelpfulEntity(
            helpful.getId(),
            helpful.getReviewId(),
            helpful.getUserId(),
            helpful.isHelpful());
        if (entityManager.find(ReviewHelpfulEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return helpful;
    }

    @Override
    @Transactional
    public Review update(Review review) {
        ReviewEntity entity = entityManager.merge(toEntity(review));
        return toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        ReviewEntity entity = entityManager.find(ReviewEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public Optional<Review> findById(UUID id) {
        ReviewEntity entity = entityManager.find(ReviewEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r", ReviewEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByReviewerId(UUID reviewerId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.reviewerId = :reviewerId", ReviewEntity.class);
        query.setParameter("reviewerId", reviewerId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationId(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Review> findByBookingId(UUID bookingId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.bookingId = :bookingId", ReviewEntity.class);
        query.setParameter("bookingId", bookingId);
        List<ReviewEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toDomain(results.get(0)));
    }

    @Override
    public List<Review> findByStatus(ApprovalStatus status) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.status = :status", ReviewEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdPaginated(UUID accommodationId, int page, int pageSize) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByReviewerIdPaginated(UUID reviewerId, int page, int pageSize) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.reviewerId = :reviewerId", ReviewEntity.class);
        query.setParameter("reviewerId", reviewerId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByStatusPaginated(ApprovalStatus status, int page, int pageSize) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.status = :status", ReviewEntity.class);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findVerified() {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.isVerified = true", ReviewEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findUnverified() {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.isVerified = false", ReviewEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByOverallRating(int rating) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.overallRating = :rating", ReviewEntity.class);
        query.setParameter("rating", rating);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByMinOverallRating(int minRating) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.overallRating >= :minRating", ReviewEntity.class);
        query.setParameter("minRating", minRating);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByTravelType(String travelType) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.travelType = :travelType", ReviewEntity.class);
        query.setParameter("travelType", travelType);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByStayedDate(LocalDate stayedDate) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.stayedDate = :stayedDate", ReviewEntity.class);
        query.setParameter("stayedDate", stayedDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByStayedDateBetween(LocalDate startDate, LocalDate endDate) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.stayedDate BETWEEN :startDate AND :endDate", ReviewEntity.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByCreatedAtAfter(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.createdAt >= :start", ReviewEntity.class);
        query.setParameter("start", start);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByCreatedAtBefore(LocalDate date) {
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.createdAt < :end", ReviewEntity.class);
        query.setParameter("end", end);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.createdAt >= :start AND r.createdAt < :end", ReviewEntity.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByReviewerId(UUID reviewerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r WHERE r.reviewerId = :reviewerId", Long.class);
        query.setParameter("reviewerId", reviewerId);
        return query.getSingleResult();
    }

    @Override
    public long countByAccommodationId(UUID accommodationId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r WHERE r.accommodationId = :accommodationId", Long.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getSingleResult();
    }

    @Override
    public long countByStatus(ApprovalStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r WHERE r.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public long countAll() {
        return count();
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r", Long.class);
        return query.getSingleResult();
    }

    @Override
    public double calculateAverageRatingByAccommodation(UUID accommodationId) {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(r.overallRating) FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status",
            Double.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }

    @Override
    public double calculateAverageRatingByCategory(UUID accommodationId, String category) {
        String ratingField = switch (category.toLowerCase()) {
            case "cleanliness" -> "r.cleanlinessRating";
            case "accuracy" -> "r.accuracyRating";
            case "communication" -> "r.communicationRating";
            case "location" -> "r.locationRating";
            case "value" -> "r.valueRating";
            default -> "r.overallRating";
        };
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(" + ratingField + ") FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status",
            Double.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }

    @Override
    public List<Review> findWithHostResponse() {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.responseFromHost IS NOT NULL AND r.responseFromHost <> ''",
            ReviewEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findWithoutHostResponse() {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.responseFromHost IS NULL OR r.responseFromHost = ''",
            ReviewEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findWithHostResponseByAccommodation(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.responseFromHost IS NOT NULL AND r.responseFromHost <> ''",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findWithoutHostResponseByAccommodation(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND (r.responseFromHost IS NULL OR r.responseFromHost = '')",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findMostHelpful(int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r ORDER BY r.helpfulCount DESC, r.createdAt DESC", ReviewEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findHighestRated(int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r ORDER BY r.overallRating DESC, r.helpfulCount DESC", ReviewEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findLowestRated(int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r ORDER BY r.overallRating ASC, r.createdAt DESC", ReviewEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findMostRecent(int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r ORDER BY r.createdAt DESC", ReviewEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> searchByKeyword(String keyword) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE " +
            "LOWER(r.title) LIKE LOWER(:keyword) OR " +
            "LOWER(r.content) LIKE LOWER(:keyword) OR " +
            "LOWER(r.pros) LIKE LOWER(:keyword) OR " +
            "LOWER(r.cons) LIKE LOWER(:keyword)", ReviewEntity.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdAndStatus(UUID accommodationId, ApprovalStatus status) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByReviewerIdAndStatus(UUID reviewerId, ApprovalStatus status) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.reviewerId = :reviewerId AND r.status = :status",
            ReviewEntity.class);
        query.setParameter("reviewerId", reviewerId);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ReviewHelpful> findHelpfulByReviewId(UUID reviewId) {
        TypedQuery<ReviewHelpfulEntity> query = entityManager.createQuery(
            "SELECT h FROM ReviewHelpfulEntity h WHERE h.reviewId = :reviewId", ReviewHelpfulEntity.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultList().stream().map(this::toHelpfulDomain).collect(Collectors.toList());
    }

    @Override
    public List<ReviewHelpful> findHelpfulByUserId(UUID userId) {
        TypedQuery<ReviewHelpfulEntity> query = entityManager.createQuery(
            "SELECT h FROM ReviewHelpfulEntity h WHERE h.userId = :userId", ReviewHelpfulEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toHelpfulDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewHelpful> findHelpfulByReviewIdAndUserId(UUID reviewId, UUID userId) {
        TypedQuery<ReviewHelpfulEntity> query = entityManager.createQuery(
            "SELECT h FROM ReviewHelpfulEntity h WHERE h.reviewId = :reviewId AND h.userId = :userId",
            ReviewHelpfulEntity.class);
        query.setParameter("reviewId", reviewId);
        query.setParameter("userId", userId);
        List<ReviewHelpfulEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toHelpfulDomain(results.get(0)));
    }

    @Override
    public long countHelpfulByReviewId(UUID reviewId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(h) FROM ReviewHelpfulEntity h WHERE h.reviewId = :reviewId AND h.isHelpful = true",
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.getSingleResult();
    }

    @Override
    public long countNotHelpfulByReviewId(UUID reviewId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(h) FROM ReviewHelpfulEntity h WHERE h.reviewId = :reviewId AND h.isHelpful = false",
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.getSingleResult();
    }

    @Override
    public List<Review> findBySupplierId(UUID supplierId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r JOIN AccommodationEntity a ON r.accommodationId = a.id WHERE a.supplierId = :supplierId",
            ReviewEntity.class);
        query.setParameter("supplierId", supplierId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findBySupplierIdPaginated(UUID supplierId, int page, int pageSize) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r JOIN AccommodationEntity a ON r.accommodationId = a.id WHERE a.supplierId = :supplierId",
            ReviewEntity.class);
        query.setParameter("supplierId", supplierId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findBySupplierIdAndStatus(UUID supplierId, ApprovalStatus status) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r JOIN AccommodationEntity a ON r.accommodationId = a.id WHERE a.supplierId = :supplierId AND r.status = :status",
            ReviewEntity.class);
        query.setParameter("supplierId", supplierId);
        query.setParameter("status", status);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countBySupplierId(UUID supplierId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r JOIN AccommodationEntity a ON r.accommodationId = a.id WHERE a.supplierId = :supplierId",
            Long.class);
        query.setParameter("supplierId", supplierId);
        return query.getSingleResult();
    }

    @Override
    public List<Review> findWithPhotos() {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE EXISTS (SELECT p FROM ReviewPhotoEntity p WHERE p.reviewId = r.id)",
            ReviewEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findWithPhotosByAccommodation(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND EXISTS (SELECT p FROM ReviewPhotoEntity p WHERE p.reviewId = r.id)",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByRatingCategoryAndValue(String category, int rating) {
        String ratingField = ratingField(category);
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE " + ratingField + " = :rating", ReviewEntity.class);
        query.setParameter("rating", rating);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByRatingCategoryMinValue(String category, int minRating) {
        String ratingField = ratingField(category);
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE " + ratingField + " >= :minRating", ReviewEntity.class);
        query.setParameter("minRating", minRating);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdSortedByRating(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId ORDER BY r.overallRating DESC",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdSortedByHelpful(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId ORDER BY r.helpfulCount DESC, r.createdAt DESC",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdSortedByDate(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId ORDER BY r.createdAt DESC",
            ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private String ratingField(String category) {
        return switch (category.toLowerCase()) {
            case "cleanliness" -> "r.cleanlinessRating";
            case "accuracy" -> "r.accuracyRating";
            case "communication" -> "r.communicationRating";
            case "location" -> "r.locationRating";
            case "value" -> "r.valueRating";
            default -> "r.overallRating";
        };
    }

    private ReviewHelpful toHelpfulDomain(ReviewHelpfulEntity entity) {
        return new ReviewHelpful(
            entity.getId(),
            entity.getReviewId(),
            entity.getUserId(),
            entity.isHelpful(),
            entity.getCreatedAt());
    }

    private Review toDomain(ReviewEntity entity) {
        Review.TravelType travelType = null;
        if (entity.getTravelType() != null && !entity.getTravelType().isBlank()) {
            travelType = Review.TravelType.valueOf(entity.getTravelType());
        }
        return new Review(
            entity.getId(),
            entity.getReviewerId(),
            entity.getAccommodationId(),
            entity.getBookingId(),
            entity.getOverallRating(),
            entity.getCleanlinessRating(),
            entity.getAccuracyRating(),
            entity.getCommunicationRating(),
            entity.getLocationRating(),
            entity.getValueRating(),
            entity.getTitle(),
            entity.getContent(),
            entity.getPros(),
            entity.getCons(),
            travelType,
            entity.getStayedDate(),
            entity.isVerified(),
            entity.getStatus(),
            entity.getHelpfulCount() != null ? entity.getHelpfulCount() : 0,
            entity.getResponseFromHost(),
            entity.getRespondedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getApprovedAt()
        );
    }

    private ReviewEntity toEntity(Review domain) {
        ReviewEntity entity = new ReviewEntity();
        entity.setId(domain.getId());
        entity.setReviewerId(domain.getReviewerId());
        entity.setAccommodationId(domain.getAccommodationId());
        entity.setBookingId(domain.getBookingId());
        entity.setOverallRating(domain.getOverallRating());
        entity.setCleanlinessRating(domain.getCleanlinessRating());
        entity.setAccuracyRating(domain.getAccuracyRating());
        entity.setCommunicationRating(domain.getCommunicationRating());
        entity.setLocationRating(domain.getLocationRating());
        entity.setValueRating(domain.getValueRating());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setPros(domain.getPros());
        entity.setCons(domain.getCons());
        entity.setTravelType(domain.getTravelType() != null ? domain.getTravelType().name() : null);
        entity.setStayedDate(domain.getStayedDate());
        entity.setVerified(domain.isVerified());
        entity.setStatus(domain.getStatus());
        entity.setHelpfulCount(domain.getHelpfulCount());
        entity.setResponseFromHost(domain.getResponseFromHost());
        entity.setRespondedAt(domain.getRespondedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        return entity;
    }
}
