package com.travelplatform.infrastructure.security.authorization;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

/**
 * Permission Evaluator for fine-grained authorization checks.
 * This class provides methods to check specific permissions based on user roles and resource ownership.
 */
@ApplicationScoped
public class PermissionEvaluator {

    @Inject
    RoleBasedAccessControl rbac;

    @Inject
    UserRepository userRepository;

    /**
     * Check if current user can view the specified user's profile.
     *
     * @param targetUserId ID of the user whose profile is being viewed
     * @return true if user can view profile, false otherwise
     */
    public boolean canViewUserProfile(UUID targetUserId) {
        // Users can view their own profile
        if (rbac.getCurrentUserId().map(id -> id.equals(targetUserId)).orElse(false)) {
            return true;
        }

        // SUPER_ADMIN can view any profile
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // All authenticated users can view public profiles
        Optional<User> targetUser = userRepository.findById(targetUserId);
        return targetUser.map(user -> user.getStatus() == UserStatus.ACTIVE).orElse(false);
    }

    /**
     * Check if current user can edit the specified user's profile.
     *
     * @param targetUserId ID of the user whose profile is being edited
     * @return true if user can edit profile, false otherwise
     */
    public boolean canEditUserProfile(UUID targetUserId) {
        // Users can edit their own profile
        if (rbac.getCurrentUserId().map(id -> id.equals(targetUserId)).orElse(false)) {
            return true;
        }

        // SUPER_ADMIN can edit any profile
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can delete the specified user's account.
     *
     * @param targetUserId ID of the user whose account is being deleted
     * @return true if user can delete account, false otherwise
     */
    public boolean canDeleteUserAccount(UUID targetUserId) {
        // Users can delete their own account
        if (rbac.getCurrentUserId().map(id -> id.equals(targetUserId)).orElse(false)) {
            return true;
        }

        // SUPER_ADMIN can delete any account
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can suspend the specified user.
     *
     * @param targetUserId ID of the user being suspended
     * @return true if user can suspend, false otherwise
     */
    public boolean canSuspendUser(UUID targetUserId) {
        // Only SUPER_ADMIN can suspend users
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can activate the specified user.
     *
     * @param targetUserId ID of the user being activated
     * @return true if user can activate, false otherwise
     */
    public boolean canActivateUser(UUID targetUserId) {
        // Only SUPER_ADMIN can activate users
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can view the specified accommodation.
     *
     * @param accommodation Accommodation to view
     * @return true if user can view accommodation, false otherwise
     */
    public boolean canViewAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            return false;
        }

        // Approved accommodations are publicly visible
        if (accommodation.getStatus() == ApprovalStatus.APPROVED) {
            return true;
        }

        // SUPER_ADMIN can view any accommodation
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Supplier can view their own accommodations
        if (rbac.getCurrentUserId().map(id -> id.equals(accommodation.getSupplierId())).orElse(false)) {
            return true;
        }

        return false;
    }

    /**
     * Check if current user can create the specified accommodation.
     *
     * @return true if user can create accommodation, false otherwise
     */
    public boolean canCreateAccommodation() {
        return rbac.canCreateAccommodations();
    }

    /**
     * Check if current user can edit the specified accommodation.
     *
     * @param accommodation Accommodation to edit
     * @return true if user can edit accommodation, false otherwise
     */
    public boolean canEditAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            return false;
        }

        // SUPER_ADMIN can edit any accommodation
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Supplier can edit their own accommodations
        return rbac.getCurrentUserId().map(id -> id.equals(accommodation.getSupplierId())).orElse(false);
    }

