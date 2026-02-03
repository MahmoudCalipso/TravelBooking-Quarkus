package com.travelplatform.domain.model.reel;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.valueobject.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a travel reel (short video content).
 * This is the aggregate root for the reel aggregate.
 */
public class TravelReel {
    private final UUID id;
    private final UUID creatorId;
    private final CreatorType creatorType;
    private final String videoUrl;
    private final String thumbnailUrl;
    private String title;
    private String description;
    private final int duration;
    private Location location;
    private String locationName;
    private List<String> tags;
    private RelatedEntityType relatedEntityType;
    private UUID relatedEntityId;
    private VisibilityScope visibility;
    private ApprovalStatus status;
    private boolean isPromotional;
    private long viewCount;
    private long uniqueViewCount;
    private long likeCount;
    private int commentCount;
    private long shareCount;
    private long bookmarkCount;
    private Integer averageWatchTime;
    private Double completionRate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private UUID approvedBy;

    // Associated entities (part of the aggregate)
    private List<ReelEngagement> engagements;
    private List<ReelComment> comments;

    /**
     * Creates a new TravelReel.
     *
     * @param creatorId     creator user ID
     * @param creatorType   creator type (TRAVELER or SUPPLIER_SUBSCRIBER)
     * @param videoUrl      video file URL
     * @param thumbnailUrl  thumbnail image URL
     * @param duration      video duration in seconds (max 90)
     * @param location      GPS location where video was filmed
     * @param locationName  location name (e.g., "Paris, France")
     * @param visibility    visibility scope
     * @param isPromotional whether this is promotional content
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public TravelReel(UUID creatorId, CreatorType creatorType, String videoUrl, String thumbnailUrl,
            int duration, Location location, String locationName, VisibilityScope visibility,
            boolean isPromotional) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
        if (creatorType == null) {
            throw new IllegalArgumentException("Creator type cannot be null");
        }
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Video URL cannot be null or empty");
        }
        if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Thumbnail URL cannot be null or empty");
        }
        if (duration <= 0 || duration > 90) {
            throw new IllegalArgumentException("Duration must be between 1 and 90 seconds");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (visibility == null) {
            throw new IllegalArgumentException("Visibility cannot be null");
        }

        this.id = UUID.randomUUID();
        this.creatorId = creatorId;
        this.creatorType = creatorType;
        this.videoUrl = videoUrl.trim();
        this.thumbnailUrl = thumbnailUrl.trim();
        this.title = null;
        this.description = null;
        this.duration = duration;
        this.location = location;
        this.locationName = locationName;
        this.tags = new ArrayList<>();
        this.relatedEntityType = null;
        this.relatedEntityId = null;
        this.visibility = visibility;
        this.status = ApprovalStatus.PENDING;
        this.isPromotional = isPromotional;
        this.viewCount = 0;
        this.uniqueViewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.shareCount = 0;
        this.bookmarkCount = 0;
        this.averageWatchTime = null;
        this.completionRate = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvedAt = null;
        this.approvedBy = null;
        this.engagements = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    /**
     * Reconstructs a TravelReel from persistence.
     */
    public TravelReel(UUID id, UUID creatorId, CreatorType creatorType, String videoUrl, String thumbnailUrl,
            String title, String description, int duration, Location location, String locationName,
            List<String> tags, RelatedEntityType relatedEntityType, UUID relatedEntityId,
            VisibilityScope visibility, ApprovalStatus status, boolean isPromotional,
            long viewCount, long uniqueViewCount, long likeCount, int commentCount,
            long shareCount, long bookmarkCount, Integer averageWatchTime,
            Double completionRate, LocalDateTime createdAt, LocalDateTime updatedAt,
            LocalDateTime approvedAt, UUID approvedBy) {
        this.id = id;
        this.creatorId = creatorId;
        this.creatorType = creatorType;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.location = location;
        this.locationName = locationName;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.visibility = visibility;
        this.status = status;
        this.isPromotional = isPromotional;
        this.viewCount = viewCount;
        this.uniqueViewCount = uniqueViewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.bookmarkCount = bookmarkCount;
        this.averageWatchTime = averageWatchTime;
        this.completionRate = completionRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.engagements = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public CreatorType getCreatorType() {
        return creatorType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public int getDuration() {
        return duration;
    }

    public Location getLocation() {
        return location;
    }

    public String getLocationName() {
        return locationName;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public RelatedEntityType getRelatedEntityType() {
        return relatedEntityType;
    }

    public UUID getRelatedEntityId() {
        return relatedEntityId;
    }

    public VisibilityScope getVisibility() {
        return visibility;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public boolean isPromotional() {
        return isPromotional;
    }

    public void setVisibility(VisibilityScope visibility) {
        this.visibility = visibility;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPromotional(boolean isPromotional) {
        this.isPromotional = isPromotional;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public void setLocation(Location location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
        this.updatedAt = LocalDateTime.now();
    }

    public long getViewCount() {
        return viewCount;
    }

    public long getUniqueViewCount() {
        return uniqueViewCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public long getShareCount() {
        return shareCount;
    }

    public long getBookmarkCount() {
        return bookmarkCount;
    }

    public Integer getAverageWatchTime() {
        return averageWatchTime;
    }

    public Double getCompletionRate() {
        return completionRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public List<ReelEngagement> getEngagements() {
        return new ArrayList<>(engagements);
    }

    public List<ReelComment> getComments() {
        return new ArrayList<>(comments);
    }

    /**
     * Updates the reel details.
     */
    public void updateDetails(String title, String description, String locationName, List<String> tags) {
        this.title = title;
        this.description = description;
        this.locationName = locationName;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Links the reel to a related entity (accommodation, event, etc.).
     *
     * @param entityType type of related entity
     * @param entityId   ID of related entity
     */
    public void linkToEntity(RelatedEntityType entityType, UUID entityId) {
        this.relatedEntityType = entityType;
        this.relatedEntityId = entityId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Unlinks the reel from any related entity.
     */
    public void unlinkFromEntity() {
        this.relatedEntityType = null;
        this.relatedEntityId = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approves the reel.
     *
     * @param approvedBy admin user ID who approved
     */
    public void approve(UUID approvedBy) {
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approved by cannot be null");
        }
        this.status = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rejects the reel.
     */
    public void reject() {
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(UUID rejectedBy) {
        this.status = ApprovalStatus.REJECTED;
        this.approvedBy = rejectedBy; // Reuse approvedBy field or ignore if strict
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Flags the reel for review.
     */
    public void flag() {
        this.status = ApprovalStatus.FLAGGED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the view count.
     */
    public void incrementViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the unique view count.
     */
    public void incrementUniqueViewCount() {
        this.uniqueViewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the like count.
     */
    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrements the like count.
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Increments the comment count.
     */
    public void incrementCommentCount() {
        this.commentCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrements the comment count.
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Increments the share count.
     */
    public void incrementShareCount() {
        this.shareCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the bookmark count.
     */
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrements the bookmark count.
     */
    public void decrementBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the average watch time.
     *
     * @param averageWatchTime new average watch time in seconds
     */
    public void updateAverageWatchTime(Integer averageWatchTime) {
        this.averageWatchTime = averageWatchTime;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the completion rate.
     *
     * @param completionRate new completion rate (0-100)
     */
    public void updateCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an engagement to the reel.
     *
     * @param engagement reel engagement
     */
    public void addEngagement(ReelEngagement engagement) {
        if (engagement != null) {
            this.engagements.add(engagement);
        }
    }

    /**
     * Adds a comment to the reel.
     *
     * @param comment reel comment
     */
    public void addComment(ReelComment comment) {
        if (comment != null) {
            this.comments.add(comment);
        }
    }

    /**
     * Checks if the reel is approved.
     *
     * @return true if status is APPROVED
     */
    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    /**
     * Checks if the reel is visible (approved and visibility allows).
     *
     * @return true if reel is visible
     */
    public boolean isVisible() {
        return isApproved() && this.visibility != VisibilityScope.PRIVATE;
    }

    /**
     * Checks if the reel is promotional.
     *
     * @return true if is promotional
     */
    public boolean isPromotionalContent() {
        return this.isPromotional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TravelReel that = (TravelReel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("TravelReel{id=%s, creatorId=%s, title='%s', status=%s}",
                id, creatorId, title, status);
    }

    /**
     * Enumeration of creator types.
     */
    public enum CreatorType {
        TRAVELER,
        SUPPLIER_SUBSCRIBER
    }

    /**
     * Enumeration of related entity types.
     */
    public enum RelatedEntityType {
        ACCOMMODATION,
        EVENT,
        DESTINATION
    }
}
