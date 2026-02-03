package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.reel.ReelEngagement;
import com.travelplatform.domain.model.reel.ReelComment;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.valueobject.Location;

import com.travelplatform.application.service.admin.AdminModerationService.ReportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TravelReel aggregate.
 * Defines the contract for reel data access operations.
 */
public interface TravelReelRepository {

    /**
     * Saves a new reel.
     *
     * @param reel reel to save
     * @return saved reel
     */
    TravelReel save(TravelReel reel);

    /**
     * Updates an existing reel.
     *
     * @param reel reel to update
     * @return updated reel
     */
    TravelReel update(TravelReel reel);

    /**
     * Deletes a reel by ID.
     *
     * @param id reel ID
     */
    void deleteById(UUID id);

    /**
     * Finds a reel by ID.
     *
     * @param id reel ID
     * @return optional reel
     */
    Optional<TravelReel> findById(UUID id);

    /**
     * Finds all reels.
     *
     * @return list of all reels
     */
    List<TravelReel> findAll();

    /**
     * Finds reels by creator ID.
     *
     * @param creatorId user ID
     * @return list of reels by creator
     */
    List<TravelReel> findByCreatorId(UUID creatorId);

    /**
     * Finds reels by status.
     *
     * @param status approval status
     * @return list of reels with the status
     */
    List<TravelReel> findByStatus(ApprovalStatus status);

    /**
     * Finds reels by visibility scope.
     *
     * @param visibility visibility scope
     * @return list of reels with the visibility
     */
    List<TravelReel> findByVisibility(VisibilityScope visibility);

    /**
     * Finds reels by status and visibility.
     *
     * @param status     approval status
     * @param visibility visibility scope
     * @return list of reels with the status and visibility
     */
    List<TravelReel> findByStatusAndVisibility(ApprovalStatus status, VisibilityScope visibility);

    /**
     * Finds reels by status with pagination.
     *
     * @param status   approval status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of reels
     */
    List<TravelReel> findByStatusPaginated(ApprovalStatus status, int page, int pageSize);

    /**
     * Finds reels by creator with pagination.
     *
     * @param creatorId user ID
     * @param page      page number (0-indexed)
     * @param pageSize  page size
     * @return list of reels
     */
    List<TravelReel> findByCreatorIdPaginated(UUID creatorId, int page, int pageSize);

    /**
     * Finds reels near a location.
     *
     * @param location center location
     * @param radiusKm search radius in kilometers
     * @return list of reels within radius
     */
    List<TravelReel> findNearby(Location location, double radiusKm);

    /**
     * Finds reels by location name.
     *
     * @param locationName location name
     * @return list of reels at the location
     */
    List<TravelReel> findByLocationName(String locationName);

    /**
     * Finds reels by tags.
     *
     * @param tags list of tags
     * @return list of reels with the tags
     */
    List<TravelReel> findByTags(List<String> tags);

    /**
     * Finds reels by related entity.
     *
     * @param entityType entity type (ACCOMMODATION, EVENT, DESTINATION)
     * @param entityId   entity ID
     * @return list of reels linked to the entity
     */
    List<TravelReel> findByRelatedEntity(String entityType, UUID entityId);

    /**
     * Finds reels by creator type.
     *
     * @param creatorType creator type (TRAVELER, SUPPLIER_SUBSCRIBER)
     * @return list of reels by creator type
     */
    List<TravelReel> findByCreatorType(String creatorType);

    /**
     * Finds promotional reels.
     *
     * @return list of promotional reels
     */
    List<TravelReel> findPromotional();

    /**
     * Finds non-promotional reels.
     *
     * @return list of non-promotional reels
     */
    List<TravelReel> findNonPromotional();

    /**
     * Finds reels created after a date.
     *
     * @param date creation date threshold
     * @return list of reels created after the date
     */
    List<TravelReel> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Finds reels created before a date.
     *
     * @param date creation date threshold
     * @return list of reels created before the date
     */
    List<TravelReel> findByCreatedAtBefore(LocalDateTime date);

    /**
     * Finds reels created between dates.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of reels created between the dates
     */
    List<TravelReel> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Counts reels by creator.
     *
     * @param creatorId user ID
     * @return count of reels by creator
     */
    long countByCreatorId(UUID creatorId);

    /**
     * Counts reels by status.
     *
     * @param status approval status
     * @return count of reels with the status
     */
    long countByStatus(ApprovalStatus status);

    /**
     * Counts all reels.
     *
     * @return total count of reels
     */
    long countAll();

    /**
     * Standard count method.
     */
    long count();

    /**
     * Counts reports by status.
     * 
     * @param status report status
     * @return count of reports
     */
    long countReportsByStatus(com.travelplatform.application.service.admin.AdminModerationService.ReportStatus status);

