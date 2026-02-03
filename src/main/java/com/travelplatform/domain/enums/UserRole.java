package com.travelplatform.domain.enums;

/**
 * Enumeration of user roles in the travel platform.
 * Each role has specific permissions and capabilities.
 */
public enum UserRole {
    /**
     * Super administrator with full system access.
     * Manually created in database, not via API.
     * Can approve/reject all content, manage users, view analytics.
     */
    SUPER_ADMIN,

    /**
     * Regular traveler user.
     * Can search, book accommodations, create reels, write reviews.
     * Self-registration via public API.
     */
    TRAVELER,

    /**
     * Accommodation supplier with subscription.
     * Can create/manage accommodation listings, view analytics, respond to reviews.
     * Requires subscription payment.
     */
    SUPPLIER_SUBSCRIBER,

    /**
     * Association/organization manager.
     * Can create travel programs, organize events, manage participants.
     * Requires verification process.
     */
    ASSOCIATION_MANAGER
}
