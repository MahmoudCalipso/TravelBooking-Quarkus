package com.travelplatform.application.service.review;

import com.travelplatform.application.dto.request.review.CreateReviewRequest;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.mapper.ReviewMapper;
import com.travelplatform.application.validator.ReviewValidator;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.model.review.ReviewHelpful;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Review operations.
 * Orchestrates review-related business workflows.
 */
@ApplicationScoped
public class ReviewService {

    @Inject
    ReviewRepository reviewRepository;

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ReviewMapper reviewMapper;

    @Inject
    ReviewValidator reviewValidator;

    /**
     * Create a new review.
     */
    @Transactional
    public ReviewResponse createReview(UUID userId, CreateReviewRequest request) {
        // Validate request
        reviewValidator.validateReviewCreation(request);

        // Get accommodation
        Accommodation accommodation = accommodationRepository.findById(request.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Get booking (for verified reviews)
        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            // Verify booking belongs to user
            if (!booking.getUserId().equals(userId)) {
                throw new IllegalArgumentException("You can only review your own bookings");
            }

            // Verify booking is for this accommodation
            if (!booking.getAccommodationId().equals(request.getAccommodationId())) {
                throw new IllegalArgumentException("Booking is not for this accommodation");
            }

            // Verify booking is completed
            if (booking.getStatus() != BookingStatus.COMPLETED) {
                throw new IllegalArgumentException("You can only review completed bookings");
            }

            // Check if review already exists for this booking
            if (reviewRepository.existsByBookingId(request.getBookingId())) {
                throw new IllegalArgumentException("You have already reviewed this booking");
            }
        }

        // Check if user has already reviewed this accommodation
        if (reviewRepository.existsByReviewerAndAccommodation(userId, request.getAccommodationId())) {
            throw new IllegalArgumentException("You have already reviewed this accommodation");
        }

        // Create review
        Review review = new Review(
            UUID.randomUUID(),
            userId,
            request.getAccommodationId(),
            request.getBookingId(),
            request.getOverallRating(),
            request.getContent()
        );

        // Set optional ratings
        if (request.getCleanlinessRating() != null) {
            review.setCleanlinessRating(request.getCleanlinessRating());
        }
        if (request.getAccuracyRating() != null) {
            review.setAccuracyRating(request.getAccuracyRating());
        }
        if (request.getCommunicationRating() != null) {
            review.setCommunicationRating(request.getCommunicationRating());
        }
        if (request.getLocationRating() != null) {
            review.setLocationRating(request.getLocationRating());
        }
        if (request.getValueRating() != null) {
            review.setValueRating(request.getValueRating());
        }

        // Set optional fields
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getPros() != null) {
            review.setPros(request.getPros());
        }
        if (request.getCons() != null) {
            review.setCons(request.getCons());
        }
        if (request.getTravelType() != null) {
            review.setTravelType(request.getTravelType());
        }
        if (request.getStayedDate() != null) {
            review.setStayedDate(request.getStayedDate());
        }

        // Set verified flag
        review.setVerified(booking != null);

        // Save review
        reviewRepository.save(review);

        // Update accommodation average rating
        updateAccommodationRating(request.getAccommodationId());

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Get review by ID.
     */
    @Transactional
    public ReviewResponse getReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Get reviews by accommodation.
     */
    @Transactional
    public List<ReviewResponse> getReviewsByAccommodation(UUID accommodationId, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByAccommodationId(accommodationId, page, pageSize);
        return reviewMapper.toReviewResponseList(reviews);
    }

    /**
     * Get reviews by reviewer.
     */
    @Transactional
    public List<ReviewResponse> getReviewsByReviewer(UUID reviewerId, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByReviewerId(reviewerId, page, pageSize);
        return reviewMapper.toReviewResponseList(reviews);
    }

    /**
     * Get reviews by rating.
     */
    @Transactional
    public List<ReviewResponse> getReviewsByRating(int rating, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByOverallRating(rating, page, pageSize);
        return reviewMapper.toReviewResponseList(reviews);
    }

    /**
     * Get reviews by status.
     */
    @Transactional
    public List<ReviewResponse> getReviewsByStatus(ApprovalStatus status, int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByStatus(status, page, pageSize);
        return reviewMapper.toReviewResponseList(reviews);
    }

    /**
     * Update review.
     */
    @Transactional
    public ReviewResponse updateReview(UUID userId, UUID reviewId, String content, String title, String pros, String cons) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Verify ownership
        if (!review.getReviewerId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own reviews");
        }

        // Validate update
        reviewValidator.validateReviewEdit(content, title, pros, cons);

        // Update fields
        review.setContent(content);
        if (title != null) {
            review.setTitle(title);
        }
        if (pros != null) {
            review.setPros(pros);
        }
        if (cons != null) {
            review.setCons(cons);
        }

        // Save updated review
        reviewRepository.save(review);

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Delete review.
     */
    @Transactional
    public void deleteReview(UUID userId, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Verify ownership
        if (!review.getReviewerId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        // Delete review
        reviewRepository.deleteById(reviewId);

        // Update accommodation average rating
        updateAccommodationRating(review.getAccommodationId());
    }

    /**
     * Mark review as helpful.
     */
    @Transactional
    public void markReviewHelpful(UUID userId, UUID reviewId, boolean isHelpful) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Check if user already marked this review
        if (reviewRepository.existsHelpfulVote(userId, reviewId)) {
            throw new IllegalArgumentException("You have already marked this review");
        }

        // Create helpful vote
        ReviewHelpful helpful = new ReviewHelpful(
            UUID.randomUUID(),
            reviewId,
            userId,
            isHelpful
        );

        // Save helpful vote
        reviewRepository.saveHelpful(helpful);

        // Update review helpful count
        if (isHelpful) {
            review.incrementHelpfulCount();
            reviewRepository.save(review);
        }
    }

    /**
     * Respond to review (supplier only).
     */
    @Transactional
    public ReviewResponse respondToReview(UUID supplierId, UUID reviewId, String response) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Get accommodation
        Accommodation accommodation = accommodationRepository.findById(review.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Verify supplier owns the accommodation
        if (!accommodation.getSupplierId().equals(supplierId)) {
            throw new IllegalArgumentException("You can only respond to reviews of your own accommodations");
        }

        // Validate response
        reviewValidator.validateHostResponse(response);

        // Set response
        review.setResponseFromHost(response);
        reviewRepository.save(review);

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Approve review (admin only).
     */
    @Transactional
    public ReviewResponse approveReview(UUID adminId, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Approve review
        review.approve(adminId);
        reviewRepository.save(review);

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Reject review (admin only).
     */
    @Transactional
    public ReviewResponse rejectReview(UUID adminId, UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // Reject review
        review.reject(adminId);
        reviewRepository.save(review);

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Update accommodation average rating.
     */
    private void updateAccommodationRating(UUID accommodationId) {
        List<Review> approvedReviews = reviewRepository.findByAccommodationIdAndStatus(
            accommodationId, ApprovalStatus.APPROVED, 0, Integer.MAX_VALUE
        );

        if (approvedReviews.isEmpty()) {
            return;
        }

        double averageRating = approvedReviews.stream()
            .mapToInt(Review::getOverallRating)
            .average()
            .orElse(0.0);

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        accommodation.updateAverageRating(averageRating);
        accommodationRepository.save(accommodation);
    }
}
