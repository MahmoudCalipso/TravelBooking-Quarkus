package com.travelplatform.application.service.reel;

import com.travelplatform.application.dto.request.reel.CreateReelRequest;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.application.mapper.ReelMapper;
import com.travelplatform.application.validator.ReelValidator;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.EngagementType;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.reel.ReelComment;
import com.travelplatform.domain.model.reel.ReelEngagement;
import com.travelplatform.domain.model.reel.ReelReport;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.domain.valueobject.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Travel Reel operations.
 * Orchestrates reel-related business workflows.
 */
@ApplicationScoped
public class ReelService {

    @Inject
    TravelReelRepository reelRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ReelMapper reelMapper;

    @Inject
    ReelValidator reelValidator;

    /**
     * Create a new reel.
     */
    @Transactional
    public ReelResponse createReel(UUID userId, CreateReelRequest request) {
        // Validate request
        reelValidator.validateReelCreation(request);

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify user can create reels
        if (user.getRole() != UserRole.TRAVELER && user.getRole() != UserRole.SUPPLIER_SUBSCRIBER) {
            throw new IllegalArgumentException("Only travelers and suppliers can create reels");
        }

        // Determine creator type
        String creatorType = user.getRole() == UserRole.SUPPLIER_SUBSCRIBER ? "SUPPLIER_SUBSCRIBER" : "TRAVELER";

        // Create reel
        // Build Location from latitude/longitude
        Location location = new Location(
                request.getLocationLatitude() != null ? request.getLocationLatitude().doubleValue() : 0.0,
                request.getLocationLongitude() != null ? request.getLocationLongitude().doubleValue() : 0.0);
        
        // Convert visibility string to enum
        VisibilityScope visibility = request.getVisibility() != null ? 
                VisibilityScope.valueOf(request.getVisibility()) : VisibilityScope.PUBLIC;
        
        // Determine if promotional
        boolean isPromotional = user.getRole() == UserRole.SUPPLIER_SUBSCRIBER;
        
        // Convert creatorType from String to enum
        TravelReel.CreatorType reelCreatorType = request.getCreatorType() != null ? 
                request.getCreatorType() : 
                (user.getRole() == UserRole.SUPPLIER_SUBSCRIBER ? TravelReel.CreatorType.SUPPLIER_SUBSCRIBER : TravelReel.CreatorType.TRAVELER);
        
        TravelReel reel = new TravelReel(
                userId,
                reelCreatorType,
                request.getVideoUrl(),
                request.getThumbnailUrl(),
                request.getDuration(),
                location,
                request.getLocationName(),
                visibility,
                isPromotional);

        // Set optional fields
        if (request.getTitle() != null) {
            reel.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            reel.setDescription(request.getDescription());
        }
        if (request.getLocationName() != null) {
            reel.setLocationName(request.getLocationName());
        }
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            reel.setTags(request.getTags());
        }
        if (request.getVisibility() != null) {
            reel.setVisibility(visibility);
        }

        // Set promotional flag for suppliers
        if (user.getRole() == UserRole.SUPPLIER_SUBSCRIBER) {
            reel.setPromotional(true);
        }

        // Save reel
        reelRepository.save(reel);

