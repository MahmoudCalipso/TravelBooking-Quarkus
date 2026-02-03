package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.review.CreateReviewRequest;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.review.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * Mapper for Review domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface ReviewMapper {

    // Entity to Response DTO
    ReviewResponse toReviewResponse(Review review);

    java.util.List<ReviewResponse> toReviewResponseList(java.util.List<Review> reviews);

    // Request DTO to Entity
    default Review toReviewFromRequest(CreateReviewRequest request, UUID reviewerId, UUID accommodationId,
            UUID bookingId) {
        Review review = new Review(
                reviewerId,
                accommodationId,
                bookingId,
                request.getOverallRating(),
                request.getCleanlinessRating(),
                request.getAccuracyRating(),
                request.getCommunicationRating(),
                request.getLocationRating(),
                request.getValueRating(),
                request.getTitle(),
                request.getContent(),
                request.getPros(),
                request.getCons(),
                Review.TravelType.valueOf(request.getTravelType().toString()),
                request.getStayedDate(),
                true // isVerified
        );

        // Ratings are set in constructor

        // Fields set in constructor

        return review;
    }

    @Named("reviewerId")
    default UUID mapReviewerId(Review review) {
        return review != null ? review.getReviewerId() : null;
    }

    @Named("reviewerName")
    default String mapReviewerName(Review review) {
        return null; // Navigation not supported in entity
    }

    @Named("reviewerPhotoUrl")
    default String mapReviewerPhotoUrl(Review review) {
        return null; // Navigation not supported in entity
    }

    @Named("accommodationId")
    default UUID mapAccommodationId(Review review) {
        return review != null ? review.getAccommodationId() : null;
    }

    @Named("accommodationTitle")
    default String mapAccommodationTitle(Review review) {
        return null; // Navigation not supported in entity
    }

    @Named("status")
    default ApprovalStatus mapStatus(Review review) {
        return review != null ? review.getStatus() : null;
    }
}
