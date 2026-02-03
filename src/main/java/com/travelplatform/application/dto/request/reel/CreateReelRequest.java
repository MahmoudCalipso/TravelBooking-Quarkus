package com.travelplatform.application.dto.request.reel;

import com.travelplatform.domain.model.reel.TravelReel;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating travel reel request.
 */
public class CreateReelRequest {

    @NotBlank(message = "Video URL is required")
    @Size(max = 500, message = "Video URL must be less than 500 characters")
    private String videoUrl;

    @NotBlank(message = "Thumbnail URL is required")
    @Size(max = 500, message = "Thumbnail URL must be less than 500 characters")
    private String thumbnailUrl;

    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 second")
    @Max(value = 90, message = "Duration cannot exceed 90 seconds")
    private Integer duration;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal locationLatitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal locationLongitude;

    @Size(max = 255, message = "Location name must be less than 255 characters")
    private String locationName;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<String> tags;

    @Pattern(regexp = "ACCOMMODATION|EVENT|DESTINATION", message = "Invalid related entity type")
    private String relatedEntityType;

    private UUID relatedEntityId;

    @Pattern(regexp = "PUBLIC|FOLLOWERS_ONLY|PRIVATE", message = "Invalid visibility scope")
    private String visibility;

    @NotNull(message = "Creator type is required")
    @Pattern(regexp = "TRAVELER|SUPPLIER_SUBSCRIBER", message = "Invalid creator type")
    private TravelReel.CreatorType creatorType;

    // Getters and Setters

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(BigDecimal locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public BigDecimal getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(BigDecimal locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public UUID getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(UUID relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public TravelReel.CreatorType getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(TravelReel.CreatorType creatorType) {
        this.creatorType = creatorType;
    }
}
