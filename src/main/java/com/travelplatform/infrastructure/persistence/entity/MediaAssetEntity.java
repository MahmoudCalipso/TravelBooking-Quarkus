package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.model.media.MediaAsset;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity for MediaAsset.
 * Maps the MediaAsset domain entity to the database table.
 * 
 * This entity stores only metadata about media files.
 * The actual file content is stored in Firebase Storage.
 */
@Entity
@Table(name = "media_assets", indexes = {
        @Index(name = "idx_media_owner_id", columnList = "owner_id"),
        @Index(name = "idx_media_owner_type", columnList = "owner_type"),
        @Index(name = "idx_media_type", columnList = "media_type"),
        @Index(name = "idx_media_firebase_path", columnList = "firebase_path"),
        @Index(name = "idx_media_owner_composite", columnList = "owner_id, owner_type, media_type")
})
public class MediaAssetEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 50)
    private MediaAsset.OwnerType ownerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaAsset.MediaType mediaType;

    @Column(name = "firebase_path", nullable = false, length = 500)
    private String firebasePath;

    @Column(name = "public_url", nullable = false, length = 1000)
    private String publicUrl;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA
     */
    public MediaAssetEntity() {
    }

    /**
     * Constructor from domain entity
     */
    public MediaAssetEntity(MediaAsset mediaAsset) {
        this.id = mediaAsset.getId();
        this.ownerId = mediaAsset.getOwnerId();
        this.ownerType = mediaAsset.getOwnerType();
        this.mediaType = mediaAsset.getMediaType();
        this.firebasePath = mediaAsset.getFirebasePath();
        this.publicUrl = mediaAsset.getPublicUrl();
        this.sizeBytes = mediaAsset.getSizeBytes();
        this.mimeType = mediaAsset.getMimeType();
        this.createdAt = mediaAsset.getCreatedAt();
        this.updatedAt = mediaAsset.getUpdatedAt();
    }

    /**
     * Convert to domain entity
     */
    public MediaAsset toDomain() {
        MediaAsset mediaAsset = new MediaAsset(
                this.ownerId,
                this.ownerType,
                this.mediaType,
                this.firebasePath,
                this.publicUrl,
                this.sizeBytes,
                this.mimeType);
        mediaAsset.setId(this.id);
        mediaAsset.setCreatedAt(this.createdAt);
        mediaAsset.setUpdatedAt(this.updatedAt);
        return mediaAsset;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public MediaAsset.OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(MediaAsset.OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public MediaAsset.MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaAsset.MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getFirebasePath() {
        return firebasePath;
    }

    public void setFirebasePath(String firebasePath) {
        this.firebasePath = firebasePath;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // equals, hashCode, toString

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MediaAssetEntity that = (MediaAssetEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MediaAssetEntity{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", ownerType=" + ownerType +
                ", mediaType=" + mediaType +
                ", firebasePath='" + firebasePath + '\'' +
                ", publicUrl='" + publicUrl + '\'' +
                ", sizeBytes=" + sizeBytes +
                ", mimeType='" + mimeType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * JPA lifecycle callback - set updated timestamp before persist
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA lifecycle callback - set updated timestamp before update
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
