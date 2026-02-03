package com.travelplatform.application.dto.response.event;

import com.travelplatform.domain.enums.ApprovalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for event response.
 */
public class EventResponse {

    private UUID id;
    private UUID creatorId;
    private String creatorName;
    private String creatorPhotoUrl;
    private String title;
    private String description;
    private String eventType;
    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal pricePerPerson;
    private String currency;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private ApprovalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private Boolean isRegisteredByCurrentUser;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCreatorId() { return creatorId; }
    public void setCreatorId(UUID creatorId) { this.creatorId = creatorId; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getCreatorPhotoUrl() { return creatorPhotoUrl; }
    public void setCreatorPhotoUrl(String creatorPhotoUrl) { this.creatorPhotoUrl = creatorPhotoUrl; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public BigDecimal getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(BigDecimal pricePerPerson) { this.pricePerPerson = pricePerPerson; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public Boolean getIsRegisteredByCurrentUser() { return isRegisteredByCurrentUser; }
    public void setIsRegisteredByCurrentUser(Boolean isRegisteredByCurrentUser) { this.isRegisteredByCurrentUser = isRegisteredByCurrentUser; }
}
