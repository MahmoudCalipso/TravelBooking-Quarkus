package com.travelplatform.domain.enums;

/**
 * Enumeration of notification types.
 */
public enum NotificationType {
    /**
     * Booking has been confirmed.
     */
    BOOKING_CONFIRMED,

    /**
     * Booking has been cancelled.
     */
    BOOKING_CANCELLED,

    /**
     * Payment has been received.
     */
    PAYMENT_RECEIVED,

    /**
     * Reel has been approved.
     */
    REEL_APPROVED,

    /**
     * Someone liked your reel.
     */
    REEL_LIKED,

    /**
     * New comment on your reel.
     */
    NEW_COMMENT,

    /**
     * New review on your property.
     */
    NEW_REVIEW,

    /**
     * New message received.
     */
    NEW_MESSAGE,

    /**
     * Event reminder.
     */
    EVENT_REMINDER,

    /**
     * Accommodation approved.
     */
    ACCOMMODATION_APPROVED,

    /**
     * Accommodation rejected.
     */
    ACCOMMODATION_REJECTED,

    /**
     * Password reset requested.
     */
    PASSWORD_RESET,

    /**
     * Email verification.
     */
    EMAIL_VERIFICATION,

    /**
     * Welcome message.
     */
    WELCOME
}
