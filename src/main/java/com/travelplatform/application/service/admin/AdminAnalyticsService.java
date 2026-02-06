package com.travelplatform.application.service.admin;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application Service for Admin Analytics operations.
 * Provides platform-wide analytics and statistics for SUPER_ADMIN.
 */
@ApplicationScoped
public class AdminAnalyticsService {

    @Inject
    UserRepository userRepository;

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    ReviewRepository reviewRepository;

    @Inject
    TravelReelRepository travelReelRepository;

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

    // ==================== PLATFORM OVERVIEW ====================

    /**
     * Get platform overview statistics.
     */
    @Transactional
    public PlatformOverview getPlatformOverview() {
        return new PlatformOverview(
                userRepository.count(),
                accommodationRepository.count(),
                bookingRepository.count(),
                reviewRepository.count(),
                travelReelRepository.count(),
                userRepository.countByRole(UserRole.SUPER_ADMIN),
                userRepository.countByRole(UserRole.TRAVELER),
                userRepository.countByRole(UserRole.SUPPLIER_SUBSCRIBER),
                userRepository.countByRole(UserRole.ASSOCIATION_MANAGER));
    }

    /**
     * Platform overview DTO.
     */
    public static class PlatformOverview {
        private final long totalUsers;
        private final long totalAccommodations;
        private final long totalBookings;
        private final long totalReviews;
        private final long totalReels;
        private final long superAdminCount;
        private final long travelerCount;
        private final long supplierCount;
        private final long associationManagerCount;

        public PlatformOverview(long totalUsers, long totalAccommodations, long totalBookings,
                long totalReviews, long totalReels, long superAdminCount,
                long travelerCount, long supplierCount, long associationManagerCount) {
            this.totalUsers = totalUsers;
            this.totalAccommodations = totalAccommodations;
            this.totalBookings = totalBookings;
            this.totalReviews = totalReviews;
            this.totalReels = totalReels;
            this.superAdminCount = superAdminCount;
            this.travelerCount = travelerCount;
            this.supplierCount = supplierCount;
            this.associationManagerCount = associationManagerCount;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getTotalAccommodations() {
            return totalAccommodations;
        }

        public long getTotalBookings() {
            return totalBookings;
        }

        public long getTotalReviews() {
            return totalReviews;
        }

        public long getTotalReels() {
            return totalReels;
        }

        public long getSuperAdminCount() {
            return superAdminCount;
        }

        public long getTravelerCount() {
            return travelerCount;
        }

        public long getSupplierCount() {
            return supplierCount;
        }

        public long getAssociationManagerCount() {
            return associationManagerCount;
        }
    }

    // ==================== ACCOMMODATION ANALYTICS ====================

    /**
     * Get accommodation statistics.
     */
    @Transactional
    public AccommodationAnalytics getAccommodationAnalytics() {
        return new AccommodationAnalytics(
                accommodationRepository.count(),
                accommodationRepository.countByStatus(ApprovalStatus.PENDING),
                accommodationRepository.countByStatus(ApprovalStatus.APPROVED),
                accommodationRepository.countByStatus(ApprovalStatus.REJECTED),
                accommodationRepository.countByStatus(ApprovalStatus.FLAGGED),
                accommodationRepository.countByIsPremium(true));
    }

    /**
     * Accommodation analytics DTO.
     */
    public static class AccommodationAnalytics {
        private final long totalAccommodations;
        private final long pendingAccommodations;
        private final long approvedAccommodations;
        private final long rejectedAccommodations;
        private final long flaggedAccommodations;
        private final long premiumAccommodations;

        public AccommodationAnalytics(long totalAccommodations, long pendingAccommodations,
                long approvedAccommodations, long rejectedAccommodations,
                long flaggedAccommodations, long premiumAccommodations) {
            this.totalAccommodations = totalAccommodations;
            this.pendingAccommodations = pendingAccommodations;
            this.approvedAccommodations = approvedAccommodations;
            this.rejectedAccommodations = rejectedAccommodations;
            this.flaggedAccommodations = flaggedAccommodations;
            this.premiumAccommodations = premiumAccommodations;
        }

        public long getTotalAccommodations() {
            return totalAccommodations;
        }

        public long getPendingAccommodations() {
            return pendingAccommodations;
        }

        public long getApprovedAccommodations() {
            return approvedAccommodations;
        }

        public long getRejectedAccommodations() {
            return rejectedAccommodations;
        }

        public long getFlaggedAccommodations() {
            return flaggedAccommodations;
        }

