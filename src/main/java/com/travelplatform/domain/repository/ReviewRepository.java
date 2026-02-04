package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.model.review.ReviewHelpful;
import com.travelplatform.domain.enums.ApprovalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Review aggregate.
 * Defines the contract for review data access operations.
 */
public interface ReviewRepository {

    /**
     * Saves a new review.
     *
     * @param review review to save
     * @return saved review
     */
    Review save(Review review);

    /**
     * Saves a helpful vote.
     *
     * @param helpful helpful vote to save
     * @return saved helpful vote
     */
    ReviewHelpful saveHelpful(ReviewHelpful helpful);

    /**
     * Updates an existing review.
     *
     * @param review review to update
     * @return updated review
     */
    Review update(Review review);

    /**
     * Deletes a review by ID.
     *
     * @param id review ID
     */
    void deleteById(UUID id);

    /**
     * Finds a review by ID.
     *
     * @param id review ID
     * @return optional review
     */
    Optional<Review> findById(UUID id);

    /**
     * Finds all reviews.
     *
     * @return list of all reviews
     */
    List<Review> findAll();

    /**
     * Finds reviews by reviewer ID.
     *
     * @param reviewerId reviewer user ID
     * @return list of reviews by reviewer
     */
    List<Review> findByReviewerId(UUID reviewerId);

    /**
     * Finds reviews by accommodation ID.
     *
     * @param accommodationId accommodation ID
     * @return list of reviews for the accommodation
     */
    List<Review> findByAccommodationId(UUID accommodationId);

    /**
     * Finds reviews by booking ID.
     *
     * @param bookingId booking ID
     * @return optional review
     */
    Optional<Review> findByBookingId(UUID bookingId);

    /**
     * Finds reviews by status.
     *
     * @param status approval status
     * @return list of reviews with the status
     */
    List<Review> findByStatus(ApprovalStatus status);

    /**
     * Finds reviews by accommodation ID with pagination.
     *
     * @param accommodationId accommodation ID
     * @param page            page number (0-indexed)
     * @param pageSize        page size
     * @return list of reviews
     */
    List<Review> findByAccommodationIdPaginated(UUID accommodationId, int page, int pageSize);

    /**
     * Finds reviews by reviewer ID with pagination.
     *
     * @param reviewerId reviewer user ID
     * @param page       page number (0-indexed)
     * @param pageSize   page size
     * @return list of reviews
     */
    List<Review> findByReviewerIdPaginated(UUID reviewerId, int page, int pageSize);

    /**
     * Finds reviews by status with pagination.
     *
     * @param status   approval status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of reviews
     */
    List<Review> findByStatusPaginated(ApprovalStatus status, int page, int pageSize);

    /**
     * Finds verified reviews.
     *
     * @return list of verified reviews
     */
    List<Review> findVerified();

    /**
     * Finds unverified reviews.
     *
     * @return list of unverified reviews
     */
    List<Review> findUnverified();

    /**
     * Finds reviews by overall rating.
     *
     * @param rating rating value (1-5)
     * @return list of reviews with the rating
     */
    List<Review> findByOverallRating(int rating);

    /**
     * Finds reviews with minimum rating.
     *
     * @param minRating minimum rating
     * @return list of reviews with rating >= minRating
     */
    List<Review> findByMinOverallRating(int minRating);

    /**
     * Finds reviews by travel type.
     *
     * @param travelType travel type (SOLO, COUPLE, FAMILY, FRIENDS, BUSINESS)
     * @return list of reviews with the travel type
     */
    List<Review> findByTravelType(String travelType);

    /**
     * Finds reviews by stayed date.
     *
     * @param stayedDate date of stay
     * @return list of reviews
     */
    List<Review> findByStayedDate(LocalDate stayedDate);

    /**
     * Finds reviews by stayed date range.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of reviews
     */
    List<Review> findByStayedDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Finds reviews created after a date.
     *
     * @param date creation date threshold
     * @return list of reviews created after the date
     */
    List<Review> findByCreatedAtAfter(LocalDate date);

    /**
     * Finds reviews created before a date.
     *
     * @param date creation date threshold
     * @return list of reviews created before the date
     */
    List<Review> findByCreatedAtBefore(LocalDate date);

    /**
     * Finds reviews created between dates.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of reviews
     */
    List<Review> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Counts reviews by reviewer.
     *
     * @param reviewerId reviewer user ID
     * @return count of reviews by reviewer
     */
    long countByReviewerId(UUID reviewerId);

    /**
     * Counts reviews by accommodation.
     *
     * @param accommodationId accommodation ID
     * @return count of reviews for the accommodation
     */
    long countByAccommodationId(UUID accommodationId);

    /**
     * Counts reviews by status.
     *
     * @param status approval status
     * @return count of reviews with the status
     */
    long countByStatus(ApprovalStatus status);

    /**
     * Counts all reviews.
     *
     * @return total count of reviews
     */
    long countAll();

    /**
     * Standard count method.
     */
    long count();

    /**
     * Calculates average rating for an accommodation.
     *
     * @param accommodationId accommodation ID
     * @return average rating
     */
    double calculateAverageRatingByAccommodation(UUID accommodationId);

    /**
     * Calculates average rating by category for an accommodation.
     *
     * @param accommodationId accommodation ID
     * @param category        rating category (cleanliness, accuracy, communication,
     *                        location, value)
     * @return average rating for the category
     */
    double calculateAverageRatingByCategory(UUID accommodationId, String category);

    /**
     * Finds reviews with host response.
     *
     * @return list of reviews with host response
     */
    List<Review> findWithHostResponse();

