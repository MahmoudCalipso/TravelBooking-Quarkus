package com.travelplatform.domain.model.review;

import com.travelplatform.domain.enums.ApprovalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a review for an accommodation.
 * This is the aggregate root for the review aggregate.
 */
public class Review {
    private final UUID id;
    private final UUID reviewerId;
    private final UUID accommodationId;
    private final UUID bookingId;
    private final int overallRating;
    private Integer cleanlinessRating;
    private Integer accuracyRating;
    private Integer communicationRating;
    private Integer locationRating;
    private Integer valueRating;
    private String title;
    private String content;
    private String pros;
    private String cons;
    private TravelType travelType;
    private LocalDate stayedDate;
    private boolean isVerified;
    private ApprovalStatus status;
    private int helpfulCount;
    private String responseFromHost;
    private LocalDateTime respondedAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;

    /**
     * Travel type enumeration.
     */
    public enum TravelType {
        SOLO,
        COUPLE,
        FAMILY,
        FRIENDS,
        BUSINESS,
        OTHER
    }

    /**
     * Simplified constructor for service layer.
     * Required fields: reviewerId, accommodationId, overallRating, content
     * Optional fields will be set via setters
     */
    public Review(UUID id, UUID reviewerId, UUID accommodationId, UUID bookingId, int overallRating, String content) {
        if (reviewerId == null) {
            throw new IllegalArgumentException("Reviewer ID cannot be null");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (overallRating < 1 || overallRating > 5) {
            throw new IllegalArgumentException("Overall rating must be between 1 and 5");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }

        this.id = id;
        this.reviewerId = reviewerId;
        this.accommodationId = accommodationId;
        this.bookingId = bookingId;
        this.overallRating = overallRating;
        this.cleanlinessRating = null;
        this.accuracyRating = null;
        this.communicationRating = null;
        this.locationRating = null;
        this.valueRating = null;
        this.title = null;
        this.content = content;
        this.pros = null;
        this.cons = null;
        this.travelType = null;
        this.stayedDate = null;
        this.isVerified = false;
        this.status = ApprovalStatus.PENDING;
        this.helpfulCount = 0;
        this.responseFromHost = null;
        this.respondedAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvedAt = null;
    }

    /**
     * Creates a new Review.
     *
     * @param reviewerId          user ID who wrote the review
     * @param accommodationId     accommodation ID being reviewed
     * @param bookingId           booking ID (for verified reviews)
     * @param overallRating       overall rating (1-5)
     * @param cleanlinessRating   cleanliness rating (1-5)
     * @param accuracyRating      accuracy rating (1-5)
     * @param communicationRating communication rating (1-5)
     * @param locationRating      location rating (1-5)
     * @param valueRating         value rating (1-5)
     * @param title               review headline
     * @param content             review text
     * @param pros                what was good
     * @param cons                what could improve
     * @param travelType          type of travel
     * @param stayedDate          date of stay
     * @param isVerified          whether from verified booking
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Review(UUID reviewerId, UUID accommodationId, UUID bookingId, int overallRating,
            Integer cleanlinessRating, Integer accuracyRating, Integer communicationRating,
            Integer locationRating, Integer valueRating, String title, String content,
            String pros, String cons, TravelType travelType, LocalDate stayedDate, boolean isVerified) {
        if (reviewerId == null) {
            throw new IllegalArgumentException("Reviewer ID cannot be null");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (overallRating < 1 || overallRating > 5) {
            throw new IllegalArgumentException("Overall rating must be between 1 and 5");
        }
        if (cleanlinessRating != null && (cleanlinessRating < 1 || cleanlinessRating > 5)) {
            throw new IllegalArgumentException("Cleanliness rating must be between 1 and 5");
        }
        if (accuracyRating != null && (accuracyRating < 1 || accuracyRating > 5)) {
            throw new IllegalArgumentException("Accuracy rating must be between 1 and 5");
        }
        if (communicationRating != null && (communicationRating < 1 || communicationRating > 5)) {
            throw new IllegalArgumentException("Communication rating must be between 1 and 5");
        }
        if (locationRating != null && (locationRating < 1 || locationRating > 5)) {
            throw new IllegalArgumentException("Location rating must be between 1 and 5");
        }
        if (valueRating != null && (valueRating < 1 || valueRating > 5)) {
            throw new IllegalArgumentException("Value rating must be between 1 and 5");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (travelType == null) {
            throw new IllegalArgumentException("Travel type cannot be null");
        }
        if (stayedDate == null) {
            throw new IllegalArgumentException("Stayed date cannot be null");
        }

        this.id = UUID.randomUUID();
        this.reviewerId = reviewerId;
        this.accommodationId = accommodationId;
        this.bookingId = bookingId;
        this.overallRating = overallRating;
        this.cleanlinessRating = cleanlinessRating;
        this.accuracyRating = accuracyRating;
        this.communicationRating = communicationRating;
        this.locationRating = locationRating;
        this.valueRating = valueRating;
        this.title = title;
        this.content = content;
        this.pros = pros;
        this.cons = cons;
        this.travelType = travelType;
        this.stayedDate = stayedDate;
        this.isVerified = isVerified;
        this.status = ApprovalStatus.PENDING;
        this.helpfulCount = 0;
        this.responseFromHost = null;
        this.respondedAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvedAt = null;
    }

    /**
     * Reconstructs a Review from persistence.
     */
    public Review(UUID id, UUID reviewerId, UUID accommodationId, UUID bookingId, int overallRating,
            Integer cleanlinessRating, Integer accuracyRating, Integer communicationRating,
            Integer locationRating, Integer valueRating, String title, String content,
            String pros, String cons, TravelType travelType, LocalDate stayedDate, boolean isVerified,
            ApprovalStatus status, int helpfulCount, String responseFromHost, LocalDateTime respondedAt,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime approvedAt) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.accommodationId = accommodationId;
        this.bookingId = bookingId;
        this.overallRating = overallRating;
        this.cleanlinessRating = cleanlinessRating;
        this.accuracyRating = accuracyRating;
        this.communicationRating = communicationRating;
        this.locationRating = locationRating;
        this.valueRating = valueRating;
        this.title = title;
        this.content = content;
        this.pros = pros;
        this.cons = cons;
        this.travelType = travelType;
        this.stayedDate = stayedDate;
        this.isVerified = isVerified;
        this.status = status;
        this.helpfulCount = helpfulCount;
        this.responseFromHost = responseFromHost;
        this.respondedAt = respondedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public Integer getCleanlinessRating() {
        return cleanlinessRating;
    }

    public Integer getAccuracyRating() {
        return accuracyRating;
    }

    public Integer getCommunicationRating() {
        return communicationRating;
    }

    public Integer getLocationRating() {
        return locationRating;
    }

    public Integer getValueRating() {
        return valueRating;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPros() {
        return pros;
    }

    public String getCons() {
        return cons;
    }

    public TravelType getTravelType() {
        return travelType;
    }

    public LocalDate getStayedDate() {
        return stayedDate;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public int getHelpfulCount() {
        return helpfulCount;
    }

    public String getResponseFromHost() {
        return responseFromHost;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
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

    /**
     * Updates the review content.
     *
     * @param title   new title
     * @param content new content
     * @param pros    new pros
     * @param cons    new cons
     */
    public void update(String title, String content, String pros, String cons) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        this.title = title;
        this.content = content;
        this.pros = pros;
        this.cons = cons;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approves the review.
     */
    public void approve() {
        this.status = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rejects the review.
     */
    public void reject() {
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Flags the review for review.
     */
    public void flag() {
        this.status = ApprovalStatus.FLAGGED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the helpful count.
     */
    public void incrementHelpfulCount() {
        this.helpfulCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrements the helpful count.
     */
    public void decrementHelpfulCount() {
        if (this.helpfulCount > 0) {
            this.helpfulCount--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Adds a response from the host.
     *
     * @param response host's response
     */
    public void addHostResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Response cannot be null or empty");
        }
        this.responseFromHost = response;
        this.respondedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the host's response.
     *
     * @param response new host response
     */
    public void updateHostResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Response cannot be null or empty");
        }
        this.responseFromHost = response;
        this.respondedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the review is approved.
     *
     * @return true if status is APPROVED
     */
    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    /**
     * Checks if the review is pending.
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    /**
     * Checks if the review is rejected.
     *
     * @return true if status is REJECTED
     */
    public boolean isRejected() {
        return this.status == ApprovalStatus.REJECTED;
    }

    /**
     * Checks if the review is flagged.
     *
     * @return true if status is FLAGGED
     */
    public boolean isFlagged() {
        return this.status == ApprovalStatus.FLAGGED;
    }

    /**
     * Checks if the review has a host response.
     *
     * @return true if host has responded
     */
    public boolean hasHostResponse() {
        return this.responseFromHost != null && !this.responseFromHost.trim().isEmpty();
    }

    /**
     * Calculates the average rating.
     *
     * @return average of all provided ratings
     */
    public double getAverageRating() {
        int count = 1; // overall rating is always present
        double sum = overallRating;

        if (cleanlinessRating != null) {
            sum += cleanlinessRating;
            count++;
        }
        if (accuracyRating != null) {
            sum += accuracyRating;
            count++;
        }
        if (communicationRating != null) {
            sum += communicationRating;
            count++;
        }
        if (locationRating != null) {
            sum += locationRating;
            count++;
        }
        if (valueRating != null) {
            sum += valueRating;
            count++;
        }

        return sum / count;
    }

    /**
     * Checks if the review is recent (within 30 days).
     *
     * @return true if review was created within 30 days
     */
    public boolean isRecent() {
        return createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    // Setter methods for service layer (for use with simplified constructor)
    public void setCleanlinessRating(Integer cleanlinessRating) {
        if (cleanlinessRating != null && (cleanlinessRating < 1 || cleanlinessRating > 5)) {
            throw new IllegalArgumentException("Cleanliness rating must be between 1 and 5");
        }
        this.cleanlinessRating = cleanlinessRating;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAccuracyRating(Integer accuracyRating) {
        if (accuracyRating != null && (accuracyRating < 1 || accuracyRating > 5)) {
            throw new IllegalArgumentException("Accuracy rating must be between 1 and 5");
        }
        this.accuracyRating = accuracyRating;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCommunicationRating(Integer communicationRating) {
        if (communicationRating != null && (communicationRating < 1 || communicationRating > 5)) {
            throw new IllegalArgumentException("Communication rating must be between 1 and 5");
        }
        this.communicationRating = communicationRating;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLocationRating(Integer locationRating) {
        if (locationRating != null && (locationRating < 1 || locationRating > 5)) {
            throw new IllegalArgumentException("Location rating must be between 1 and 5");
        }
        this.locationRating = locationRating;
        this.updatedAt = LocalDateTime.now();
    }

    public void setValueRating(Integer valueRating) {
        if (valueRating != null && (valueRating < 1 || valueRating > 5)) {
            throw new IllegalArgumentException("Value rating must be between 1 and 5");
        }
        this.valueRating = valueRating;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPros(String pros) {
        this.pros = pros;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCons(String cons) {
        this.cons = cons;
        this.updatedAt = LocalDateTime.now();
    }

    public void setTravelType(TravelType travelType) {
        this.travelType = travelType;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStayedDate(LocalDate stayedDate) {
        this.stayedDate = stayedDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Review that = (Review) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Review{id=%s, reviewerId=%s, accommodationId=%s, overallRating=%d, status=%s}",
                id, reviewerId, accommodationId, overallRating, status);
    }
}
