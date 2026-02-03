package com.travelplatform.domain.enums;

/**
 * Enumeration of payment transaction statuses.
 */
public enum PaymentStatus {
    /**
     * Payment has been initiated but not yet processed.
     */
    UNPAID,

    /**
     * Payment has been successfully processed.
     */
    PAID,

    /**
     * Payment has been refunded to the customer.
     */
    REFUNDED,

    /**
     * Partial refund has been processed.
     */
    PARTIALLY_REFUNDED,

    /**
     * Payment processing has failed.
     */
    FAILED,

    /**
     * Payment is currently being processed.
     */
    /**
     * Payment is currently being processed.
     */
    PROCESSING,

    /**
     * Payment is pending (synonym for UNPAID/Initialized).
     */
    PENDING,

    /**
     * Payment is completed (synonym for PAID).
     */
    COMPLETED
}