        public long getPremiumAccommodations() {
            return premiumAccommodations;
        }
    }

    // ==================== REEL ANALYTICS ====================

    /**
     * Get reel statistics.
     */
    @Transactional
    public ReelAnalytics getReelAnalytics() {
        return new ReelAnalytics(
                travelReelRepository.count(),
                travelReelRepository.countByStatus(ApprovalStatus.PENDING),
                travelReelRepository.countByStatus(ApprovalStatus.APPROVED),
                travelReelRepository.countByStatus(ApprovalStatus.REJECTED),
                travelReelRepository.countByStatus(ApprovalStatus.FLAGGED),
                travelReelRepository.countReportsByStatus(
                        com.travelplatform.domain.model.reel.ReelReport.ReportStatus.PENDING));
    }

    /**
     * Reel analytics DTO.
     */
    public static class ReelAnalytics {
        private final long totalReels;
        private final long pendingReels;
        private final long approvedReels;
        private final long rejectedReels;
        private final long flaggedReels;
        private final long pendingReports;

        public ReelAnalytics(long totalReels, long pendingReels, long approvedReels,
                long rejectedReels, long flaggedReels, long pendingReports) {
            this.totalReels = totalReels;
            this.pendingReels = pendingReels;
            this.approvedReels = approvedReels;
            this.rejectedReels = rejectedReels;
            this.flaggedReels = flaggedReels;
            this.pendingReports = pendingReports;
        }

        public long getTotalReels() {
            return totalReels;
        }

        public long getPendingReels() {
            return pendingReels;
        }

        public long getApprovedReels() {
            return approvedReels;
        }

        public long getRejectedReels() {
            return rejectedReels;
        }

        public long getFlaggedReels() {
            return flaggedReels;
        }

        public long getPendingReports() {
            return pendingReports;
        }
    }

    // ==================== BOOKING ANALYTICS ====================

    /**
     * Get booking statistics.
     */
    @Transactional
    public BookingAnalytics getBookingAnalytics() {
        return new BookingAnalytics(
                bookingRepository.count(),
                bookingRepository.countByStatus(BookingStatus.PENDING),
                bookingRepository.countByStatus(BookingStatus.CONFIRMED),
                bookingRepository.countByStatus(BookingStatus.CANCELLED),
                bookingRepository.countByStatus(BookingStatus.COMPLETED),
                bookingRepository.countByStatus(BookingStatus.NO_SHOW),
                bookingRepository.countByPaymentStatus(PaymentStatus.PAID),
                bookingRepository.countByPaymentStatus(PaymentStatus.REFUNDED));
    }

    /**
     * Get booking revenue.
     */
    @Transactional
    public RevenueAnalytics getRevenueAnalytics(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenue(startDate, endDate);
        BigDecimal totalRefunds = bookingRepository.calculateTotalRefunds(startDate, endDate);
        BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);

        return new RevenueAnalytics(
                totalRevenue,
                totalRefunds,
                netRevenue,
                bookingRepository.countCompletedBookings(startDate, endDate),
                bookingRepository.countCancelledBookings(startDate, endDate));
    }

    /**
     * Get platform fee analytics (service fees collected from suppliers/associations).
     */
    @Transactional
    public PlatformFeeAnalytics getPlatformFeeAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusSeconds(1);

