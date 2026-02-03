package com.travelplatform.application.dto.response.review;

import com.travelplatform.domain.enums.ApprovalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for review response.
 */
public class ReviewResponse {

    private UUID id;
    private UUID reviewerId;
    private String reviewerName;
    private String reviewerPhotoUrl;
    private UUID accommodationId;
    private String accommodationTitle;
    private UUID bookingId;
    private Integer overallRating;
    private Integer cleanlinessRating;
    private Integer accuracyRating;
    private Integer communicationRating;
    private Integer locationRating;
    private Integer valueRating;
    private String title;
    private String content;
    private String pros;
    private String cons;
    private String travelType;
    private LocalDate stayedDate;
    private Boolean isVerified;
    private ApprovalStatus status;
    private Integer helpfulCount;
    private String responseFromHost;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private List<String> photoUrls;
    private Boolean isHelpfulToCurrentUser;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReviewerId() { return reviewerId; }
    public void setReviewerId(UUID reviewerId) { this.reviewerId = reviewerId; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getReviewerPhotoUrl() { return reviewerPhotoUrl; }
    public void setReviewerPhotoUrl(String reviewerPhotoUrl) { this.reviewerPhotoUrl = reviewerPhotoUrl; }
    public UUID getAccommodationId() { return accommodationId; }
    public void setAccommodationId(UUID accommodationId) { this.accommodationId = accommodationId; }
    public String getAccommodationTitle() { return accommodationTitle; }
    public void setAccommodationTitle(String accommodationTitle) { this.accommodationTitle = accommodationTitle; }
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public Integer getOverallRating() { return overallRating; }
    public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }
    public Integer getCleanlinessRating() { return cleanlinessRating; }
    public void setCleanlinessRating(Integer cleanlinessRating) { this.cleanlinessRating = cleanlinessRating; }
    public Integer getAccuracyRating() { return accuracyRating; }
    public void setAccuracyRating(Integer accuracyRating) { this.accuracyRating = accuracyRating; }
    public Integer getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(Integer communicationRating) { this.communicationRating = communicationRating; }
    public Integer getLocationRating() { return locationRating; }
    public void setLocationRating(Integer locationRating) { this.locationRating = locationRating; }
    public Integer getValueRating() { return valueRating; }
    public void setValueRating(Integer valueRating) { this.valueRating = valueRating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPros() { return pros; }
    public void setPros(String pros) { this.pros = pros; }
    public String getCons() { return cons; }
    public void setCons(String cons) { this.cons = cons; }
    public String getTravelType() { return travelType; }
    public void setTravelType(String travelType) { this.travelType = travelType; }
    public LocalDate getStayedDate() { return stayedDate; }
    public void setStayedDate(LocalDate stayedDate) { this.stayedDate = stayedDate; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public Integer getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }
    public String getResponseFromHost() { return responseFromHost; }
    public void setResponseFromHost(String responseFromHost) { this.responseFromHost = responseFromHost; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
    public Boolean getIsHelpfulToCurrentUser() { return isHelpfulToCurrentUser; }
    public void setIsHelpfulToCurrentUser(Boolean isHelpfulToCurrentUser) { this.isHelpfulToCurrentUser = isHelpfulToCurrentUser; }
}
