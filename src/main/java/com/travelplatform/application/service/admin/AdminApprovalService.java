package com.travelplatform.application.service.admin;

import com.travelplatform.application.dto.response.accommodation.AccommodationResponse;
import com.travelplatform.application.dto.response.event.EventResponse;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.mapper.AccommodationMapper;
import com.travelplatform.application.mapper.EventMapper;
import com.travelplatform.application.mapper.ReelMapper;
import com.travelplatform.application.mapper.ReviewMapper;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.EventRepository;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Admin Approval operations.
 * Orchestrates content approval workflows for SUPER_ADMIN.
 */
@ApplicationScoped
public class AdminApprovalService {

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    TravelReelRepository travelReelRepository;

    @Inject
    ReviewRepository reviewRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    AccommodationMapper accommodationMapper;

    @Inject
    ReelMapper reelMapper;

    @Inject
    ReviewMapper reviewMapper;

    @Inject
    EventMapper eventMapper;

    /**
     * Verify user is SUPER_ADMIN.
     */
    private void verifySuperAdmin(UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (admin.getRole() != UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Only SUPER_ADMIN can perform this action");
        }

        if (admin.getStatus() != com.travelplatform.domain.enums.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Admin account is not active");
        }
    }

    // ==================== ACCOMMODATION APPROVAL ====================

    /**
     * Get pending accommodations.
     */
    @Transactional
    public List<AccommodationResponse> getPendingAccommodations(int page, int pageSize) {
        List<Accommodation> accommodations = accommodationRepository.findByStatusPaginated(ApprovalStatus.PENDING, page,
                pageSize);
        return accommodationMapper.toAccommodationResponseList(accommodations);
    }

    /**
     * Get pending accommodations count.
     */
    @Transactional
    public long getPendingAccommodationsCount() {
        return accommodationRepository.countByStatus(ApprovalStatus.PENDING);
    }

    /**
     * Approve accommodation.
     */
    @Transactional
    public void approveAccommodation(UUID adminId, UUID accommodationId) {
        verifySuperAdmin(adminId);

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (accommodation.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Accommodation is not pending approval");
        }

        // Approve accommodation
        accommodation.approve(adminId);
        accommodationRepository.save(accommodation);
    }

    /**
     * Reject accommodation.
     */
    @Transactional
    public void rejectAccommodation(UUID adminId, UUID accommodationId) {
        verifySuperAdmin(adminId);

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (accommodation.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Accommodation is not pending approval");
        }

        // Reject accommodation
        accommodation.reject();
        accommodationRepository.save(accommodation);
    }

    // ==================== REEL APPROVAL ====================

