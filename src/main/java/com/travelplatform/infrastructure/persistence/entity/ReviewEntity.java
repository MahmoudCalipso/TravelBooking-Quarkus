package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.enums.ApprovalStatus;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for reviews table.
 * This is the persistence model for Review domain entity.
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_accommodation_id", columnList = "accommodation_id"),
    @Index(name = "idx_reviews_reviewer_id", columnList = "reviewer_id"),
    @Index(name = "idx_reviews_booking_id", columnList = "booking_id"),
    @Index(name = "idx_reviews_status", columnList = "status"),
    @Index(name = "idx_reviews_accommodation_status_created", columnList = "accommodation_id, status, created_at")
})
public class ReviewEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Column(name = "accommodation_id", nullable = false)
    private UUID accommodationId;

    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating;

    @Column(name = "accuracy_rating")
    private Integer accuracyRating;

    @Column(name = "communication_rating")
    private Integer communicationRating;

    @Column(name = "location_rating")
    private Integer locationRating;

    @Column(name = "value_rating")
    private Integer valueRating;

    @Column(name = "title", length = 150)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "pros", columnDefinition = "TEXT")
    private String pros;

    @Column(name = "cons", columnDefinition = "TEXT")
    private String cons;

    @Column(name = "travel_type", length = 50)
    private String travelType;

    @Column(name = "stayed_date")
    private LocalDate stayedDate;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApprovalStatus status;

    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;

    @Column(name = "response_from_host", columnDefinition = "TEXT")
    private String responseFromHost;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Default constructor for JPA
    public ReviewEntity() {
    }

    // Constructor for creating new entity
    public ReviewEntity(UUID id, UUID reviewerId, UUID accommodationId, Integer overallRating,
                       String content, boolean isVerified) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.accommodationId = accommodationId;
        this.overallRating = overallRating;
        this.content = content;
        this.isVerified = isVerified;
        this.status = ApprovalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getCleanlinessRating() {
        return cleanlinessRating;
    }

    public void setCleanlinessRating(Integer cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }

    public Integer getAccuracyRating() {
        return accuracyRating;
    }

    public void setAccuracyRating(Integer accuracyRating) {
        this.accuracyRating = accuracyRating;
    }

    public Integer getCommunicationRating() {
        return communicationRating;
    }

    public void setCommunicationRating(Integer communicationRating) {
        this.communicationRating = communicationRating;
    }

    public Integer getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(Integer locationRating) {
        this.locationRating = locationRating;
    }

    public Integer getValueRating() {
        return valueRating;
    }

    public void setValueRating(Integer valueRating) {
        this.valueRating = valueRating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPros() {
        return pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getCons() {
        return cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public LocalDate getStayedDate() {
        return stayedDate;
    }

    public void setStayedDate(LocalDate stayedDate) {
        this.stayedDate = stayedDate;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public String getResponseFromHost() {
        return responseFromHost;
    }

    public void setResponseFromHost(String responseFromHost) {
        this.responseFromHost = responseFromHost;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
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

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