    /**
     * Finds reviews without host response.
     *
     * @return list of reviews without host response
     */
    List<Review> findWithoutHostResponse();

    /**
     * Finds reviews by accommodation with host response.
     *
     * @param accommodationId accommodation ID
     * @return list of reviews with host response
     */
    List<Review> findWithHostResponseByAccommodation(UUID accommodationId);

    /**
     * Finds reviews by accommodation without host response.
     *
     * @param accommodationId accommodation ID
     * @return list of reviews without host response
     */
    List<Review> findWithoutHostResponseByAccommodation(UUID accommodationId);

    /**
     * Finds reviews sorted by helpful count.
     *
     * @param limit maximum number of results
     * @return list of most helpful reviews
     */
    List<Review> findMostHelpful(int limit);

    /**
     * Finds reviews sorted by overall rating.
     *
     * @param limit maximum number of results
     * @return list of highest-rated reviews
     */
    List<Review> findHighestRated(int limit);

    /**
     * Finds reviews sorted by overall rating (lowest first).
     *
     * @param limit maximum number of results
     * @return list of lowest-rated reviews
     */
    List<Review> findLowestRated(int limit);

    /**
     * Finds reviews sorted by creation date.
     *
     * @param limit maximum number of results
     * @return list of most recent reviews
     */
    List<Review> findMostRecent(int limit);

    /**
     * Searches reviews by keyword.
     *
     * @param keyword search term
     * @return list of matching reviews
     */
    List<Review> searchByKeyword(String keyword);

    /**
     * Finds reviews by accommodation and status.
     *
     * @param accommodationId accommodation ID
     * @param status          approval status
     * @return list of reviews
     */
    List<Review> findByAccommodationIdAndStatus(UUID accommodationId, ApprovalStatus status);

    /**
     * Finds reviews by reviewer and status.
     *
     * @param reviewerId reviewer user ID
     * @param status     approval status
     * @return list of reviews
     */
    List<Review> findByReviewerIdAndStatus(UUID reviewerId, ApprovalStatus status);

    /**
     * Finds review helpful votes by review ID.
     *
     * @param reviewId review ID
     * @return list of helpful votes
     */
    List<ReviewHelpful> findHelpfulByReviewId(UUID reviewId);

    /**
     * Finds review helpful votes by user ID.
     *
     * @param userId user ID
     * @return list of helpful votes by user
     */
    List<ReviewHelpful> findHelpfulByUserId(UUID userId);

    /**
     * Finds review helpful vote by review ID and user ID.
     *
     * @param reviewId review ID
     * @param userId   user ID
     * @return optional helpful vote
     */
    Optional<ReviewHelpful> findHelpfulByReviewIdAndUserId(UUID reviewId, UUID userId);

    /**
     * Counts helpful votes for a review.
     *
     * @param reviewId review ID
     * @return count of helpful votes
     */
    long countHelpfulByReviewId(UUID reviewId);

    /**
     * Counts not helpful votes for a review.
     *
     * @param reviewId review ID
     * @return count of not helpful votes
     */
    long countNotHelpfulByReviewId(UUID reviewId);

    /**
     * Finds reviews by supplier (via accommodation).
     *
     * @param supplierId supplier user ID
     * @return list of reviews for supplier's accommodations
     */
    List<Review> findBySupplierId(UUID supplierId);

    /**
     * Finds reviews by supplier with pagination.
     *
     * @param supplierId supplier user ID
     * @param page       page number (0-indexed)
     * @param pageSize   page size
     * @return list of reviews
     */
    List<Review> findBySupplierIdPaginated(UUID supplierId, int page, int pageSize);

    /**
     * Finds reviews by supplier and status.
     *
     * @param supplierId supplier user ID
     * @param status     approval status
     * @return list of reviews
     */
    List<Review> findBySupplierIdAndStatus(UUID supplierId, ApprovalStatus status);

    /**
     * Counts reviews by supplier.
     *
     * @param supplierId supplier user ID
     * @return count of reviews for supplier
     */
    long countBySupplierId(UUID supplierId);

    /**
     * Finds reviews with photos.
     *
     * @return list of reviews with photos
     */
    List<Review> findWithPhotos();

    /**
     * Finds reviews by accommodation with photos.
     *
     * @param accommodationId accommodation ID
     * @return list of reviews with photos
     */
    List<Review> findWithPhotosByAccommodation(UUID accommodationId);

    /**
     * Finds reviews with specific rating category value.
     *
     * @param category rating category
     * @param rating   rating value
     * @return list of reviews
     */
    List<Review> findByRatingCategoryAndValue(String category, int rating);

    /**
     * Finds reviews with minimum rating in a category.
     *
     * @param category  rating category
     * @param minRating minimum rating
     * @return list of reviews
     */
    List<Review> findByRatingCategoryMinValue(String category, int minRating);

    /**
     * Finds reviews by accommodation sorted by rating.
     *
     * @param accommodationId accommodation ID
     * @param limit           maximum number of results
     * @return list of reviews
     */
    List<Review> findByAccommodationIdSortedByRating(UUID accommodationId, int limit);

    /**
     * Finds reviews by accommodation sorted by helpful count.
     *
     * @param accommodationId accommodation ID
     * @param limit           maximum number of results
     * @return list of reviews
     */
    List<Review> findByAccommodationIdSortedByHelpful(UUID accommodationId, int limit);

    /**
     * Finds reviews by accommodation sorted by creation date.
     *
     * @param accommodationId accommodation ID
     * @param limit           maximum number of results
     * @return list of reviews
     */
    List<Review> findByAccommodationIdSortedByDate(UUID accommodationId, int limit);
}
