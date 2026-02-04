package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.EngagementType;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.model.reel.ReelComment;
import com.travelplatform.domain.model.reel.ReelEngagement;
import com.travelplatform.domain.model.reel.ReelReport;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.infrastructure.persistence.entity.ReelCommentEntity;
import com.travelplatform.infrastructure.persistence.entity.ReelEngagementEntity;
import com.travelplatform.infrastructure.persistence.entity.ReelReportEntity;
import com.travelplatform.infrastructure.persistence.entity.TravelReelEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of TravelReelRepository.
 * This class implements repository interface defined in Domain layer
 * using JPA/Hibernate for data persistence.
 */
@ApplicationScoped
public class JpaTravelReelRepository implements TravelReelRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public TravelReel save(TravelReel reel) {
        TravelReelEntity entity = toEntity(reel);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return toDomain(entity);
    }

    @Override
    @Transactional
    public TravelReel update(TravelReel reel) {
        if (reel == null) {
            throw new IllegalArgumentException("Reel cannot be null");
        }
        TravelReelEntity entity = entityManager.merge(toEntity(reel));
        return toDomain(entity);
    }

    @Override
    public Optional<TravelReel> findById(UUID id) {
        TravelReelEntity entity = entityManager.find(TravelReelEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<TravelReel> findAll() {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r", TravelReelEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByCreatorId(UUID creatorId) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.creatorId = :creatorId", TravelReelEntity.class);
        query.setParameter("creatorId", creatorId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByCreatorIdPaginated(UUID creatorId, int page, int pageSize) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.creatorId = :creatorId", TravelReelEntity.class);
        query.setParameter("creatorId", creatorId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByStatus(ApprovalStatus status) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.status = :status", TravelReelEntity.class);
        query.setParameter("status", status);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByStatusPaginated(ApprovalStatus status, int page, int pageSize) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.status = :status", TravelReelEntity.class);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByVisibility(VisibilityScope visibility) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.visibility = :visibility", TravelReelEntity.class);
        query.setParameter("visibility", visibility);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByVisibilityPaginated(VisibilityScope visibility, int page, int pageSize) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.visibility = :visibility", TravelReelEntity.class);
        query.setParameter("visibility", visibility);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByStatusAndVisibility(ApprovalStatus status, VisibilityScope visibility) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.status = :status AND r.visibility = :visibility", TravelReelEntity.class);
        query.setParameter("status", status);
        query.setParameter("visibility", visibility);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByLocationName(String locationName) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE LOWER(r.locationName) LIKE LOWER(:locationName)",
            TravelReelEntity.class);
        query.setParameter("locationName", "%" + locationName + "%");
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByCreatorType(String creatorType) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.creatorType = :creatorType", TravelReelEntity.class);
        query.setParameter("creatorType", creatorType);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findPromotional() {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.isPromotional = true", TravelReelEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findNonPromotional() {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.isPromotional = false", TravelReelEntity.class);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByCreatedAtAfter(LocalDateTime date) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.createdAt >= :date", TravelReelEntity.class);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByCreatedAtBefore(LocalDateTime date) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.createdAt <= :date", TravelReelEntity.class);
        query.setParameter("date", date);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.createdAt BETWEEN :startDate AND :endDate",
            TravelReelEntity.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    public List<TravelReel> findByTag(String tag) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE :tag MEMBER OF r.tags", TravelReelEntity.class);
        query.setParameter("tag", tag);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByTags(List<String> tags) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE EXISTS (" +
            "SELECT t FROM r.tags t WHERE t IN :tags)", TravelReelEntity.class);
        query.setParameter("tags", tags);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByTags(List<String> tags, int page, int pageSize) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE EXISTS (" +
            "SELECT t FROM r.tags t WHERE t IN :tags)", TravelReelEntity.class);
        query.setParameter("tags", tags);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findNearby(Location location, double radiusKm) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double radius = radiusKm;
        
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.locationLatitude IS NOT NULL AND r.locationLongitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(r.locationLatitude)) * " +
            "cos(radians(r.locationLongitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(r.locationLatitude)))) <= :radius", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("lat", lat);
        query.setParameter("lng", lng);
        query.setParameter("radius", radius);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<TravelReel> findByLocation(Location location, double radiusKm) {
        return findNearby(location, radiusKm);
    }

    @Override
    public List<TravelReel> findByLocation(Location location, double radiusKm, int page, int pageSize) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double radius = radiusKm;

        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.visibility = :visibility AND " +
            "r.locationLatitude IS NOT NULL AND r.locationLongitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(r.locationLatitude)) * " +
            "cos(radians(r.locationLongitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(r.locationLatitude)))) <= :radius", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setParameter("lat", lat);
        query.setParameter("lng", lng);
        query.setParameter("radius", radius);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findByRelatedEntity(String entityType, UUID entityId) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.relatedEntityType = :entityType AND r.relatedEntityId = :entityId", TravelReelEntity.class);
        query.setParameter("entityType", entityType);
        query.setParameter("entityId", entityId);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findTrending(int limit) {
        // Trending algorithm: high engagement + recent + high completion rate
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.visibility = :visibility AND " +
            "r.createdAt >= :since " +
            "ORDER BY " +
            "(r.likeCount * 3 + r.shareCount * 2 + r.viewCount + r.commentCount) DESC, " +
            "r.completionRate DESC, " +
            "r.createdAt DESC", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setParameter("since", thirtyDaysAgo);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findTrending(int page, int pageSize) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.visibility = :visibility AND " +
            "r.createdAt >= :since " +
            "ORDER BY " +
            "(r.likeCount * 3 + r.shareCount * 2 + r.viewCount + r.commentCount) DESC, " +
            "r.completionRate DESC, " +
            "r.createdAt DESC", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setParameter("since", thirtyDaysAgo);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<TravelReel> findPopular(int limit) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.visibility = :visibility " +
            "ORDER BY r.viewCount DESC, r.likeCount DESC", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<TravelReel> findRecent(int limit) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.visibility = :visibility " +
            "ORDER BY r.createdAt DESC", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<TravelReel> findFeed(UUID userId, int limit) {
        // Personalized feed: reels from followed users + popular reels
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "(r.creatorId IN (SELECT f.followingId FROM UserFollowEntity f WHERE f.followerId = :userId) OR " +
            "r.status = :status AND r.visibility = :visibility) AND " +
            "r.createdAt >= :since " +
            "ORDER BY r.createdAt DESC", TravelReelEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setParameter("since", thirtyDaysAgo);
        query.setMaxResults(limit);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findFeed(UUID userId, int page, int pageSize) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "(r.creatorId IN (SELECT f.followingId FROM UserFollowEntity f WHERE f.followerId = :userId) OR " +
            "r.status = :status AND r.visibility = :visibility) AND " +
            "r.createdAt >= :since " +
            "ORDER BY r.createdAt DESC", TravelReelEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("visibility", VisibilityScope.PUBLIC);
        query.setParameter("since", thirtyDaysAgo);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public List<TravelReel> search(String searchTerm) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "LOWER(r.title) LIKE LOWER(:search) OR " +
            "LOWER(r.description) LIKE LOWER(:search) OR " +
            "LOWER(r.locationName) LIKE LOWER(:search) OR " +
            "EXISTS (SELECT t FROM r.tags t WHERE LOWER(t) LIKE LOWER(:search))", TravelReelEntity.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public boolean hasUserEngaged(UUID reelId, UUID userId, EngagementType engagementType) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM ReelEngagementEntity e WHERE " +
            "e.reelId = :reelId AND e.userId = :userId AND e.engagementType = :engagementType", Long.class);
        query.setParameter("reelId", reelId);
        query.setParameter("userId", userId);
        query.setParameter("engagementType", engagementType);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsEngagement(UUID userId, UUID reelId, EngagementType type) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM ReelEngagementEntity e WHERE " +
            "e.reelId = :reelId AND e.userId = :userId AND e.engagementType = :type", Long.class);
        query.setParameter("reelId", reelId);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        return query.getSingleResult() > 0;
    }

    @Override
    public ReelEngagement findEngagement(UUID userId, UUID reelId, EngagementType type) {
        TypedQuery<ReelEngagementEntity> query = entityManager.createQuery(
            "SELECT e FROM ReelEngagementEntity e WHERE " +
            "e.reelId = :reelId AND e.userId = :userId AND e.engagementType = :type", ReelEngagementEntity.class);
        query.setParameter("reelId", reelId);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        List<ReelEngagementEntity> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return toDomain(results.get(0));
    }

    @Override
    @Transactional
    public void saveEngagement(ReelEngagement engagement) {
        ReelEngagementEntity entity = toEntity(engagement);
        if (entityManager.find(ReelEngagementEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteEngagement(UUID engagementId) {
        ReelEngagementEntity entity = entityManager.find(ReelEngagementEntity.class, engagementId);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void saveComment(ReelComment comment) {
        ReelCommentEntity entity = toEntity(comment);
        if (entityManager.find(ReelCommentEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    public Optional<ReelComment> findCommentById(UUID commentId) {
        ReelCommentEntity entity = entityManager.find(ReelCommentEntity.class, commentId);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId) {
        ReelCommentEntity entity = entityManager.find(ReelCommentEntity.class, commentId);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public long countReportsByReel(UUID reelId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReelReportEntity r WHERE r.reelId = :reelId", Long.class);
        query.setParameter("reelId", reelId);
        return query.getSingleResult();
    }

    @Override
    public List<ReelReport> findReports(int page, int pageSize) {
        TypedQuery<ReelReportEntity> query = entityManager.createQuery(
            "SELECT r FROM ReelReportEntity r ORDER BY r.createdAt DESC", ReelReportEntity.class);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ReelReport> findReportsByStatus(ReelReport.ReportStatus status, int page, int pageSize) {
        TypedQuery<ReelReportEntity> query = entityManager.createQuery(
            "SELECT r FROM ReelReportEntity r WHERE r.status = :status ORDER BY r.createdAt DESC",
            ReelReportEntity.class);
        query.setParameter("status", status);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ReelReport> findReportById(UUID id) {
        ReelReportEntity entity = entityManager.find(ReelReportEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<ReelReport> findReportsByReporter(UUID reporterId, int page, int pageSize) {
        TypedQuery<ReelReportEntity> query = entityManager.createQuery(
            "SELECT r FROM ReelReportEntity r WHERE r.reportedBy = :reporterId ORDER BY r.createdAt DESC",
            ReelReportEntity.class);
        query.setParameter("reporterId", reporterId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countReportsByReporter(UUID reporterId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReelReportEntity r WHERE r.reportedBy = :reporterId", Long.class);
        query.setParameter("reporterId", reporterId);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void saveReport(ReelReport report) {
        ReelReportEntity entity = toEntity(report);
        if (entityManager.find(ReelReportEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Transactional
    public void delete(TravelReel reel) {
        TravelReelEntity entity = entityManager.find(TravelReelEntity.class, reel.getId());
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        TravelReelEntity entity = entityManager.find(TravelReelEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public boolean existsById(UUID id) {
        return entityManager.find(TravelReelEntity.class, id) != null;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM TravelReelEntity r", Long.class);
        return query.getSingleResult();
    }

    @Override
    public long countByCreatorId(UUID creatorId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM TravelReelEntity r WHERE r.creatorId = :creatorId", Long.class);
        query.setParameter("creatorId", creatorId);
        return query.getSingleResult();
    }

    @Override
    public long countByStatus(ApprovalStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM TravelReelEntity r WHERE r.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    @Override
    public long countAll() {
        return count();
    }

    public long countByVisibility(VisibilityScope visibility) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM TravelReelEntity r WHERE r.visibility = :visibility", Long.class);
        query.setParameter("visibility", visibility);
        return query.getSingleResult();
    }

    @Override
    public long countViewsByReelId(UUID reelId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COALESCE(r.viewCount, 0) FROM TravelReelEntity r WHERE r.id = :reelId", Long.class);
        query.setParameter("reelId", reelId);
        List<Long> result = query.getResultList();
        return result.isEmpty() ? 0L : result.get(0);
    }

    @Override
    public List<TravelReel> findMostViewed(int limit) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r ORDER BY r.viewCount DESC", TravelReelEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findMostLiked(int limit) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r ORDER BY r.likeCount DESC", TravelReelEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> findMostCompleted(int limit) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r ORDER BY r.completionRate DESC", TravelReelEntity.class);
        query.setMaxResults(limit);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TravelReel> searchByKeyword(String keyword) {
        return search(keyword);
    }

    @Override
    public List<ReelEngagement> findEngagementsByReelId(UUID reelId) {
        TypedQuery<ReelEngagementEntity> query = entityManager.createQuery(
            "SELECT e FROM ReelEngagementEntity e WHERE e.reelId = :reelId", ReelEngagementEntity.class);
        query.setParameter("reelId", reelId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ReelEngagement> findEngagementsByUserId(UUID userId) {
        TypedQuery<ReelEngagementEntity> query = entityManager.createQuery(
            "SELECT e FROM ReelEngagementEntity e WHERE e.userId = :userId", ReelEngagementEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<ReelEngagement> findEngagementByReelIdAndUserId(UUID reelId, UUID userId) {
        TypedQuery<ReelEngagementEntity> query = entityManager.createQuery(
            "SELECT e FROM ReelEngagementEntity e WHERE e.reelId = :reelId AND e.userId = :userId",
            ReelEngagementEntity.class);
        query.setParameter("reelId", reelId);
        query.setParameter("userId", userId);
        List<ReelEngagementEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(toDomain(results.get(0)));
    }

    @Override
    public List<ReelComment> findCommentsByReelId(UUID reelId) {
        TypedQuery<ReelCommentEntity> query = entityManager.createQuery(
            "SELECT c FROM ReelCommentEntity c WHERE c.reelId = :reelId ORDER BY c.createdAt ASC",
            ReelCommentEntity.class);
        query.setParameter("reelId", reelId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ReelComment> findCommentsByReelIdPaginated(UUID reelId, int page, int pageSize) {
        TypedQuery<ReelCommentEntity> query = entityManager.createQuery(
            "SELECT c FROM ReelCommentEntity c WHERE c.reelId = :reelId ORDER BY c.createdAt ASC",
            ReelCommentEntity.class);
        query.setParameter("reelId", reelId);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ReelComment> findCommentsByUserId(UUID userId) {
        TypedQuery<ReelCommentEntity> query = entityManager.createQuery(
            "SELECT c FROM ReelCommentEntity c WHERE c.userId = :userId ORDER BY c.createdAt DESC",
            ReelCommentEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ReelComment> findRepliesByParentCommentId(UUID parentCommentId) {
        TypedQuery<ReelCommentEntity> query = entityManager.createQuery(
            "SELECT c FROM ReelCommentEntity c WHERE c.parentCommentId = :parentCommentId ORDER BY c.createdAt ASC",
            ReelCommentEntity.class);
        query.setParameter("parentCommentId", parentCommentId);
        return query.getResultList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countCommentsByReelId(UUID reelId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM ReelCommentEntity c WHERE c.reelId = :reelId", Long.class);
        query.setParameter("reelId", reelId);
        return query.getSingleResult();
    }

    @Override
    public long countEngagementsByReelId(UUID reelId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM ReelEngagementEntity e WHERE e.reelId = :reelId", Long.class);
        query.setParameter("reelId", reelId);
        return query.getSingleResult();
    }

    @Override
    public long countLikesByReelId(UUID reelId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM ReelEngagementEntity e WHERE e.reelId = :reelId AND e.engagementType = :type",
            Long.class);
        query.setParameter("reelId", reelId);
        query.setParameter("type", EngagementType.LIKE);
        return query.getSingleResult();
    }

    @Override
    public long countReportsByStatus(ReelReport.ReportStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM ReelReportEntity r WHERE r.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
    private TravelReel toDomain(TravelReelEntity entity) {
        TravelReel.CreatorType creatorType = null;
        if (entity.getCreatorType() != null && !entity.getCreatorType().isBlank()) {
            creatorType = TravelReel.CreatorType.valueOf(entity.getCreatorType());
        }
        TravelReel.RelatedEntityType relatedEntityType = null;
        if (entity.getRelatedEntityType() != null && !entity.getRelatedEntityType().isBlank()) {
            relatedEntityType = TravelReel.RelatedEntityType.valueOf(entity.getRelatedEntityType());
        }
        Location location = null;
        if (entity.getLocationLatitude() != null && entity.getLocationLongitude() != null) {
            location = new Location(entity.getLocationLatitude().doubleValue(),
                entity.getLocationLongitude().doubleValue());
        }
        return new TravelReel(
            entity.getId(),
            entity.getCreatorId(),
            creatorType,
            entity.getVideoUrl(),
            entity.getThumbnailUrl(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getDuration(),
            location,
            entity.getLocationName(),
            entity.getTags(),
            relatedEntityType,
            entity.getRelatedEntityId(),
            entity.getVisibility(),
            entity.getStatus(),
            entity.isPromotional(),
            entity.getViewCount(),
            entity.getUniqueViewCount(),
            entity.getLikeCount(),
            entity.getCommentCount(),
            entity.getShareCount(),
            entity.getBookmarkCount(),
            entity.getAverageWatchTime(),
            entity.getCompletionRate() != null ? entity.getCompletionRate().doubleValue() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getApprovedAt(),
            entity.getApprovedBy()
        );
    }

    private TravelReelEntity toEntity(TravelReel domain) {
        TravelReelEntity entity = new TravelReelEntity();
        entity.setId(domain.getId());
        entity.setCreatorId(domain.getCreatorId());
        entity.setCreatorType(domain.getCreatorType() != null ? domain.getCreatorType().name() : null);
        entity.setVideoUrl(domain.getVideoUrl());
        entity.setThumbnailUrl(domain.getThumbnailUrl());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDuration(domain.getDuration());
        if (domain.getLocation() != null) {
            entity.setLocationLatitude(java.math.BigDecimal.valueOf(domain.getLocation().getLatitude()));
            entity.setLocationLongitude(java.math.BigDecimal.valueOf(domain.getLocation().getLongitude()));
        }
        entity.setLocationName(domain.getLocationName());
        entity.setTags(domain.getTags());
        entity.setRelatedEntityType(domain.getRelatedEntityType() != null ? domain.getRelatedEntityType().name() : null);
        entity.setRelatedEntityId(domain.getRelatedEntityId());
        entity.setVisibility(domain.getVisibility());
        entity.setStatus(domain.getStatus());
        entity.setPromotional(domain.isPromotional());
        entity.setViewCount(domain.getViewCount());
        entity.setUniqueViewCount(domain.getUniqueViewCount());
        entity.setLikeCount(domain.getLikeCount());
        entity.setCommentCount(domain.getCommentCount());
        entity.setShareCount(domain.getShareCount());
        entity.setBookmarkCount(domain.getBookmarkCount());
        entity.setAverageWatchTime(domain.getAverageWatchTime());
        entity.setCompletionRate(domain.getCompletionRate() != null
            ? java.math.BigDecimal.valueOf(domain.getCompletionRate())
            : null);
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setApprovedBy(domain.getApprovedBy());
        return entity;
    }

    private ReelEngagement toDomain(ReelEngagementEntity entity) {
        return new ReelEngagement(
            entity.getId(),
            entity.getReelId(),
            entity.getUserId(),
            entity.getEngagementType(),
            entity.getWatchDuration(),
            entity.getCreatedAt()
        );
    }

    private ReelEngagementEntity toEntity(ReelEngagement domain) {
        ReelEngagementEntity entity = new ReelEngagementEntity();
        entity.setId(domain.getId());
        entity.setReelId(domain.getReelId());
        entity.setUserId(domain.getUserId());
        entity.setEngagementType(domain.getEngagementType());
        entity.setWatchDuration(domain.getWatchDuration());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private ReelComment toDomain(ReelCommentEntity entity) {
        return new ReelComment(
            entity.getId(),
            entity.getReelId(),
            entity.getUserId(),
            entity.getParentCommentId(),
            entity.getContent(),
            entity.getStatus(),
            entity.getLikeCount() != null ? entity.getLikeCount() : 0,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private ReelCommentEntity toEntity(ReelComment domain) {
        ReelCommentEntity entity = new ReelCommentEntity();
        entity.setId(domain.getId());
        entity.setReelId(domain.getReelId());
        entity.setUserId(domain.getUserId());
        entity.setParentCommentId(domain.getParentCommentId());
        entity.setContent(domain.getContent());
        entity.setStatus(domain.getStatus());
        entity.setLikeCount(domain.getLikeCount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private ReelReport toDomain(ReelReportEntity entity) {
        return new ReelReport(
            entity.getId(),
            entity.getReelId(),
            entity.getReportedBy(),
            entity.getReason(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getReviewedBy(),
            entity.getAdminNotes(),
            entity.getCreatedAt(),
            entity.getReviewedAt()
        );
    }

    private ReelReportEntity toEntity(ReelReport domain) {
        ReelReportEntity entity = new ReelReportEntity();
        entity.setId(domain.getId());
        entity.setReelId(domain.getReelId());
        entity.setReportedBy(domain.getReportedBy());
        entity.setReason(domain.getReason());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setReviewedBy(domain.getReviewedBy());
        entity.setAdminNotes(domain.getAdminNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setReviewedAt(domain.getReviewedAt());
        return entity;
    }
}
