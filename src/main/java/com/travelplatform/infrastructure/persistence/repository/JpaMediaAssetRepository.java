package com.travelplatform.infrastructure.persistence.repository;

import com.travelplatform.domain.model.media.MediaAsset;
import com.travelplatform.domain.repository.MediaAssetRepository;
import com.travelplatform.infrastructure.persistence.entity.MediaAssetEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository implementation for MediaAsset.
 * Implements the MediaAssetRepository interface using Panache.
 */
@ApplicationScoped
public class JpaMediaAssetRepository implements MediaAssetRepository {

    /**
     * Save a media asset to the database.
     */
    @Override
    @Transactional
    public MediaAsset save(MediaAsset mediaAsset) {
        MediaAssetEntity entity = new MediaAssetEntity(mediaAsset);
        entity.persist();
        return entity.toDomain();
    }

    /**
     * Find a media asset by its ID.
     */
    @Override
    public Optional<MediaAsset> findById(UUID id) {
        MediaAssetEntity entity = MediaAssetEntity.findById(id);
        return entity != null ? Optional.of(entity.toDomain()) : Optional.empty();
    }

    /**
     * Find all media assets owned by a specific user.
     */
    @Override
    public List<MediaAsset> findByOwnerId(UUID ownerId) {
        return MediaAssetEntity.list("ownerId", ownerId)
                .stream()
                .map(MediaAssetEntity::toDomain)
                .toList();
    }

    /**
     * Find all media assets for a specific owner and owner type.
     */
    @Override
    public List<MediaAsset> findByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType) {
        return MediaAssetEntity.list("ownerId = ?1 and ownerType = ?2", ownerId, ownerType)
                .stream()
                .map(MediaAssetEntity::toDomain)
                .toList();
    }

    /**
     * Find all media assets of a specific type.
     */
    @Override
    public List<MediaAsset> findByMediaType(MediaAsset.MediaType mediaType) {
        return MediaAssetEntity.list("mediaType", mediaType)
                .stream()
                .map(MediaAssetEntity::toDomain)
                .toList();
    }

    /**
     * Find all media assets for a specific owner, owner type, and media type.
     */
    @Override
    public List<MediaAsset> findByOwnerIdAndOwnerTypeAndMediaType(UUID ownerId, MediaAsset.OwnerType ownerType, MediaAsset.MediaType mediaType) {
        return MediaAssetEntity.list("ownerId = ?1 and ownerType = ?2 and mediaType = ?3", ownerId, ownerType, mediaType)
                .stream()
                .map(MediaAssetEntity::toDomain)
                .toList();
    }

    /**
     * Find a media asset by its Firebase storage path.
     */
    @Override
    public Optional<MediaAsset> findByFirebasePath(String firebasePath) {
        List<MediaAssetEntity> results = MediaAssetEntity.list("firebasePath", firebasePath);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0).toDomain());
    }

    /**
     * Find all media assets.
     */
    @Override
    public List<MediaAsset> findAll() {
        return MediaAssetEntity.listAll()
                .stream()
                .map(MediaAssetEntity::toDomain)
                .toList();
    }

    /**
     * Delete a media asset by its ID.
     */
    @Override
    @Transactional
    public void deleteById(UUID id) {
        MediaAssetEntity.deleteById(id);
    }

    /**
     * Delete all media assets owned by a specific user.
     */
    @Override
    @Transactional
    public void deleteByOwnerId(UUID ownerId) {
        MediaAssetEntity.delete("ownerId", ownerId);
    }

    /**
     * Delete all media assets for a specific owner and owner type.
     */
    @Override
    @Transactional
    public void deleteByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType) {
        MediaAssetEntity.delete("ownerId = ?1 and ownerType = ?2", ownerId, ownerType);
    }

    /**
     * Check if a media asset exists by its ID.
     */
    @Override
    public boolean existsById(UUID id) {
        return MediaAssetEntity.count("id", id) > 0;
    }

    /**
     * Count total number of media assets owned by a user.
     */
    @Override
    public long countByOwnerId(UUID ownerId) {
        return MediaAssetEntity.count("ownerId", ownerId);
    }

    /**
     * Count total number of media assets for a specific owner and owner type.
     */
    @Override
    public long countByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType) {
        return MediaAssetEntity.count("ownerId = ?1 and ownerType = ?2", ownerId, ownerType);
    }

    /**
     * Count total number of media assets of a specific type.
     */
    @Override
    public long countByMediaType(MediaAsset.MediaType mediaType) {
        return MediaAssetEntity.count("mediaType", mediaType);
    }

    /**
     * Calculate total storage size in bytes for a specific owner.
     */
    @Override
    public long getTotalSizeByOwnerId(UUID ownerId) {
        List<MediaAssetEntity> assets = MediaAssetEntity.list("ownerId", ownerId);
        return assets.stream()
                .mapToLong(MediaAssetEntity::getSizeBytes)
                .sum();
    }
}