    /**
     * Get pending reels.
     */
    @Transactional
    public List<ReelResponse> getPendingReels(int page, int pageSize) {
        List<TravelReel> reels = travelReelRepository.findByStatusPaginated(ApprovalStatus.PENDING, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get pending reels count.
     */
    @Transactional
    public long getPendingReelsCount() {
        return travelReelRepository.countByStatus(ApprovalStatus.PENDING);
    }

    /**
     * Approve reel.
     */
    @Transactional
    public void approveReel(UUID adminId, UUID reelId) {
        verifySuperAdmin(adminId);

        TravelReel reel = travelReelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        if (reel.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Reel is not pending approval");
        }

        // Approve reel
        reel.approve(adminId);
        travelReelRepository.save(reel);
    }

    /**
     * Reject reel.
     */
    @Transactional
    public void rejectReel(UUID adminId, UUID reelId) {
        verifySuperAdmin(adminId);

        TravelReel reel = travelReelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        if (reel.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Reel is not pending approval");
        }

        // Reject reel
        reel.reject();
        travelReelRepository.save(reel);
    }

    // ==================== REVIEW APPROVAL ====================

    /**
     * Get pending reviews.
     */
    @Transactional
    public List<ReviewResponse> getPendingReviews(int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByStatusPaginated(ApprovalStatus.PENDING, page, pageSize);
        return reviewMapper.toReviewResponseList(reviews);
    }

    /**
     * Get pending reviews count.
     */
    @Transactional
    public long getPendingReviewsCount() {
        return reviewRepository.countByStatus(ApprovalStatus.PENDING);
    }

    /**
     * Approve review.
     */
    @Transactional
    public void approveReview(UUID adminId, UUID reviewId) {
        verifySuperAdmin(adminId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (review.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Review is not pending approval");
        }

        // Approve review
        review.approve();
        reviewRepository.save(review);

        // Update accommodation average rating
        updateAccommodationAverageRating(review.getAccommodationId());
    }

    /**
     * Reject review.
     */
    @Transactional
    public void rejectReview(UUID adminId, UUID reviewId) {
        verifySuperAdmin(adminId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (review.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Review is not pending approval");
        }

        // Reject review
        review.reject();
        reviewRepository.save(review);
    }

    /**
     * Update accommodation average rating.
     */
    private void updateAccommodationAverageRating(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Calculate new average rating from approved reviews
        List<Review> approvedReviews = reviewRepository.findByAccommodationIdAndStatus(
                accommodationId, ApprovalStatus.APPROVED);

        if (approvedReviews.isEmpty()) {
            accommodation.updateAverageRating(null);
        } else {
            double sum = approvedReviews.stream()
                    .mapToInt(Review::getOverallRating)
                    .sum();
            double average = sum / approvedReviews.size();
            accommodation.updateAverageRating(average);
        }

        accommodationRepository.save(accommodation);
    }

    // ==================== EVENT APPROVAL ====================

    /**
     * Get pending events.
     */
    @Transactional
    public List<EventResponse> getPendingEvents(int page, int pageSize) {
        List<Event> events = eventRepository.findByStatusPaginated(ApprovalStatus.PENDING, page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Get pending events count.
     */
    @Transactional
    public long getPendingEventsCount() {
        return eventRepository.countByStatus(ApprovalStatus.PENDING);
    }

    /**
     * Approve event.
     */
    @Transactional
    public void approveEvent(UUID adminId, UUID eventId) {
        verifySuperAdmin(adminId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Event is not pending approval");
        }

        // Approve event
        event.approve(adminId);
        eventRepository.save(event);
    }

    /**
     * Reject event.
     */
    @Transactional
    public void rejectEvent(UUID adminId, UUID eventId) {
        verifySuperAdmin(adminId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Event is not pending approval");
        }

        // Reject event
        event.reject();
        eventRepository.save(event);
    }

    // ==================== APPROVAL QUEUE SUMMARY ====================

    /**
     * Get approval queue summary.
     */
    @Transactional
    public ApprovalQueueSummary getApprovalQueueSummary() {
        return new ApprovalQueueSummary(
                getPendingAccommodationsCount(),
                getPendingReelsCount(),
                getPendingReviewsCount(),
                getPendingEventsCount());
    }

    /**
     * Approval queue summary DTO.
     */
    public static class ApprovalQueueSummary {
        private final long pendingAccommodations;
        private final long pendingReels;
        private final long pendingReviews;
        private final long pendingEvents;

        public ApprovalQueueSummary(long pendingAccommodations, long pendingReels,
                long pendingReviews, long pendingEvents) {
            this.pendingAccommodations = pendingAccommodations;
            this.pendingReels = pendingReels;
            this.pendingReviews = pendingReviews;
            this.pendingEvents = pendingEvents;
        }

        public long getPendingAccommodations() {
            return pendingAccommodations;
        }

        public long getPendingReels() {
            return pendingReels;
        }

        public long getPendingReviews() {
            return pendingReviews;
        }

        public long getPendingEvents() {
            return pendingEvents;
        }

        public long getTotalPending() {
            return pendingAccommodations + pendingReels + pendingReviews + pendingEvents;
        }
    }
}
