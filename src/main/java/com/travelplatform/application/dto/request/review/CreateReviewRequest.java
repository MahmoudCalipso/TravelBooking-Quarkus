package com.travelplatform.application.dto.request.review;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating review request.
 */
public class CreateReviewRequest {

    @NotNull(message = "Accommodation ID is required")
    private UUID accommodationId;

    @NotNull(message = "Booking ID is required")
    private UUID bookingId;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer overallRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer cleanlinessRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer accuracyRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer communicationRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer locationRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer valueRating;

    @Size(max = 150, message = "Title must be less than 150 characters")
    private String title;

    @NotBlank(message = "Review content is required")
    @Size(min = 50, max = 2000, message = "Review content must be between 50 and 2000 characters")
    private String content;

    @Size(max = 1000, message = "Pros must be less than 1000 characters")
    private String pros;

    @Size(max = 1000, message = "Cons must be less than 1000 characters")
    private String cons;

    @Pattern(regexp = "SOLO|COUPLE|FAMILY|FRIENDS|BUSINESS", message = "Invalid travel type")
    private String travelType;

    @NotNull(message = "Stayed date is required")
    private LocalDate stayedDate;

    // Getters and Setters

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
}
