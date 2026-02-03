package com.travelplatform.domain.enums;

/**
 * Enumeration of content approval statuses.
 * Used for accommodations, reels, events, and reviews that require admin approval.
 */
public enum ApprovalStatus {
    /**
     * Content is newly created and awaiting admin review.
     * Not visible to public, only to creator and admins.
     */
    PENDING,

    /**
     * Content has been reviewed and approved by admin.
     * Publicly visible and searchable.
     */
    APPROVED,

    /**
     * Content has been rejected by admin.
     * Not publicly visible, creator can edit and resubmit.
     */
    REJECTED,

    /**
     * Content has been flagged by users for review.
     * Hidden from public, pending admin investigation.
     */
    FLAGGED
}
