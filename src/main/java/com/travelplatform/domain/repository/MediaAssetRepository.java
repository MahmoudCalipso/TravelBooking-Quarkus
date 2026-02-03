package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.media.MediaAsset;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for MediaAsset domain entity.
 * Defines the contract for media asset data access operations.
 * 
 * Media assets are stored in Firebase Storage, with only metadata tracked in the database.
 */
public interface MediaAssetRepository {

    /**
     * Save a new media asset or update an existing one.
     * 
     * @param mediaAsset the media asset to save
     * @return the saved media asset
     */
    MediaAsset save(MediaAsset mediaAsset);

    /**
     * Find a media asset by its unique identifier.
     * 
     * @param id the media asset ID
     * @return Optional containing the media asset if found
     */
    Optional<MediaAsset> findById(UUID id);

    /**
     * Find all media assets owned by a specific user.
     * 
     * @param ownerId the owner's user ID
     * @return list of media assets owned by the user
     */
    List<MediaAsset> findByOwnerId(UUID ownerId);

    /**
     * Find all media assets for a specific owner and owner type.
     * 
     * @param ownerId the owner's ID
     * @param ownerType the type of owner (USER, ACCOMMODATION, etc.)
     * @return list of media assets for the specified owner
     */
    List<MediaAsset> findByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType);

    /**
     * Find all media assets of a specific type (IMAGE, VIDEO, AUDIO, DOCUMENT).
     * 
     * @param mediaType the media type
     * @return list of media assets of the specified type
     */
    List<MediaAsset> findByMediaType(MediaAsset.MediaType mediaType);

    /**
     * Find all media assets for a specific owner, owner type, and media type.
     * 
     * @param ownerId the owner's ID
     * @param ownerType the type of owner
     * @param mediaType the media type
     * @return list of matching media assets
     */
    List<MediaAsset> findByOwnerIdAndOwnerTypeAndMediaType(UUID ownerId, MediaAsset.OwnerType ownerType, MediaAsset.MediaType mediaType);

    /**
     * Find a media asset by its Firebase storage path.
     * 
     * @param firebasePath the Firebase storage path
     * @return Optional containing the media asset if found
     */
    Optional<MediaAsset> findByFirebasePath(String firebasePath);

    /**
     * Find all media assets.
     * 
     * @return list of all media assets
     */
    List<MediaAsset> findAll();

    /**
     * Delete a media asset by its ID.
     * 
     * @param id the media asset ID
     */
    void deleteById(UUID id);

    /**
     * Delete all media assets owned by a specific user.
     * 
     * @param ownerId the owner's user ID
     */
    void deleteByOwnerId(UUID ownerId);

    /**
     * Delete all media assets for a specific owner and owner type.
     * 
     * @param ownerId the owner's ID
     * @param ownerType the type of owner
     */
    void deleteByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType);

    /**
     * Check if a media asset exists by its ID.
     * 
     * @param id the media asset ID
     * @return true if the media asset exists
     */
    boolean existsById(UUID id);

    /**
     * Count the total number of media assets owned by a user.
     * 
     * @param ownerId the owner's user ID
     * @return the count of media assets
     */
    long countByOwnerId(UUID ownerId);

    /**
     * Count the total number of media assets for a specific owner and owner type.
     * 
     * @param ownerId the owner's ID
     * @param ownerType the type of owner
     * @return the count of media assets
     */
    long countByOwnerIdAndOwnerType(UUID ownerId, MediaAsset.OwnerType ownerType);

    /**
     * Count the total number of media assets of a specific type.
     * 
     * @param mediaType the media type
     * @return the count of media assets
     */
    long countByMediaType(MediaAsset.MediaType mediaType);

    /**
     * Calculate the total storage size in bytes for a specific owner.
     * 
     * @param ownerId the owner's user ID
     * @return total size in bytes
     */
    long getTotalSizeByOwnerId(UUID ownerId);
}
