package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.model.media.MediaAsset;
import com.travelplatform.domain.repository.MediaAssetRepository;
import com.travelplatform.infrastructure.persistence.entity.MediaAssetEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository implementation for MediaAsset.
 */
@ApplicationScoped
public class JpaMediaAssetRepository implements MediaAssetRepository {

    @Inject
    EntityManager entityManager;

    @Override
    @Transactional
    public MediaAsset save(MediaAsset mediaAsset) {
        MediaAssetEntity entity = new MediaAssetEntity(mediaAsset);
        MediaAssetEntity existing = entityManager.find(MediaAssetEntity.class, entity.getId());
        if (existing == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        return entity.toDomain();
    }

    @Override
    public Optional<MediaAsset> findById(UUID id) {
        MediaAssetEntity entity = entityManager.find(MediaAssetEntity.class, id);
        return entity != null ? Optional.of(entity.toDomain()) : Optional.empty();
    }

    @Override
    public List<MediaAsset> findByOwnerId(UUID ownerId) {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m WHERE m.ownerId = :ownerId", MediaAssetEntity.class);
        query.setParameter("ownerId", ownerId);
        return query.getResultList().stream().map(MediaAssetEntity::toDomain).toList();
    }

    @Override
    public List<MediaAsset> findByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType) {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m WHERE m.ownerId = :ownerId AND m.ownerType = :ownerType",
                MediaAssetEntity.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("ownerType", ownerType);
        return query.getResultList().stream().map(MediaAssetEntity::toDomain).toList();
    }

    @Override
    public List<MediaAsset> findByMediaType(MediaAsset.MediaType mediaType) {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m WHERE m.mediaType = :mediaType", MediaAssetEntity.class);
        query.setParameter("mediaType", mediaType);
        return query.getResultList().stream().map(MediaAssetEntity::toDomain).toList();
    }

    @Override
    public List<MediaAsset> findByOwnerIdAndOwnerTypeAndMediaType(UUID ownerId, MediaAsset.OwnerType ownerType,
            MediaAsset.MediaType mediaType) {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m WHERE m.ownerId = :ownerId AND m.ownerType = :ownerType AND m.mediaType = :mediaType",
                MediaAssetEntity.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("ownerType", ownerType);
        query.setParameter("mediaType", mediaType);
        return query.getResultList().stream().map(MediaAssetEntity::toDomain).toList();
    }

    @Override
    public Optional<MediaAsset> findByFirebasePath(String firebasePath) {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m WHERE m.firebasePath = :firebasePath", MediaAssetEntity.class);
        query.setParameter("firebasePath", firebasePath);
        query.setMaxResults(1);
        List<MediaAssetEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0).toDomain());
    }

    @Override
    public Optional<MediaAsset> findByPublicUrl(String publicUrl) {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m WHERE m.publicUrl = :publicUrl", MediaAssetEntity.class);
        query.setParameter("publicUrl", publicUrl);
        query.setMaxResults(1);
        List<MediaAssetEntity> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0).toDomain());
    }

    @Override
    public List<MediaAsset> findAll() {
        TypedQuery<MediaAssetEntity> query = entityManager.createQuery(
                "SELECT m FROM MediaAssetEntity m", MediaAssetEntity.class);
        return query.getResultList().stream().map(MediaAssetEntity::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        MediaAssetEntity entity = entityManager.find(MediaAssetEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional
    public void deleteByOwnerId(UUID ownerId) {
        entityManager.createQuery("DELETE FROM MediaAssetEntity m WHERE m.ownerId = :ownerId")
                .setParameter("ownerId", ownerId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType) {
        entityManager
                .createQuery("DELETE FROM MediaAssetEntity m WHERE m.ownerId = :ownerId AND m.ownerType = :ownerType")
                .setParameter("ownerId", ownerId)
                .setParameter("ownerType", ownerType)
                .executeUpdate();
    }

    @Override
    public boolean existsById(UUID id) {
        return entityManager.find(MediaAssetEntity.class, id) != null;
    }

    @Override
    public long countByOwnerId(UUID ownerId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM MediaAssetEntity m WHERE m.ownerId = :ownerId", Long.class);
        query.setParameter("ownerId", ownerId);
        return query.getSingleResult();
    }

    @Override
    public long countByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM MediaAssetEntity m WHERE m.ownerId = :ownerId AND m.ownerType = :ownerType",
                Long.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("ownerType", ownerType);
        return query.getSingleResult();
    }

    @Override
    public long countByMediaType(MediaAsset.MediaType mediaType) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM MediaAssetEntity m WHERE m.mediaType = :mediaType", Long.class);
        query.setParameter("mediaType", mediaType);
        return query.getSingleResult();
    }

    @Override
    public long getTotalSizeByOwnerId(UUID ownerId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COALESCE(SUM(m.sizeBytes), 0) FROM MediaAssetEntity m WHERE m.ownerId = :ownerId", Long.class);
        query.setParameter("ownerId", ownerId);
        return query.getSingleResult();
    }
}
