package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.infrastructure.persistence.entity.ReviewEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of ReviewRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaReviewRepository implements ReviewRepository, PanacheRepository<ReviewEntity> {

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
    public Optional<Review> findById(UUID id) {
        ReviewEntity entity = entityManager.find(ReviewEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Review> findByReviewerId(UUID reviewerId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.reviewerId = :reviewerId", ReviewEntity.class);
        query.setParameter("reviewerId", reviewerId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationId(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByBookingId(UUID bookingId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.bookingId = :bookingId", ReviewEntity.class);
        query.setParameter("bookingId", bookingId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByStatus(ApprovalStatus status) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.status = :status", ReviewEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdAndStatus(UUID accommodationId, ApprovalStatus status) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByOverallRating(int rating) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.overallRating = :rating", ReviewEntity.class);
        query.setParameter("rating", rating);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByRatingRange(int minRating, int maxRating) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.overallRating BETWEEN :minRating AND :maxRating", ReviewEntity.class);
        query.setParameter("minRating", minRating);
        query.setParameter("maxRating", maxRating);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByAccommodationIdAndRatingRange(UUID accommodationId, int minRating, int maxRating) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.overallRating BETWEEN :minRating AND :maxRating", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("minRating", minRating);
        query.setParameter("maxRating", maxRating);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findVerifiedReviews(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.isVerified = true", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findRecentReviews(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status " +
            "ORDER BY r.createdAt DESC", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findMostHelpfulReviews(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status " +
            "ORDER BY r.helpfulCount DESC, r.createdAt DESC", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findHighestRatedReviews(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status " +
            "ORDER BY r.overallRating DESC, r.helpfulCount DESC", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findLowestRatedReviews(UUID accommodationId, int limit) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status " +
            "ORDER BY r.overallRating ASC, r.createdAt DESC", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findReviewsWithPhotos(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status " +
            "AND EXISTS (SELECT p FROM ReviewPhotoEntity p WHERE p.reviewId = r.id)", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findReviewsWithHostResponse(UUID accommodationId) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status " +
            "AND r.responseFromHost IS NOT NULL", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findReviewsByTravelType(UUID accommodationId, String travelType) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.travelType = :travelType AND r.status = :status", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("travelType", travelType);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findReviewsByStayDateRange(UUID accommodationId, LocalDate startDate, LocalDate endDate) {
        TypedQuery<ReviewEntity> query = entityManager.createQuery(
            "SELECT r FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.stayedDate BETWEEN :startDate AND :endDate AND r.status = :status", ReviewEntity.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("status", ApprovalStatus.APPROVED);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserReviewed(UUID userId, UUID accommodationId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r WHERE r.reviewerId = :userId AND r.accommodationId = :accommodationId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("accommodationId", accommodationId);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean hasUserReviewedBooking(UUID userId, UUID bookingId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r WHERE r.reviewerId = :userId AND r.bookingId = :bookingId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("bookingId", bookingId);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public void delete(Review review) {
        ReviewEntity entity = entityManager.find(ReviewEntity.class, review.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
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
    public boolean existsById(UUID id) {
        return entityManager.find(ReviewEntity.class, id) != null;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r", Long.class);
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
    public long countByReviewerId(UUID reviewerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReviewEntity r WHERE r.reviewerId = :reviewerId", Long.class);
        query.setParameter("reviewerId", reviewerId);
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
    public double calculateAverageRating(UUID accommodationId) {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(r.overallRating) FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status", Double.class);
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
            "SELECT AVG(" + ratingField + ") FROM ReviewEntity r WHERE r.accommodationId = :accommodationId AND r.status = :status", Double.class);
        query.setParameter("accommodationId", accommodationId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }

    // Helper methods for Entity <-> Domain conversion
    private Review toDomain(ReviewEntity entity) {
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
            entity.getTravelType(),
            entity.getStayedDate(),
            entity.isVerified(),
            entity.getStatus(),
            entity.getHelpfulCount(),
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
        entity.setTravelType(domain.getTravelType());
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