        return reelMapper.toReelResponse(reel);
    }

    /**
     * Get reel by ID.
     */
    @Transactional
    public ReelResponse getReelById(UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        return reelMapper.toReelResponse(reel);
    }

    /**
     * Get reels by creator.
     */
    @Transactional
    public List<ReelResponse> getReelsByCreator(UUID creatorId, int page, int pageSize) {
        List<TravelReel> reels = reelRepository.findByCreatorIdPaginated(creatorId, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get reels by status.
     */
    @Transactional
    public List<ReelResponse> getReelsByStatus(ApprovalStatus status, int page, int pageSize) {
        List<TravelReel> reels = reelRepository.findByStatusPaginated(status, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get reels by visibility.
     */
    @Transactional
    public List<ReelResponse> getReelsByVisibility(VisibilityScope visibility, int page, int pageSize) {
        List<TravelReel> reels = reelRepository.findByVisibilityPaginated(visibility, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get reels by tags.
     */
    @Transactional
    public List<ReelResponse> getReelsByTags(List<String> tags, int page, int pageSize) {
        List<TravelReel> reels = reelRepository.findByTags(tags, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get reels by location.
     */
    @Transactional
    public List<ReelResponse> getReelsByLocation(double latitude, double longitude, double radiusKm, int page,
            int pageSize) {
        Location location = new Location(latitude, longitude);
        List<TravelReel> reels = reelRepository.findByLocation(location, radiusKm, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get trending reels.
     */
    @Transactional
    public List<ReelResponse> getTrendingReels(int page, int pageSize) {
        List<TravelReel> reels = reelRepository.findTrending(page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get reel feed (personalized).
     */
    @Transactional
    public List<ReelResponse> getReelFeed(UUID userId, int page, int pageSize) {
        List<TravelReel> reels = reelRepository.findFeed(userId, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get bookmarked reels for a user.
     */
    @Transactional
    public List<ReelResponse> getBookmarkedReels(UUID userId, int page, int pageSize) {
        List<ReelEngagement> engagements = reelRepository.findEngagementsByUserId(userId);
        List<UUID> reelIds = engagements.stream()
                .filter(ReelEngagement::isBookmark)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(ReelEngagement::getReelId)
                .toList();

        List<UUID> pagedIds = paginate(reelIds, page, pageSize);
        List<TravelReel> reels = pagedIds.stream()
                .map(id -> reelRepository.findById(id).orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();

        return reelMapper.toReelResponseList(reels);
    }

    /**
     * Get comments for a reel.
     */
    @Transactional
    public List<ReelComment> getComments(UUID reelId, int page, int pageSize) {
        return reelRepository.findCommentsByReelIdPaginated(reelId, page, pageSize);
    }

    /**
     * Get reports submitted by a user.
     */
    @Transactional
    public List<ReelReport> getUserReports(UUID userId, int page, int pageSize) {
        return reelRepository.findReportsByReporter(userId, page, pageSize);
    }

    /**
     * Update reel.
     */
    @Transactional
    public ReelResponse updateReel(UUID userId, UUID reelId, String title, String description,
            VisibilityScope visibility) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Verify ownership
        if (!reel.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own reels");
        }

        // Update fields
        if (title != null) {
            reel.setTitle(title);
        }
        if (description != null) {
            reel.setDescription(description);
        }
        if (visibility != null) {
            reel.setVisibility(visibility);
        }

        // Save updated reel
        reelRepository.save(reel);

        return reelMapper.toReelResponse(reel);
    }

    /**
     * Delete reel.
     */
    @Transactional
    public void deleteReel(UUID userId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Verify ownership
        if (!reel.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own reels");
        }

        // Delete reel
        reelRepository.deleteById(reelId);
    }

    /**
     * Track view.
     */
    @Transactional
    public void trackView(UUID userId, UUID reelId, int watchDuration) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Check if user already viewed this reel
        if (reelRepository.existsEngagement(userId, reelId, EngagementType.VIEW)) {
            // Update existing engagement with watch duration
            ReelEngagement engagement = reelRepository.findEngagement(userId, reelId, EngagementType.VIEW);
            if (engagement != null) {
                engagement.updateWatchDuration(watchDuration);
                reelRepository.saveEngagement(engagement);
            }
        } else {
            // Create new view engagement
            ReelEngagement engagement = new ReelEngagement(
                    reelId,
                    userId,
                    EngagementType.VIEW,
                    watchDuration);
            reelRepository.saveEngagement(engagement);
        }

        // Increment reel view count
        reel.incrementViewCount();
        reelRepository.save(reel);
    }

    /**
     * Like reel.
     */
    @Transactional
    public void likeReel(UUID userId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Check if user already liked this reel
        if (reelRepository.existsEngagement(userId, reelId, EngagementType.LIKE)) {
            throw new IllegalArgumentException("You have already liked this reel");
        }

        // Create like engagement
        ReelEngagement engagement = new ReelEngagement(
                reelId,
                userId,
                EngagementType.LIKE,
                null);
        reelRepository.saveEngagement(engagement);

        // Increment reel like count
        reel.incrementLikeCount();
        reelRepository.save(reel);
    }

    /**
     * Unlike reel.
     */
    @Transactional
    public void unlikeReel(UUID userId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Find and remove like engagement
        ReelEngagement engagement = reelRepository.findEngagement(userId, reelId, EngagementType.LIKE);
        if (engagement != null) {
            reelRepository.deleteEngagement(engagement.getId());

            // Decrement reel like count
            reel.decrementLikeCount();
            reelRepository.save(reel);
        }
    }

    /**
     * Share reel.
     */
    @Transactional
    public void shareReel(UUID userId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Create share engagement
        ReelEngagement engagement = new ReelEngagement(
                reelId,
                userId,
                EngagementType.SHARE,
                null);
        reelRepository.saveEngagement(engagement);

        // Increment reel share count
        reel.incrementShareCount();
        reelRepository.save(reel);
    }

    /**
     * Bookmark reel.
     */
    @Transactional
    public void bookmarkReel(UUID userId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Check if user already bookmarked this reel
        if (reelRepository.existsEngagement(userId, reelId, EngagementType.BOOKMARK)) {
            throw new IllegalArgumentException("You have already bookmarked this reel");
        }

        // Create bookmark engagement
        ReelEngagement engagement = new ReelEngagement(
                reelId,
                userId,
                EngagementType.BOOKMARK,
                null);
        reelRepository.saveEngagement(engagement);

        // Increment reel bookmark count
        reel.incrementBookmarkCount();
        reelRepository.save(reel);
    }

    /**
     * Remove bookmark.
     */
    @Transactional
    public void removeBookmark(UUID userId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Find and remove bookmark engagement
        ReelEngagement engagement = reelRepository.findEngagement(userId, reelId, EngagementType.BOOKMARK);
        if (engagement != null) {
            reelRepository.deleteEngagement(engagement.getId());

            // Decrement reel bookmark count
            reel.decrementBookmarkCount();
            reelRepository.save(reel);
        }
    }

    /**
     * Add comment to reel.
     */
    @Transactional
    public void addComment(UUID userId, UUID reelId, String content, UUID parentCommentId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Validate comment
        reelValidator.validateComment(content);

        // Create comment
        ReelComment comment = new ReelComment(reelId, userId, parentCommentId, content);

        // Set parent comment if it's a reply
        // Save comment
        reelRepository.saveComment(comment);

        // Increment reel comment count
        reel.incrementCommentCount();
        reelRepository.save(reel);
    }

    /**
     * Delete comment.
     */
    @Transactional
    public void deleteComment(UUID userId, UUID commentId) {
        ReelComment comment = reelRepository.findCommentById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Verify ownership
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        // Delete comment
        reelRepository.deleteComment(commentId);

        // Decrement reel comment count
        TravelReel reel = reelRepository.findById(comment.getReelId())
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));
        reel.decrementCommentCount();
        reelRepository.save(reel);
    }

    /**
     * Report reel.
     */
    @Transactional
    public void reportReel(UUID userId, UUID reelId, String reason, String description) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Validate report
        reelValidator.validateReport(reason, description);

        // Create report
        com.travelplatform.domain.enums.ReportReason reportReason =
                com.travelplatform.domain.enums.ReportReason.valueOf(reason);
        ReelReport report = new ReelReport(reelId, userId, reportReason, description);

        // Save report
        reelRepository.saveReport(report);

        // Flag reel if multiple reports
        long reportCount = reelRepository.countReportsByReel(reelId);
        if (reportCount >= 5) {
            reel.flag();
            reelRepository.save(reel);
        }
    }

    /**
     * Approve reel (admin only).
     */
    @Transactional
    public ReelResponse approveReel(UUID adminId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Approve reel
        reel.approve(adminId);
        reelRepository.save(reel);

        return reelMapper.toReelResponse(reel);
    }

    /**
     * Reject reel (admin only).
     */
    @Transactional
    public ReelResponse rejectReel(UUID adminId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Reject reel
        reel.reject(adminId);
        reelRepository.save(reel);

        return reelMapper.toReelResponse(reel);
    }

    /**
     * Flag reel (admin only).
     */
    @Transactional
    public ReelResponse flagReel(UUID adminId, UUID reelId) {
        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        // Flag reel
        reel.flag();
        reelRepository.save(reel);

        return reelMapper.toReelResponse(reel);
    }

    private <T> List<T> paginate(List<T> items, int page, int pageSize) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        int safePage = Math.max(page, 0);
        int safePageSize = Math.max(pageSize, 1);
        int fromIndex = safePage * safePageSize;
        if (fromIndex >= items.size()) {
            return List.of();
        }
        int toIndex = Math.min(items.size(), fromIndex + safePageSize);
        return items.subList(fromIndex, toIndex);
    }
}