        BigDecimal supplierFees = bookingRepository.calculateServiceFeesBySupplierRoles(
                List.of(UserRole.SUPPLIER_SUBSCRIBER), startDateTime, endDateTime,
                List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED));

        BigDecimal associationFees = bookingRepository.calculateServiceFeesBySupplierRoles(
                List.of(UserRole.ASSOCIATION_MANAGER), startDateTime, endDateTime,
                List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED));

        BigDecimal totalFees = supplierFees.add(associationFees);

        return new PlatformFeeAnalytics(start, end, supplierFees, associationFees, totalFees);
    }

    /**
     * Booking analytics DTO.
     */
    public static class BookingAnalytics {
        private final long totalBookings;
        private final long pendingBookings;
        private final long confirmedBookings;
        private final long cancelledBookings;
        private final long completedBookings;
        private final long noShowBookings;
        private final long paidBookings;
        private final long refundedBookings;

        public BookingAnalytics(long totalBookings, long pendingBookings, long confirmedBookings,
                long cancelledBookings, long completedBookings, long noShowBookings,
                long paidBookings, long refundedBookings) {
            this.totalBookings = totalBookings;
            this.pendingBookings = pendingBookings;
            this.confirmedBookings = confirmedBookings;
            this.cancelledBookings = cancelledBookings;
            this.completedBookings = completedBookings;
            this.noShowBookings = noShowBookings;
            this.paidBookings = paidBookings;
            this.refundedBookings = refundedBookings;
        }

        public long getTotalBookings() {
            return totalBookings;
        }

        public long getPendingBookings() {
            return pendingBookings;
        }

        public long getConfirmedBookings() {
            return confirmedBookings;
        }

        public long getCancelledBookings() {
            return cancelledBookings;
        }

        public long getCompletedBookings() {
            return completedBookings;
        }

        public long getNoShowBookings() {
            return noShowBookings;
        }

        public long getPaidBookings() {
            return paidBookings;
        }

        public long getRefundedBookings() {
            return refundedBookings;
        }
    }

    /**
     * Revenue analytics DTO.
     */
    public static class RevenueAnalytics {
        private final BigDecimal totalRevenue;
        private final BigDecimal totalRefunds;
        private final BigDecimal netRevenue;
        private final long completedBookings;
        private final long cancelledBookings;

        public RevenueAnalytics(BigDecimal totalRevenue, BigDecimal totalRefunds, BigDecimal netRevenue,
                long completedBookings, long cancelledBookings) {
            this.totalRevenue = totalRevenue;
            this.totalRefunds = totalRefunds;
            this.netRevenue = netRevenue;
            this.completedBookings = completedBookings;
            this.cancelledBookings = cancelledBookings;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public BigDecimal getTotalRefunds() {
            return totalRefunds;
        }

        public BigDecimal getNetRevenue() {
            return netRevenue;
        }

        public long getCompletedBookings() {
            return completedBookings;
        }

        public long getCancelledBookings() {
            return cancelledBookings;
        }
    }

    /**
     * Platform fee analytics DTO.
     */
    public static class PlatformFeeAnalytics {
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final BigDecimal supplierFees;
        private final BigDecimal associationFees;
        private final BigDecimal totalFees;

        public PlatformFeeAnalytics(LocalDate startDate, LocalDate endDate,
                BigDecimal supplierFees, BigDecimal associationFees, BigDecimal totalFees) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.supplierFees = supplierFees;
            this.associationFees = associationFees;
            this.totalFees = totalFees;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public BigDecimal getSupplierFees() {
            return supplierFees;
        }

        public BigDecimal getAssociationFees() {
            return associationFees;
        }

        public BigDecimal getTotalFees() {
            return totalFees;
        }
    }

    // ==================== USER ANALYTICS ====================

    /**
     * Get user statistics.
     */
    @Transactional
    public UserAnalytics getUserAnalytics() {
        return new UserAnalytics(
                userRepository.count(),
                userRepository.countByStatus(UserStatus.ACTIVE),
                userRepository.countByStatus(UserStatus.SUSPENDED),
                userRepository.countByStatus(UserStatus.DELETED),
                userRepository.countByRole(UserRole.SUPER_ADMIN),
                userRepository.countByRole(UserRole.TRAVELER),
                userRepository.countByRole(UserRole.SUPPLIER_SUBSCRIBER),
                userRepository.countByRole(UserRole.ASSOCIATION_MANAGER));
    }

    /**
     * Get user registration trends.
     */
    @Transactional
    public List<UserRegistrationTrend> getUserRegistrationTrends(LocalDate startDate, LocalDate endDate) {
        // This would typically query a pre-aggregated table or use date truncation
        // For now, return empty list as placeholder
        return List.of();
    }

    /**
     * User analytics DTO.
     */
    public static class UserAnalytics {
        private final long totalUsers;
        private final long activeUsers;
        private final long suspendedUsers;
        private final long deletedUsers;
        private final long superAdminCount;
        private final long travelerCount;
        private final long supplierCount;
        private final long associationManagerCount;

        public UserAnalytics(long totalUsers, long activeUsers, long suspendedUsers, long deletedUsers,
                long superAdminCount, long travelerCount, long supplierCount, long associationManagerCount) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.suspendedUsers = suspendedUsers;
            this.deletedUsers = deletedUsers;
            this.superAdminCount = superAdminCount;
            this.travelerCount = travelerCount;
            this.supplierCount = supplierCount;
            this.associationManagerCount = associationManagerCount;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getSuspendedUsers() {
            return suspendedUsers;
        }

        public long getDeletedUsers() {
            return deletedUsers;
        }

        public long getSuperAdminCount() {
            return superAdminCount;
        }

        public long getTravelerCount() {
            return travelerCount;
        }

        public long getSupplierCount() {
            return supplierCount;
        }

        public long getAssociationManagerCount() {
            return associationManagerCount;
        }
    }

    /**
     * User registration trend DTO.
     */
    public static class UserRegistrationTrend {
        private final LocalDate date;
        private final long count;

        public UserRegistrationTrend(LocalDate date, long count) {
            this.date = date;
            this.count = count;
        }

        public LocalDate getDate() {
            return date;
        }

        public long getCount() {
            return count;
        }
    }

    // ==================== REVIEW ANALYTICS ====================

    /**
     * Get review statistics.
     */
    @Transactional
    public ReviewAnalytics getReviewAnalytics() {
        return new ReviewAnalytics(
                reviewRepository.count(),
                reviewRepository.countByStatus(ApprovalStatus.PENDING),
                reviewRepository.countByStatus(ApprovalStatus.APPROVED),
                reviewRepository.countByStatus(ApprovalStatus.REJECTED),
                reviewRepository.countByStatus(ApprovalStatus.FLAGGED));
    }

    /**
     * Review analytics DTO.
     */
    public static class ReviewAnalytics {
        private final long totalReviews;
        private final long pendingReviews;
        private final long approvedReviews;
        private final long rejectedReviews;
        private final long flaggedReviews;

        public ReviewAnalytics(long totalReviews, long pendingReviews, long approvedReviews,
                long rejectedReviews, long flaggedReviews) {
            this.totalReviews = totalReviews;
            this.pendingReviews = pendingReviews;
            this.approvedReviews = approvedReviews;
            this.rejectedReviews = rejectedReviews;
            this.flaggedReviews = flaggedReviews;
        }

        public long getTotalReviews() {
            return totalReviews;
        }

        public long getPendingReviews() {
            return pendingReviews;
        }

        public long getApprovedReviews() {
            return approvedReviews;
        }

        public long getRejectedReviews() {
            return rejectedReviews;
        }

        public long getFlaggedReviews() {
            return flaggedReviews;
        }
    }

    // ==================== COMPREHENSIVE ANALYTICS ====================

    /**
     * Get comprehensive analytics dashboard.
     */
    @Transactional
    public AnalyticsDashboard getAnalyticsDashboard(LocalDate startDate, LocalDate endDate) {
        return new AnalyticsDashboard(
                getPlatformOverview(),
                getAccommodationAnalytics(),
                getReelAnalytics(),
                getBookingAnalytics(),
                getUserAnalytics(),
                getReviewAnalytics(),
                getRevenueAnalytics(startDate, endDate));
    }

    /**
     * Analytics dashboard DTO.
     */
    public static class AnalyticsDashboard {
        private final PlatformOverview platformOverview;
        private final AccommodationAnalytics accommodationAnalytics;
        private final ReelAnalytics reelAnalytics;
        private final BookingAnalytics bookingAnalytics;
        private final UserAnalytics userAnalytics;
        private final ReviewAnalytics reviewAnalytics;
        private final RevenueAnalytics revenueAnalytics;

        public AnalyticsDashboard(PlatformOverview platformOverview, AccommodationAnalytics accommodationAnalytics,
                ReelAnalytics reelAnalytics, BookingAnalytics bookingAnalytics,
                UserAnalytics userAnalytics, ReviewAnalytics reviewAnalytics,
                RevenueAnalytics revenueAnalytics) {
            this.platformOverview = platformOverview;
            this.accommodationAnalytics = accommodationAnalytics;
            this.reelAnalytics = reelAnalytics;
            this.bookingAnalytics = bookingAnalytics;
            this.userAnalytics = userAnalytics;
            this.reviewAnalytics = reviewAnalytics;
            this.revenueAnalytics = revenueAnalytics;
        }

        public PlatformOverview getPlatformOverview() {
            return platformOverview;
        }

        public AccommodationAnalytics getAccommodationAnalytics() {
            return accommodationAnalytics;
        }

        public ReelAnalytics getReelAnalytics() {
            return reelAnalytics;
        }

        public BookingAnalytics getBookingAnalytics() {
            return bookingAnalytics;
        }

        public UserAnalytics getUserAnalytics() {
            return userAnalytics;
        }

        public ReviewAnalytics getReviewAnalytics() {
            return reviewAnalytics;
        }

        public RevenueAnalytics getRevenueAnalytics() {
            return revenueAnalytics;
        }
    }
}
