package com.travelplatform.domain.model.booking;

import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a payment transaction for a booking.
 * This is part of the Booking aggregate.
 */
public class BookingPayment {
    private final UUID id;
    private final UUID bookingId;
    private final Money amount;
    private final String currency;
    private final PaymentMethod paymentMethod;
    private final String paymentProvider;
    private String transactionId;
    private PaymentStatus status;
    private String failureReason;
    private Money refundAmount;
    private String refundReason;
    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;

    /**
     * Payment method enumeration.
     */
    public enum PaymentMethod {
        CARD,
        PAYPAL,
        BANK_TRANSFER,
        CRYPTO,
        APPLE_PAY,
        GOOGLE_PAY
    }

    /**
     * Creates a new BookingPayment.
     *
     * @param bookingId       booking ID
     * @param amount          payment amount
     * @param currency        currency code
     * @param paymentMethod   payment method
     * @param paymentProvider payment provider (e.g., STRIPE, PAYPAL)
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public BookingPayment(UUID bookingId, Money amount, String currency, PaymentMethod paymentMethod, String paymentProvider) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (paymentProvider == null || paymentProvider.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment provider cannot be null or empty");
        }

        this.id = UUID.randomUUID();
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentProvider = paymentProvider;
        this.transactionId = null;
        this.status = PaymentStatus.PENDING;
        this.failureReason = null;
        this.refundAmount = null;
        this.refundReason = null;
        this.createdAt = LocalDateTime.now();
        this.paidAt = null;
        this.refundedAt = null;
    }

    /**
     * Reconstructs a BookingPayment from persistence.
     */
    public BookingPayment(UUID id, UUID bookingId, Money amount, String currency, PaymentMethod paymentMethod,
                         String paymentProvider, String transactionId, PaymentStatus status, String failureReason,
                         Money refundAmount, String refundReason, LocalDateTime createdAt, LocalDateTime paidAt,
                         LocalDateTime refundedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentProvider = paymentProvider;
        this.transactionId = transactionId;
        this.status = status;
        this.failureReason = failureReason;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.refundedAt = refundedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Money getRefundAmount() {
        return refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    /**
     * Sets the transaction ID.
     *
     * @param transactionId external payment transaction ID
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Marks the payment as processing.
     */
    public void markAsProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    /**
     * Marks the payment as completed.
     *
     * @param transactionId external transaction ID
     */
    public void markAsCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.paidAt = LocalDateTime.now();
    }

    /**
     * Marks the payment as failed.
     *
     * @param failureReason reason for failure
     */
    public void markAsFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    /**
     * Marks the payment as refunded.
     *
     * @param refundAmount amount refunded
     * @param refundReason reason for refund
     */
    public void markAsRefunded(Money refundAmount, String refundReason) {
        this.status = PaymentStatus.REFUNDED;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
        this.refundedAt = LocalDateTime.now();
    }

    /**
     * Marks the payment as partially refunded.
     *
     * @param refundAmount amount refunded
     * @param refundReason reason for refund
     */
    public void markAsPartiallyRefunded(Money refundAmount, String refundReason) {
        this.status = PaymentStatus.PARTIALLY_REFUNDED;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
        this.refundedAt = LocalDateTime.now();
    }

    /**
     * Checks if the payment is pending.
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    /**
     * Checks if the payment is processing.
     *
     * @return true if status is PROCESSING
     */
    public boolean isProcessing() {
        return this.status == PaymentStatus.PROCESSING;
    }

    /**
     * Checks if the payment is completed.
     *
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    /**
     * Checks if the payment is failed.
     *
     * @return true if status is FAILED
     */
    public boolean isFailed() {
        return this.status == PaymentStatus.FAILED;
    }

    /**
     * Checks if the payment is refunded.
     *
     * @return true if status is REFUNDED
     */
    public boolean isRefunded() {
        return this.status == PaymentStatus.REFUNDED;
    }

    /**
     * Checks if the payment is partially refunded.
     *
     * @return true if status is PARTIALLY_REFUNDED
     */
    public boolean isPartiallyRefunded() {
        return this.status == PaymentStatus.PARTIALLY_REFUNDED;
    }

    /**
     * Checks if the payment can be refunded.
     *
     * @return true if payment is completed and not already refunded
     */
    public boolean canBeRefunded() {
        return this.status == PaymentStatus.COMPLETED;
    }

    /**
     * Checks if the payment is successful.
     *
     * @return true if payment is completed
     */
    public boolean isSuccessful() {
        return this.status == PaymentStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingPayment that = (BookingPayment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("BookingPayment{id=%s, bookingId=%s, amount=%s, status=%s}",
                id, bookingId, amount, status);
    }
}
