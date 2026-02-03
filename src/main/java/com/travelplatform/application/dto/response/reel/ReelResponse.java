package com.travelplatform.application.dto.response.reel;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.VisibilityScope;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for travel reel response.
 */
public class ReelResponse {

    private UUID id;
    private UUID creatorId;
    private String creatorName;
    private String creatorPhotoUrl;
    private String creatorType;
    private String videoUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Integer duration;
    private BigDecimal locationLatitude;
    private BigDecimal locationLongitude;
    private String locationName;
    private List<String> tags;
    private String relatedEntityType;
    private UUID relatedEntityId;
    private String relatedEntityTitle;
    private VisibilityScope visibility;
    private ApprovalStatus status;
    private Boolean isPromotional;
    private Long viewCount;
    private Long uniqueViewCount;
    private Long likeCount;
    private Integer commentCount;
    private Long shareCount;
    private Long bookmarkCount;
    private Integer averageWatchTime;
    private BigDecimal completionRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private Boolean isLikedByCurrentUser;
    private Boolean isBookmarkedByCurrentUser;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCreatorId() { return creatorId; }
    public void setCreatorId(UUID creatorId) { this.creatorId = creatorId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getCreatorPhotoUrl() { return creatorPhotoUrl; }
    public void setCreatorPhotoUrl(String creatorPhotoUrl) { this.creatorPhotoUrl = creatorPhotoUrl; }
    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public BigDecimal getLocationLatitude() { return locationLatitude; }
    public void setLocationLatitude(BigDecimal locationLatitude) { this.locationLatitude = locationLatitude; }
    public BigDecimal getLocationLongitude() { return locationLongitude; }
    public void setLocationLongitude(BigDecimal locationLongitude) { this.locationLongitude = locationLongitude; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }
    public UUID getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(UUID relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public String getRelatedEntityTitle() { return relatedEntityTitle; }
    public void setRelatedEntityTitle(String relatedEntityTitle) { this.relatedEntityTitle = relatedEntityTitle; }
    public VisibilityScope getVisibility() { return visibility; }
    public void setVisibility(VisibilityScope visibility) { this.visibility = visibility; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public Boolean getIsPromotional() { return isPromotional; }
    public void setIsPromotional(Boolean isPromotional) { this.isPromotional = isPromotional; }
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public Long getUniqueViewCount() { return uniqueViewCount; }
    public void setUniqueViewCount(Long uniqueViewCount) { this.uniqueViewCount = uniqueViewCount; }
    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Long getShareCount() { return shareCount; }
    public void setShareCount(Long shareCount) { this.shareCount = shareCount; }
    public Long getBookmarkCount() { return bookmarkCount; }
    public void setBookmarkCount(Long bookmarkCount) { this.bookmarkCount = bookmarkCount; }
    public Integer getAverageWatchTime() { return averageWatchTime; }
    public void setAverageWatchTime(Integer averageWatchTime) { this.averageWatchTime = averageWatchTime; }
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public Boolean getIsLikedByCurrentUser() { return isLikedByCurrentUser; }
    public void setIsLikedByCurrentUser(Boolean isLikedByCurrentUser) { this.isLikedByCurrentUser = isLikedByCurrentUser; }
    public Boolean getIsBookmarkedByCurrentUser() { return isBookmarkedByCurrentUser; }
    public void setIsBookmarkedByCurrentUser(Boolean isBookmarkedByCurrentUser) { this.isBookmarkedByCurrentUser = isBookmarkedByCurrentUser; }
}