    /**
     * Finds reports with pagination.
     */
    List<com.travelplatform.domain.model.reel.ReelReport> findReports(int page, int pageSize);

    /**
     * Finds reports by status.
     */
    List<com.travelplatform.domain.model.reel.ReelReport> findReportsByStatus(
            com.travelplatform.application.service.admin.AdminModerationService.ReportStatus status, int page,
            int pageSize);

    /**
     * Finds report by ID.
     */
    Optional<com.travelplatform.domain.model.reel.ReelReport> findReportById(UUID id);

    /**
     * Saves a report.
     */
    void saveReport(com.travelplatform.domain.model.reel.ReelReport report);

    /**
     * Finds trending reels (high engagement).
     *
     * @param limit maximum number of results
     * @return list of trending reels
     */
    List<TravelReel> findTrending(int limit);

    /**
     * Finds reels sorted by view count.
     *
     * @param limit maximum number of results
     * @return list of most-viewed reels
     */
    List<TravelReel> findMostViewed(int limit);

    /**
     * Finds reels sorted by like count.
     *
     * @param limit maximum number of results
     * @return list of most-liked reels
     */
    List<TravelReel> findMostLiked(int limit);

    /**
     * Finds reels sorted by completion rate.
     *
     * @param limit maximum number of results
     * @return list of most-completed reels
     */
    List<TravelReel> findMostCompleted(int limit);

    /**
     * Searches reels by keyword.
     *
     * @param keyword search term
     * @return list of matching reels
     */
    List<TravelReel> searchByKeyword(String keyword);

    /**
     * Finds reel engagements by reel ID.
     *
     * @param reelId reel ID
     * @return list of engagements
     */
    List<ReelEngagement> findEngagementsByReelId(UUID reelId);

    /**
     * Finds reel engagements by user ID.
     *
     * @param userId user ID
     * @return list of engagements by user
     */
    List<ReelEngagement> findEngagementsByUserId(UUID userId);

    /**
     * Finds reel engagement by reel ID and user ID.
     *
     * @param reelId reel ID
     * @param userId user ID
     * @return optional engagement
     */
    Optional<ReelEngagement> findEngagementByReelIdAndUserId(UUID reelId, UUID userId);

    /**
     * Finds reel comments by reel ID.
     *
     * @param reelId reel ID
     * @return list of comments
     */
    List<ReelComment> findCommentsByReelId(UUID reelId);

    /**
     * Finds reel comments by reel ID with pagination.
     *
     * @param reelId   reel ID
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of comments
     */
    List<ReelComment> findCommentsByReelIdPaginated(UUID reelId, int page, int pageSize);

    /**
     * Finds reel comments by user ID.
     *
     * @param userId user ID
     * @return list of comments by user
     */
    List<ReelComment> findCommentsByUserId(UUID userId);

    /**
     * Finds reel comments by parent comment ID (replies).
     *
     * @param parentCommentId parent comment ID
     * @return list of replies
     */
    List<ReelComment> findRepliesByParentCommentId(UUID parentCommentId);

    /**
     * Counts comments by reel ID.
     *
     * @param reelId reel ID
     * @return count of comments
     */
    long countCommentsByReelId(UUID reelId);

    /**
     * Counts engagements by reel ID.
     *
     * @param reelId reel ID
     * @return count of engagements
     */
    long countEngagementsByReelId(UUID reelId);

    /**
     * Counts likes by reel ID.
     *
     * @param reelId reel ID
     * @return count of likes
     */
    long countLikesByReelId(UUID reelId);

    /**
     * Counts views by reel ID.
     *
     * @param reelId reel ID
     * @return count of views
     */
    long countViewsByReelId(UUID reelId);

    // Missing methods for Service logic
    List<TravelReel> findByVisibilityPaginated(VisibilityScope visibility, int page, int pageSize);

    List<TravelReel> findByTags(List<String> tags, int page, int pageSize);

    List<TravelReel> findByLocation(Location location, double radiusKm, int page, int pageSize);

    List<TravelReel> findTrending(int page, int pageSize);

    List<TravelReel> findFeed(UUID userId, int page, int pageSize);

    boolean existsEngagement(UUID userId, UUID reelId, com.travelplatform.domain.enums.EngagementType type);

    ReelEngagement findEngagement(UUID userId, UUID reelId, com.travelplatform.domain.enums.EngagementType type);

    void saveEngagement(ReelEngagement engagement);

    void deleteEngagement(UUID engagementId);

    void saveComment(ReelComment comment);

    Optional<ReelComment> findCommentById(UUID commentId);

    void deleteComment(UUID commentId);

    long countReportsByReel(UUID reelId);

    void saveReport(com.travelplatform.domain.model.reel.ReelReport report);
}