    /**
     * Check if current user can delete the specified accommodation.
     *
     * @param accommodation Accommodation to delete
     * @return true if user can delete accommodation, false otherwise
     */
    public boolean canDeleteAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            return false;
        }

        // SUPER_ADMIN can delete any accommodation
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Supplier can delete their own accommodations
        return rbac.getCurrentUserId().map(id -> id.equals(accommodation.getSupplierId())).orElse(false);
    }

    /**
     * Check if current user can approve the specified accommodation.
     *
     * @param accommodation Accommodation to approve
     * @return true if user can approve accommodation, false otherwise
     */
    public boolean canApproveAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            return false;
        }

        // Only SUPER_ADMIN can approve accommodations
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can reject the specified accommodation.
     *
     * @param accommodation Accommodation to reject
     * @return true if user can reject accommodation, false otherwise
     */
    public boolean canRejectAccommodation(Accommodation accommodation) {
        if (accommodation == null) {
            return false;
        }

        // Only SUPER_ADMIN can reject accommodations
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can view the specified reel.
     *
     * @param reel Reel to view
     * @return true if user can view reel, false otherwise
     */
    public boolean canViewReel(TravelReel reel) {
        if (reel == null) {
            return false;
        }

        // Approved reels are publicly visible
        if (reel.getStatus() == ApprovalStatus.APPROVED) {
            return true;
        }

        // SUPER_ADMIN can view any reel
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Creator can view their own reels
        if (rbac.getCurrentUserId().map(id -> id.equals(reel.getCreatorId())).orElse(false)) {
            return true;
        }

        return false;
    }

    /**
     * Check if current user can create the specified reel.
     *
     * @return true if user can create reel, false otherwise
     */
    public boolean canCreateReel() {
        return rbac.canCreateReels();
    }

    /**
     * Check if current user can edit the specified reel.
     *
     * @param reel Reel to edit
     * @return true if user can edit reel, false otherwise
     */
    public boolean canEditReel(TravelReel reel) {
        if (reel == null) {
            return false;
        }

        // SUPER_ADMIN can edit any reel
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Creator can edit their own reels
        return rbac.getCurrentUserId().map(id -> id.equals(reel.getCreatorId())).orElse(false);
    }

    /**
     * Check if current user can delete the specified reel.
     *
     * @param reel Reel to delete
     * @return true if user can delete reel, false otherwise
     */
    public boolean canDeleteReel(TravelReel reel) {
        if (reel == null) {
            return false;
        }

        // SUPER_ADMIN can delete any reel
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Creator can delete their own reels
        return rbac.getCurrentUserId().map(id -> id.equals(reel.getCreatorId())).orElse(false);
    }

    /**
     * Check if current user can approve the specified reel.
     *
     * @param reel Reel to approve
     * @return true if user can approve reel, false otherwise
     */
    public boolean canApproveReel(TravelReel reel) {
        if (reel == null) {
            return false;
        }

        // Only SUPER_ADMIN can approve reels
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can reject the specified reel.
     *
     * @param reel Reel to reject
     * @return true if user can reject reel, false otherwise
     */
    public boolean canRejectReel(TravelReel reel) {
        if (reel == null) {
            return false;
        }

        // Only SUPER_ADMIN can reject reels
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can flag the specified reel.
     *
     * @param reel Reel to flag
     * @return true if user can flag reel, false otherwise
     */
    public boolean canFlagReel(TravelReel reel) {
        if (reel == null) {
            return false;
        }

        // Only SUPER_ADMIN can flag reels
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can view the specified booking.
     *
     * @param booking Booking to view
     * @return true if user can view booking, false otherwise
     */
    public boolean canViewBooking(Booking booking) {
        if (booking == null) {
            return false;
        }

        // SUPER_ADMIN can view any booking
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // User can view their own bookings
        if (rbac.getCurrentUserId().map(id -> id.equals(booking.getUserId())).orElse(false)) {
            return true;
        }

        // Supplier can view bookings for their accommodations
        return rbac.getCurrentUserId().map(id -> id.equals(booking.getAccommodationId())).orElse(false);
    }

    /**
     * Check if current user can create the specified booking.
     *
     * @return true if user can create booking, false otherwise
     */
    public boolean canCreateBooking() {
        return rbac.canCreateBookings();
    }

    /**
     * Check if current user can cancel the specified booking.
     *
     * @param booking Booking to cancel
     * @return true if user can cancel booking, false otherwise
     */
    public boolean canCancelBooking(Booking booking) {
        if (booking == null) {
            return false;
        }

        // SUPER_ADMIN can cancel any booking
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // User can cancel their own bookings
        if (rbac.getCurrentUserId().map(id -> id.equals(booking.getUserId())).orElse(false)) {
            // Can only cancel pending or confirmed bookings
            return booking.getStatus() == BookingStatus.PENDING || 
                   booking.getStatus() == BookingStatus.CONFIRMED;
        }

        // Supplier can cancel bookings for their accommodations
        if (rbac.getCurrentUserId().map(id -> id.equals(booking.getAccommodationId())).orElse(false)) {
            return true;
        }

        return false;
    }

    /**
     * Check if current user can view the specified review.
     *
     * @param review Review to view
     * @return true if user can view review, false otherwise
     */
    public boolean canViewReview(Review review) {
        if (review == null) {
            return false;
        }

        // Approved reviews are publicly visible
        if (review.getStatus() == ApprovalStatus.APPROVED) {
            return true;
        }

        // SUPER_ADMIN can view any review
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Reviewer can view their own reviews
        if (rbac.getCurrentUserId().map(id -> id.equals(review.getReviewerId())).orElse(false)) {
            return true;
        }

        return false;
    }

    /**
     * Check if current user can create the specified review.
     *
     * @return true if user can create review, false otherwise
     */
    public boolean canCreateReview() {
        return rbac.canCreateReviews();
    }

    /**
     * Check if current user can edit the specified review.
     *
     * @param review Review to edit
     * @return true if user can edit review, false otherwise
     */
    public boolean canEditReview(Review review) {
        if (review == null) {
            return false;
        }

        // SUPER_ADMIN can edit any review
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Reviewer can edit their own reviews
        return rbac.getCurrentUserId().map(id -> id.equals(review.getReviewerId())).orElse(false);
    }

    /**
     * Check if current user can delete the specified review.
     *
     * @param review Review to delete
     * @return true if user can delete review, false otherwise
     */
    public boolean canDeleteReview(Review review) {
        if (review == null) {
            return false;
        }

        // SUPER_ADMIN can delete any review
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Reviewer can delete their own reviews
        return rbac.getCurrentUserId().map(id -> id.equals(review.getReviewerId())).orElse(false);
    }

    /**
     * Check if current user can respond to the specified review.
     *
     * @param review Review to respond to
     * @return true if user can respond, false otherwise
     */
    public boolean canRespondToReview(Review review) {
        if (review == null) {
            return false;
        }

        // SUPER_ADMIN can respond to any review
        if (rbac.isSuperAdmin()) {
            return true;
        }

        // Supplier can respond to reviews for their accommodations
        return rbac.getCurrentUserId().map(id -> id.equals(review.getAccommodationId())).orElse(false);
    }

    /**
     * Check if current user can access admin dashboard.
     *
     * @return true if user can access admin dashboard, false otherwise
     */
    public boolean canAccessAdminDashboard() {
        return rbac.canAccessAdminEndpoints();
    }

    /**
     * Check if current user can access supplier dashboard.
     *
     * @return true if user can access supplier dashboard, false otherwise
     */
    public boolean canAccessSupplierDashboard() {
        return rbac.canAccessSupplierEndpoints();
    }

    /**
     * Check if current user can access association dashboard.
     *
     * @return true if user can access association dashboard, false otherwise
     */
    public boolean canAccessAssociationDashboard() {
        return rbac.canAccessAssociationEndpoints();
    }

    /**
     * Check if current user can view analytics.
     *
     * @return true if user can view analytics, false otherwise
     */
    public boolean canViewAnalytics() {
        return rbac.canViewAnalytics();
    }

    /**
     * Check if current user can manage subscriptions.
     *
     * @return true if user can manage subscriptions, false otherwise
     */
    public boolean canManageSubscriptions() {
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can send notifications.
     *
     * @return true if user can send notifications, false otherwise
     */
    public boolean canSendNotifications() {
        return rbac.isSuperAdmin();
    }

    /**
     * Check if current user can access chat.
     *
     * @return true if user can access chat, false otherwise
     */
    public boolean canAccessChat() {
        return rbac.isAuthenticated();
    }

    /**
     * Check if current user can send messages.
     *
     * @return true if user can send messages, false otherwise
     */
    public boolean canSendMessages() {
        return rbac.isAuthenticated();
    }

    /**
     * Check if current user can create events.
     *
     * @return true if user can create events, false otherwise
     */
    public boolean canCreateEvents() {
        return rbac.canCreateEvents();
    }

    /**
     * Check if current user can manage events.
     *
     * @return true if user can manage events, false otherwise
     */
    public boolean canManageEvents() {
        return rbac.canCreateEvents();
    }
}
