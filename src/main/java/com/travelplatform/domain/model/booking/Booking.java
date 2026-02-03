package com.travelplatform.domain.model.booking;

import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a booking reservation.
 * This is the aggregate root for the booking aggregate.
 */
public class Booking {
    private final UUID id;
    private final UUID userId;
    private final UUID accommodationId;
    private final DateRange dateRange;
    private final int numberOfGuests;
    private final int numberOfAdults;
    private final int numberOfChildren;
    private final int numberOfInfants;
    private final long totalNights;
    private final Money basePricePerNight;
    private final Money totalBasePrice;
    private final Money serviceFee;
    private final Money cleaningFee;
    private final Money taxAmount;
    private final Money discountAmount;
    private final Money totalPrice;
    private final String currency;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private UUID cancelledBy;
    private String specialRequests;
    private String guestMessageToHost;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;

    // Associated entity (part of the aggregate)
    private BookingPayment payment;

    /**
     * Creates a new Booking.
     *
     * @param userId             user ID who is booking
     * @param accommodationId    accommodation ID being booked
     * @param dateRange          check-in and check-out dates
     * @param numberOfGuests     total number of guests
     * @param numberOfAdults     number of adults
     * @param numberOfChildren   number of children
     * @param numberOfInfants    number of infants
     * @param basePricePerNight  nightly rate
     * @param serviceFee         platform commission
     * @param cleaningFee        cleaning fee
     * @param taxAmount          tax amount
     * @param discountAmount     discount amount
     * @param specialRequests    guest notes
     * @param guestMessageToHost message to host
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Booking(UUID userId, UUID accommodationId, DateRange dateRange, int numberOfGuests,
            int numberOfAdults, int numberOfChildren, int numberOfInfants, Money basePricePerNight,
            Money serviceFee, Money cleaningFee, Money taxAmount, Money discountAmount,
            String specialRequests, String guestMessageToHost) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (dateRange == null) {
            throw new IllegalArgumentException("Date range cannot be null");
        }
        if (numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be positive");
        }
        if (numberOfAdults < 0) {
            throw new IllegalArgumentException("Number of adults cannot be negative");
        }
        if (numberOfChildren < 0) {
            throw new IllegalArgumentException("Number of children cannot be negative");
        }
        if (numberOfInfants < 0) {
            throw new IllegalArgumentException("Number of infants cannot be negative");
        }
        if (basePricePerNight == null) {
            throw new IllegalArgumentException("Base price per night cannot be null");
        }
        if (serviceFee == null) {
            throw new IllegalArgumentException("Service fee cannot be null");
        }
        if (cleaningFee == null) {
            throw new IllegalArgumentException("Cleaning fee cannot be null");
        }
        if (taxAmount == null) {
            throw new IllegalArgumentException("Tax amount cannot be null");
        }
        if (discountAmount == null) {
            throw new IllegalArgumentException("Discount amount cannot be null");
        }

        this.id = UUID.randomUUID();
        this.userId = userId;
        this.accommodationId = accommodationId;
        this.dateRange = dateRange;
        this.numberOfGuests = numberOfGuests;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.numberOfInfants = numberOfInfants;
        this.totalNights = dateRange.getNights();
        this.basePricePerNight = basePricePerNight;
        this.totalBasePrice = basePricePerNight.multiply(this.totalNights);
        this.serviceFee = serviceFee;
        this.cleaningFee = cleaningFee;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.totalPrice = this.totalBasePrice.add(serviceFee).add(cleaningFee).add(taxAmount).subtract(discountAmount);
        this.currency = basePricePerNight.getCurrencyCode();
        this.status = BookingStatus.PENDING;
        this.paymentStatus = PaymentStatus.UNPAID;
        this.cancellationReason = null;
        this.cancelledAt = null;
        this.cancelledBy = null;
        this.specialRequests = specialRequests;
        this.guestMessageToHost = guestMessageToHost;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.confirmedAt = null;
        this.payment = null;
    }

    /**
     * Reconstructs a Booking from persistence.
     */
    public Booking(UUID id, UUID userId, UUID accommodationId, DateRange dateRange, int numberOfGuests,
            int numberOfAdults, int numberOfChildren, int numberOfInfants, long totalNights,
            Money basePricePerNight, Money totalBasePrice, Money serviceFee, Money cleaningFee,
            Money taxAmount, Money discountAmount, Money totalPrice, String currency,
            BookingStatus status, PaymentStatus paymentStatus, String cancellationReason,
            LocalDateTime cancelledAt, UUID cancelledBy, String specialRequests,
            String guestMessageToHost, LocalDateTime createdAt, LocalDateTime updatedAt,
            LocalDateTime confirmedAt) {
        this.id = id;
        this.userId = userId;
        this.accommodationId = accommodationId;
        this.dateRange = dateRange;
        this.numberOfGuests = numberOfGuests;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.numberOfInfants = numberOfInfants;
        this.totalNights = totalNights;
        this.basePricePerNight = basePricePerNight;
        this.totalBasePrice = totalBasePrice;
        this.serviceFee = serviceFee;
        this.cleaningFee = cleaningFee;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.cancellationReason = cancellationReason;
        this.cancelledAt = cancelledAt;
        this.cancelledBy = cancelledBy;
        this.specialRequests = specialRequests;
        this.guestMessageToHost = guestMessageToHost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.confirmedAt = confirmedAt;
        this.payment = null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public LocalDate getCheckInDate() {
        return dateRange.getStartDate();
    }

    public LocalDate getCheckOutDate() {
        return dateRange.getEndDate();
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public int getNumberOfInfants() {
        return numberOfInfants;
    }

    public long getTotalNights() {
        return totalNights;
    }

    public Money getBasePricePerNight() {
        return basePricePerNight;
    }

    public Money getTotalBasePrice() {
        return totalBasePrice;
    }

    public Money getServiceFee() {
        return serviceFee;
    }

    public Money getCleaningFee() {
        return cleaningFee;
    }

    public Money getTaxAmount() {
        return taxAmount;
    }

    public Money getDiscountAmount() {
        return discountAmount;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public UUID getCancelledBy() {
        return cancelledBy;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public String getGuestMessageToHost() {
        return guestMessageToHost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public BookingPayment getPayment() {
        return payment;
    }

    /**
     * Sets the payment record.
     *
     * @param payment booking payment
     */
    public void setPayment(BookingPayment payment) {
        this.payment = payment;
        this.updatedAt = LocalDateTime.now();
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
        this.updatedAt = LocalDateTime.now();
    }

    public void setGuestMessageToHost(String guestMessageToHost) {
        this.guestMessageToHost = guestMessageToHost;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Confirms the booking.
     */
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancels the booking.
     *
     * @param reason      cancellation reason
     * @param cancelledBy user ID who cancelled
     */
    public void cancel(String reason, UUID cancelledBy) {
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the booking as completed.
     */
    public void complete() {
        this.status = BookingStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the booking as no-show.
     */
    public void markAsNoShow() {
        this.status = BookingStatus.NO_SHOW;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the payment status.
     *
     * @param paymentStatus new payment status
     */
    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the booking is confirmed.
     *
     * @return true if status is CONFIRMED
     */
    public boolean isConfirmed() {
        return this.status == BookingStatus.CONFIRMED;
    }

    /**
     * Checks if the booking is pending.
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == BookingStatus.PENDING;
    }

    /**
     * Checks if the booking is cancelled.
     *
     * @return true if status is CANCELLED
     */
    public boolean isCancelled() {
        return this.status == BookingStatus.CANCELLED;
    }

    /**
     * Checks if the booking is completed.
     *
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return this.status == BookingStatus.COMPLETED;
    }

    /**
     * Checks if the booking is a no-show.
     *
     * @return true if status is NO_SHOW
     */
    public boolean isNoShow() {
        return this.status == BookingStatus.NO_SHOW;
    }

    /**
     * Checks if the booking is paid.
     *
     * @return true if payment status is PAID
     */
    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.PAID;
    }

    /**
     * Checks if the booking is unpaid.
     *
     * @return true if payment status is UNPAID
     */
    public boolean isUnpaid() {
        return this.paymentStatus == PaymentStatus.UNPAID;
    }

    /**
     * Checks if the booking is in the past.
     *
     * @return true if check-out date is in the past
     */
    public boolean isInPast() {
        return dateRange.getEndDate().isBefore(LocalDate.now());
    }

    /**
     * Checks if the booking is in the future.
     *
     * @return true if check-in date is in the future
     */
    public boolean isInFuture() {
        return dateRange.getStartDate().isAfter(LocalDate.now());
    }

    /**
     * Checks if the booking is active (confirmed and not completed/cancelled).
     *
     * @return true if booking is active
     */
    public boolean isActive() {
        return isConfirmed() && !isCompleted() && !isCancelled() && !isNoShow();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Booking that = (Booking) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Booking{id=%s, userId=%s, accommodationId=%s, status=%s}",
                id, userId, accommodationId, status);
    }
}
