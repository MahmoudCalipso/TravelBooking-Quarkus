package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.EngagementType;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.infrastructure.persistence.entity.TravelReelEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
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
public class JpaTravelReelRepository implements TravelReelRepository, PanacheRepository<TravelReelEntity> {

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
    public Optional<TravelReel> findById(UUID id) {
        TravelReelEntity entity = entityManager.find(TravelReelEntity.class, id);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
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
    public List<TravelReel> findByStatus(ApprovalStatus status) {
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE r.status = :status", TravelReelEntity.class);
        query.setParameter("status", status);
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
    public List<TravelReel> findByLocation(Location location, double radiusKm) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double radius = radiusKm;
        
        TypedQuery<TravelReelEntity> query = entityManager.createQuery(
            "SELECT r FROM TravelReelEntity r WHERE " +
            "r.status = :status AND " +
            "r.latitude IS NOT NULL AND r.longitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(r.latitude)))) <= :radius", TravelReelEntity.class);
        query.setParameter("status", ApprovalStatus.APPROVED);
        query.setParameter("lat", lat);
        query.setParameter("lng", lng);
        query.setParameter("radius", radius);
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
    public long countByVisibility(VisibilityScope visibility) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM TravelReelEntity r WHERE r.visibility = :visibility", Long.class);
        query.setParameter("visibility", visibility);
        return query.getSingleResult();
    }

    // Helper methods for Entity <-> Domain conversion
    private TravelReel toDomain(TravelReelEntity entity) {
        return new TravelReel(
            entity.getId(),
            entity.getCreatorId(),
            entity.getCreatorType(),
            entity.getVideoUrl(),
            entity.getThumbnailUrl(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getDuration(),
            entity.getLatitude() != null && entity.getLongitude() != null 
                ? new Location(entity.getLatitude(), entity.getLongitude()) 
                : null,
            entity.getLocationName(),
            entity.getTags(),
            entity.getRelatedEntityType(),
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
            entity.getCompletionRate(),
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
        entity.setCreatorType(domain.getCreatorType());
        entity.setVideoUrl(domain.getVideoUrl());
        entity.setThumbnailUrl(domain.getThumbnailUrl());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDuration(domain.getDuration());
        if (domain.getLocation() != null) {
            entity.setLatitude(domain.getLocation().getLatitude());
            entity.setLongitude(domain.getLocation().getLongitude());
        }
        entity.setLocationName(domain.getLocationName());
        entity.setTags(domain.getTags());
        entity.setRelatedEntityType(domain.getRelatedEntityType());
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
        entity.setCompletionRate(domain.getCompletionRate());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setApprovedBy(domain.getApprovedBy());
        return entity;
    }
}
