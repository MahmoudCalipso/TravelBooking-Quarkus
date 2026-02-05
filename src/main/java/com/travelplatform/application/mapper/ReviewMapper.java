package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.review.CreateReviewRequest;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.domain.model.review.Review;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Mapper for Review domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface ReviewMapper {

    default ReviewResponse toReviewResponse(Review review) {
        if (review == null) {
            return null;
        }
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setReviewerId(review.getReviewerId());
        response.setReviewerName(null);
        response.setReviewerPhotoUrl(null);
        response.setAccommodationId(review.getAccommodationId());
        response.setAccommodationTitle(null);
        response.setBookingId(review.getBookingId());
        response.setOverallRating(review.getOverallRating());
        response.setCleanlinessRating(review.getCleanlinessRating());
        response.setAccuracyRating(review.getAccuracyRating());
        response.setCommunicationRating(review.getCommunicationRating());
        response.setLocationRating(review.getLocationRating());
        response.setValueRating(review.getValueRating());
        response.setTitle(review.getTitle());
        response.setContent(review.getContent());
        response.setPros(review.getPros());
        response.setCons(review.getCons());
        response.setTravelType(review.getTravelType() != null ? review.getTravelType().name() : null);
        response.setStayedDate(review.getStayedDate());
        response.setIsVerified(review.isVerified());
        response.setStatus(review.getStatus());
        response.setHelpfulCount(review.getHelpfulCount());
        response.setResponseFromHost(review.getResponseFromHost());
        response.setRespondedAt(review.getRespondedAt());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        response.setApprovedAt(review.getApprovedAt());
        response.setPhotoUrls(Collections.emptyList());
        response.setIsHelpfulToCurrentUser(Boolean.FALSE);
        return response;
    }

    default List<ReviewResponse> toReviewResponseList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }
        List<ReviewResponse> responses = new ArrayList<>(reviews.size());
        for (Review review : reviews) {
            responses.add(toReviewResponse(review));
        }
        return responses;
    }

    // Request DTO to Entity
    default Review toReviewFromRequest(CreateReviewRequest request, UUID reviewerId, UUID accommodationId,
            UUID bookingId) {
        return new Review(
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
                request.getTravelType() != null ? Review.TravelType.valueOf(request.getTravelType().toUpperCase()) : null,
                request.getStayedDate(),
                true // isVerified
        );
    }
}
