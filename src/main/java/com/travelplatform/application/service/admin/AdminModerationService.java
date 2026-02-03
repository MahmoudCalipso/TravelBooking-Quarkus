package com.travelplatform.application.service.admin;

import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.dto.response.user.UserResponse;
import com.travelplatform.application.mapper.ReelMapper;
import com.travelplatform.application.mapper.ReviewMapper;
import com.travelplatform.application.mapper.UserMapper;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.ReportReason;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.reel.ReelReport;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Admin Moderation operations.
 * Orchestrates content moderation workflows for SUPER_ADMIN.
 */
@ApplicationScoped
public class AdminModerationService {

    @Inject
    UserRepository userRepository;

    @Inject
    TravelReelRepository travelReelRepository;

    @Inject
    ReviewRepository reviewRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    ReelMapper reelMapper;

    @Inject
    ReviewMapper reviewMapper;

    /**
     * Verify user is SUPER_ADMIN.
     */
    private void verifySuperAdmin(UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (admin.getRole() != UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Only SUPER_ADMIN can perform this action");
        }

        if (admin.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Admin account is not active");
        }
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Get all users.
     */
    @Transactional
    public List<UserResponse> getAllUsers(int page, int pageSize) {
        List<User> users = userRepository.findAll(page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    /**
     * Get users by role.
     */
    @Transactional
    public List<UserResponse> getUsersByRole(UserRole role, int page, int pageSize) {
        List<User> users = userRepository.findByRolePaginated(role, page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    /**
     * Get users by status.
     */
    @Transactional
    public List<UserResponse> getUsersByStatus(UserStatus status, int page, int pageSize) {
        List<User> users = userRepository.findByStatusPaginated(status, page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    /**
     * Get user details.
     */
    @Transactional
    public UserResponse getUserDetails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toUserResponse(user);
    }

    /**
     * Suspend user account.
     */
    @Transactional
    public void suspendUser(UUID adminId, UUID userId) {
        verifySuperAdmin(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Cannot suspend another SUPER_ADMIN
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Cannot suspend another SUPER_ADMIN");
        }

        // Suspend user
        user.suspend();
        userRepository.save(user);
    }

    /**
     * Activate user account.
     */
    @Transactional
    public void activateUser(UUID adminId, UUID userId) {
        verifySuperAdmin(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Activate user
        user.activate();
        userRepository.save(user);
    }

    /**
     * Delete user account.
     */
    @Transactional
    public void deleteUser(UUID adminId, UUID userId) {
        verifySuperAdmin(adminId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Cannot delete another SUPER_ADMIN
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Cannot delete another SUPER_ADMIN");
        }

        // Delete user
        userRepository.deleteById(userId);
    }

    /**
     * Search users by email or name.
     */
    @Transactional
    public List<UserResponse> searchUsers(String query, int page, int pageSize) {
        List<User> users = userRepository.search(query, page, pageSize);
        return userMapper.toUserResponseList(users);
    }

    // ==================== REEL MODERATION ====================

    /**
     * Get all reel reports.
     */
    @Transactional
    public List<ReelReport> getReelReports(int page, int pageSize) {
        return travelReelRepository.findReports(page, pageSize);
    }

    /**
     * Get reel reports by status.
     */
    @Transactional
    public List<ReelReport> getReelReportsByStatus(ReelReport.ReportStatus status, int page, int pageSize) {
        return travelReelRepository.findReportsByStatus(status, page, pageSize);
    }

    /**
     * Get reel report details.
     */
    @Transactional
    public ReelReport getReelReportDetails(UUID reportId) {
        return travelReelRepository.findReportById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    /**
     * Review reel report.
     */
    @Transactional
    public void reviewReelReport(UUID adminId, UUID reportId, ReportAction action, String adminNotes) {
        verifySuperAdmin(adminId);

        ReelReport report = travelReelRepository.findReportById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        if (report.getStatus() != ReelReport.ReportStatus.PENDING) {
            throw new IllegalArgumentException("Report has already been reviewed");
        }

        // Determine status based on action
        ReelReport.ReportStatus status;
        switch (action) {
            case DISMISS:
                status = ReelReport.ReportStatus.DISMISSED;
                break;
            case FLAG_CONTENT:
            case REMOVE_CONTENT:
            case SUSPEND_USER:
                status = ReelReport.ReportStatus.ACTION_TAKEN;
                break;
            default:
                status = ReelReport.ReportStatus.REVIEWED;
        }

        // Update report
        report.review(adminId, status, adminNotes);
        travelReelRepository.saveReport(report);

        // Take action on reel if needed
        if (action == ReportAction.FLAG_CONTENT) {
            TravelReel reel = travelReelRepository.findById(report.getReelId())
                    .orElseThrow(() -> new IllegalArgumentException("Reel not found"));
            reel.flag();
            travelReelRepository.save(reel);
        } else if (action == ReportAction.REMOVE_CONTENT) {
            travelReelRepository.deleteById(report.getReelId());
        }
    }

    /**
     * Dismiss reel report.
     */
    @Transactional
    public void dismissReelReport(UUID adminId, UUID reportId, String adminNotes) {
        reviewReelReport(adminId, reportId, ReportAction.DISMISS, adminNotes);
    }

    /**
     * Flag reel.
     */
    @Transactional
    public void flagReel(UUID adminId, UUID reelId) {
        verifySuperAdmin(adminId);

        TravelReel reel = travelReelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        reel.flag();
        travelReelRepository.save(reel);
    }

    /**
     * Remove reel.
     */
    @Transactional
    public void removeReel(UUID adminId, UUID reelId) {
        verifySuperAdmin(adminId);

        TravelReel reel = travelReelRepository.findById(reelId)
                .orElseThrow(() -> new IllegalArgumentException("Reel not found"));

        travelReelRepository.deleteById(reelId);
    }

    /**
     * Get flagged reels.
     */
    @Transactional
    public List<ReelResponse> getFlaggedReels(int page, int pageSize) {
        List<TravelReel> reels = travelReelRepository.findByStatusPaginated(ApprovalStatus.FLAGGED, page, pageSize);
        return reelMapper.toReelResponseList(reels);
    }

    // ==================== REVIEW MODERATION ====================

    /**
     * Get flagged reviews.
     */
    @Transactional
    public List<ReviewResponse> getFlaggedReviews(int page, int pageSize) {
        List<Review> reviews = reviewRepository.findByStatusPaginated(ApprovalStatus.FLAGGED, page, pageSize);
        return reviewMapper.toReviewResponseList(reviews);
    }

    /**
     * Flag review.
     */
    @Transactional
    public void flagReview(UUID adminId, UUID reviewId) {
        verifySuperAdmin(adminId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        review.flag();
        reviewRepository.save(review);
    }

    /**
     * Remove review.
     */
    @Transactional
    public void removeReview(UUID adminId, UUID reviewId) {
        verifySuperAdmin(adminId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        reviewRepository.deleteById(reviewId);
    }

    // ==================== MODERATION SUMMARY ====================

    /**
     * Get moderation summary.
     */
    @Transactional
    public ModerationSummary getModerationSummary() {
        return new ModerationSummary(
                userRepository.countByStatus(UserStatus.SUSPENDED),
                travelReelRepository.countByStatus(ApprovalStatus.FLAGGED),
                reviewRepository.countByStatus(ApprovalStatus.FLAGGED),
                travelReelRepository.countReportsByStatus(ReelReport.ReportStatus.PENDING));
    }

    /**
     * Moderation summary DTO.
     */
    public static class ModerationSummary {
        private final long suspendedUsers;
        private final long flaggedReels;
        private final long flaggedReviews;
        private final long pendingReports;

        public ModerationSummary(long suspendedUsers, long flaggedReels,
                long flaggedReviews, long pendingReports) {
            this.suspendedUsers = suspendedUsers;
            this.flaggedReels = flaggedReels;
            this.flaggedReviews = flaggedReviews;
            this.pendingReports = pendingReports;
        }

        public long getSuspendedUsers() {
            return suspendedUsers;
        }

        public long getFlaggedReels() {
            return flaggedReels;
        }

        public long getFlaggedReviews() {
            return flaggedReviews;
        }

        public long getPendingReports() {
            return pendingReports;
        }
    }

    /**
     * Report action enum.
     */
    public enum ReportAction {
        DISMISS,
        FLAG_CONTENT,
        REMOVE_CONTENT,
        SUSPEND_USER
    }
}
