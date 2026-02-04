package com.travelplatform.application.validator;

import com.travelplatform.application.dto.request.review.CreateReviewRequest;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.service.ValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Validator for review-related operations.
 * Provides additional validation beyond bean validation annotations.
 */
@ApplicationScoped
public class ReviewValidator {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final int MIN_TITLE_LENGTH = 1;
    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MIN_CONTENT_LENGTH = 20;
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int MAX_PROS_LENGTH = 500;
    private static final int MAX_CONS_LENGTH = 500;
    private static final int MAX_HOST_RESPONSE_LENGTH = 1000;
    private static final int MAX_DAYS_AFTER_CHECKOUT = 30;

    @Inject
    ValidationService validationService;

    /**
     * Validates review creation request.
     */
    public void validateReviewCreation(CreateReviewRequest request) {
        // Accommodation ID validation
        if (request.getAccommodationId() == null) {
            throw new IllegalArgumentException("Accommodation ID is required");
        }

        // Booking ID validation (for verified reviews)
        if (request.getBookingId() == null) {
            throw new IllegalArgumentException("Booking ID is required for verified reviews");
        }

        // Overall rating validation
        if (request.getOverallRating() != null) {
            if (request.getOverallRating() < MIN_RATING || request.getOverallRating() > MAX_RATING) {
                throw new IllegalArgumentException("Overall rating must be between " + MIN_RATING + " and " + MAX_RATING);
            }
        }

        // Individual ratings validation
        if (request.getCleanlinessRating() != null) {
            if (request.getCleanlinessRating() < MIN_RATING || request.getCleanlinessRating() > MAX_RATING) {
                throw new IllegalArgumentException("Cleanliness rating must be between " + MIN_RATING + " and " + MAX_RATING);
            }
        }

        if (request.getAccuracyRating() != null) {
            if (request.getAccuracyRating() < MIN_RATING || request.getAccuracyRating() > MAX_RATING) {
                throw new IllegalArgumentException("Accuracy rating must be between " + MIN_RATING + " and " + MAX_RATING);
            }
        }

        if (request.getCommunicationRating() != null) {
            if (request.getCommunicationRating() < MIN_RATING || request.getCommunicationRating() > MAX_RATING) {
                throw new IllegalArgumentException("Communication rating must be between " + MIN_RATING + " and " + MAX_RATING);
            }
        }

        if (request.getLocationRating() != null) {
            if (request.getLocationRating() < MIN_RATING || request.getLocationRating() > MAX_RATING) {
                throw new IllegalArgumentException("Location rating must be between " + MIN_RATING + " and " + MAX_RATING);
            }
        }

        if (request.getValueRating() != null) {
            if (request.getValueRating() < MIN_RATING || request.getValueRating() > MAX_RATING) {
                throw new IllegalArgumentException("Value rating must be between " + MIN_RATING + " and " + MAX_RATING);
            }
        }

        // Title validation
        if (request.getTitle() != null) {
            if (request.getTitle().length() < MIN_TITLE_LENGTH) {
                throw new IllegalArgumentException("Title is too short");
            }
            if (request.getTitle().length() > MAX_TITLE_LENGTH) {
                throw new IllegalArgumentException("Title cannot exceed " + MAX_TITLE_LENGTH + " characters");
            }
        }

        // Content validation
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Review content is required");
        }

        if (request.getContent().length() < MIN_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Review content is too short (minimum " + MIN_CONTENT_LENGTH + " characters)");
        }

        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Review content cannot exceed " + MAX_CONTENT_LENGTH + " characters");
        }

        // Pros validation
        if (request.getPros() != null && request.getPros().length() > MAX_PROS_LENGTH) {
            throw new IllegalArgumentException("Pros cannot exceed " + MAX_PROS_LENGTH + " characters");
        }

        // Cons validation
        if (request.getCons() != null && request.getCons().length() > MAX_CONS_LENGTH) {
            throw new IllegalArgumentException("Cons cannot exceed " + MAX_CONS_LENGTH + " characters");
        }

        // Stayed date validation
        if (request.getStayedDate() != null) {
            LocalDate today = LocalDate.now();
            
            if (request.getStayedDate().isAfter(today)) {
                throw new IllegalArgumentException("Stayed date cannot be in the future");
            }

            long daysSinceStay = ChronoUnit.DAYS.between(request.getStayedDate(), today);
            if (daysSinceStay > MAX_DAYS_AFTER_CHECKOUT) {
                throw new IllegalArgumentException("Reviews must be submitted within " + MAX_DAYS_AFTER_CHECKOUT + " days of checkout");
            }
        }

        // Use domain validation service
        Review tempReview = new Review(
                UUID.randomUUID(),
                UUID.randomUUID(),
                request.getAccommodationId(),
                request.getBookingId(),
                request.getOverallRating(),
                request.getContent());
        validationService.validateReview(tempReview);
    }

    /**
     * Validates host response to review.
     */
    public void validateHostResponse(String response) {
        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("Response content is required");
        }

        if (response.length() > MAX_HOST_RESPONSE_LENGTH) {
            throw new IllegalArgumentException("Response cannot exceed " + MAX_HOST_RESPONSE_LENGTH + " characters");
        }
    }

    /**
     * Validates review edit.
     */
    public void validateReviewEdit(String content, String title, String pros, String cons) {
        // Content validation
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Review content is required");
        }

        if (content.length() < MIN_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Review content is too short");
        }

        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Review content cannot exceed " + MAX_CONTENT_LENGTH + " characters");
        }

        // Title validation
        if (title != null && title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title cannot exceed " + MAX_TITLE_LENGTH + " characters");
        }

        // Pros validation
        if (pros != null && pros.length() > MAX_PROS_LENGTH) {
            throw new IllegalArgumentException("Pros cannot exceed " + MAX_PROS_LENGTH + " characters");
        }

        // Cons validation
        if (cons != null && cons.length() > MAX_CONS_LENGTH) {
            throw new IllegalArgumentException("Cons cannot exceed " + MAX_CONS_LENGTH + " characters");
        }
    }
}
