package com.travelplatform.domain.enums;

/**
 * Enumeration of booking statuses.
 * Tracks the lifecycle of a booking from creation to completion.
 */
public enum BookingStatus {
    /**
     * Booking has been created but not yet confirmed.
     * Awaiting payment or supplier confirmation.
     */
    PENDING,

    /**
     * Booking has been confirmed and payment processed.
     * Reservation is guaranteed.
     */
    CONFIRMED,

    /**
     * Booking has been cancelled by user or supplier.
     * Refund may be processed based on cancellation policy.
     */
    CANCELLED,

    /**
     * Guest has completed their stay.
     * Booking is finalized, review can be submitted.
     */
    COMPLETED,

    /**
     * Guest did not show up for the booking.
     * No refund is typically provided.
     */
    NO_SHOW
}
