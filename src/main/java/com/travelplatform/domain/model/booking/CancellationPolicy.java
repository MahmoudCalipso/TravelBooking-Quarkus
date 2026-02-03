package com.travelplatform.domain.model.booking;

import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a cancellation policy for bookings.
 * This is part of the Booking aggregate.
 */
public class CancellationPolicy {
    private final UUID id;
    private final UUID accommodationId;
    private PolicyType policyType;
    private int freeCancellationDays;
    private int fullRefundBeforeDays;
    private int partialRefundBeforeDays;
    private double partialRefundPercentage;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Cancellation policy type enumeration.
     */
    public enum PolicyType {
        FLEXIBLE,
        MODERATE,
        STRICT,
        SUPER_STRICT
    }

    /**
     * Creates a new CancellationPolicy.
     *
     * @param accommodationId         accommodation ID
     * @param policyType              policy type
     * @param freeCancellationDays    days before check-in for free cancellation
     * @param fullRefundBeforeDays    days before check-in for full refund
     * @param partialRefundBeforeDays days before check-in for partial refund
     * @param partialRefundPercentage percentage for partial refund (0-100)
     * @param description             policy description
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public CancellationPolicy(UUID accommodationId, PolicyType policyType, int freeCancellationDays,
            int fullRefundBeforeDays, int partialRefundBeforeDays, double partialRefundPercentage,
            String description) {
        if (accommodationId == null) {
            throw new IllegalArgumentException("Accommodation ID cannot be null");
        }
        if (policyType == null) {
            throw new IllegalArgumentException("Policy type cannot be null");
        }
        if (freeCancellationDays < 0) {
            throw new IllegalArgumentException("Free cancellation days cannot be negative");
        }
        if (fullRefundBeforeDays < 0) {
            throw new IllegalArgumentException("Full refund before days cannot be negative");
        }
        if (partialRefundBeforeDays < 0) {
            throw new IllegalArgumentException("Partial refund before days cannot be negative");
        }
        if (partialRefundPercentage < 0 || partialRefundPercentage > 100) {
            throw new IllegalArgumentException("Partial refund percentage must be between 0 and 100");
        }

        this.id = UUID.randomUUID();
        this.accommodationId = accommodationId;
        this.policyType = policyType;
        this.freeCancellationDays = freeCancellationDays;
        this.fullRefundBeforeDays = fullRefundBeforeDays;
        this.partialRefundBeforeDays = partialRefundBeforeDays;
        this.partialRefundPercentage = partialRefundPercentage;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a CancellationPolicy from persistence.
     */
    public CancellationPolicy(UUID id, UUID accommodationId, PolicyType policyType, int freeCancellationDays,
            int fullRefundBeforeDays, int partialRefundBeforeDays, double partialRefundPercentage,
            String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.policyType = policyType;
        this.freeCancellationDays = freeCancellationDays;
        this.fullRefundBeforeDays = fullRefundBeforeDays;
        this.partialRefundBeforeDays = partialRefundBeforeDays;
        this.partialRefundPercentage = partialRefundPercentage;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public int getFreeCancellationDays() {
        return freeCancellationDays;
    }

    public int getFullRefundBeforeDays() {
        return fullRefundBeforeDays;
    }

    public int getPartialRefundBeforeDays() {
        return partialRefundBeforeDays;
    }

    public double getPartialRefundPercentage() {
        return partialRefundPercentage;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the policy details.
     *
     * @param policyType              new policy type
     * @param freeCancellationDays    new free cancellation days
     * @param fullRefundBeforeDays    new full refund before days
     * @param partialRefundBeforeDays new partial refund before days
     * @param partialRefundPercentage new partial refund percentage
     * @param description             new description
     */
    public void update(PolicyType policyType, int freeCancellationDays, int fullRefundBeforeDays,
            int partialRefundBeforeDays, double partialRefundPercentage, String description) {
        if (policyType == null) {
            throw new IllegalArgumentException("Policy type cannot be null");
        }
        if (freeCancellationDays < 0) {
            throw new IllegalArgumentException("Free cancellation days cannot be negative");
        }
        if (fullRefundBeforeDays < 0) {
            throw new IllegalArgumentException("Full refund before days cannot be negative");
        }
        if (partialRefundBeforeDays < 0) {
            throw new IllegalArgumentException("Partial refund before days cannot be negative");
        }
        if (partialRefundPercentage < 0 || partialRefundPercentage > 100) {
            throw new IllegalArgumentException("Partial refund percentage must be between 0 and 100");
        }

        this.policyType = policyType;
        this.freeCancellationDays = freeCancellationDays;
        this.fullRefundBeforeDays = fullRefundBeforeDays;
        this.partialRefundBeforeDays = partialRefundBeforeDays;
        this.partialRefundPercentage = partialRefundPercentage;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the refund amount for a cancellation.
     *
     * @param totalAmount      total booking amount
     * @param checkInDate      check-in date
     * @param cancellationDate date of cancellation
     * @return refund amount
     */
    public Money calculateRefund(Money totalAmount, LocalDate checkInDate, LocalDate cancellationDate) {
        if (totalAmount == null) {
            throw new IllegalArgumentException("Total amount cannot be null");
        }
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        if (cancellationDate == null) {
            throw new IllegalArgumentException("Cancellation date cannot be null");
        }

        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, checkInDate);

        if (daysUntilCheckIn >= freeCancellationDays) {
            // Full refund
            return totalAmount;
        } else if (daysUntilCheckIn >= fullRefundBeforeDays) {
            // Full refund
            return totalAmount;
        } else if (daysUntilCheckIn >= partialRefundBeforeDays) {
            // Partial refund
            return totalAmount.multiply(partialRefundPercentage / 100.0);
        } else {
            // No refund
            return new Money(0, totalAmount.getCurrencyCode());
        }
    }

    /**
     * Checks if cancellation is free.
     *
     * @param checkInDate      check-in date
     * @param cancellationDate date of cancellation
     * @return true if cancellation is free
     */
    public boolean isFreeCancellation(LocalDate checkInDate, LocalDate cancellationDate) {
        if (checkInDate == null || cancellationDate == null) {
            return false;
        }
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, checkInDate);
        return daysUntilCheckIn >= freeCancellationDays;
    }

    /**
     * Checks if full refund is available.
     *
     * @param checkInDate      check-in date
     * @param cancellationDate date of cancellation
     * @return true if full refund is available
     */
    public boolean isFullRefundAvailable(LocalDate checkInDate, LocalDate cancellationDate) {
        if (checkInDate == null || cancellationDate == null) {
            return false;
        }
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, checkInDate);
        return daysUntilCheckIn >= fullRefundBeforeDays;
    }

    /**
     * Checks if partial refund is available.
     *
     * @param checkInDate      check-in date
     * @param cancellationDate date of cancellation
     * @return true if partial refund is available
     */
    public boolean isPartialRefundAvailable(LocalDate checkInDate, LocalDate cancellationDate) {
        if (checkInDate == null || cancellationDate == null) {
            return false;
        }
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, checkInDate);
        return daysUntilCheckIn >= partialRefundBeforeDays;
    }

    /**
     * Gets the refund percentage for a cancellation.
     *
     * @param checkInDate      check-in date
     * @param cancellationDate date of cancellation
     * @return refund percentage (0-100)
     */
    public double getRefundPercentage(LocalDate checkInDate, LocalDate cancellationDate) {
        if (checkInDate == null || cancellationDate == null) {
            return 0;
        }
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, checkInDate);

        if (daysUntilCheckIn >= freeCancellationDays) {
            return 100.0;
        } else if (daysUntilCheckIn >= fullRefundBeforeDays) {
            return 100.0;
        } else if (daysUntilCheckIn >= partialRefundBeforeDays) {
            return partialRefundPercentage;
        } else {
            return 0.0;
        }
    }

    /**
     * Creates a flexible cancellation policy.
     *
     * @param accommodationId accommodation ID
     * @return flexible cancellation policy
     */
    public static CancellationPolicy createFlexible(UUID accommodationId) {
        return new CancellationPolicy(
                accommodationId,
                PolicyType.FLEXIBLE,
                1, // Free cancellation 1 day before
                1, // Full refund 1 day before
                0, // No partial refund window
                0, // No partial refund
                "Free cancellation up to 1 day before check-in. Full refund if cancelled at least 1 day before check-in.");
    }

    /**
     * Creates a moderate cancellation policy.
     *
     * @param accommodationId accommodation ID
     * @return moderate cancellation policy
     */
    public static CancellationPolicy createModerate(UUID accommodationId) {
        return new CancellationPolicy(
                accommodationId,
                PolicyType.MODERATE,
                5, // Free cancellation 5 days before
                5, // Full refund 5 days before
                1, // Partial refund 1 day before
                50, // 50% refund
                "Free cancellation up to 5 days before check-in. 50% refund if cancelled 1-5 days before check-in.");
    }

    /**
     * Creates a strict cancellation policy.
     *
     * @param accommodationId accommodation ID
     * @return strict cancellation policy
     */
    public static CancellationPolicy createStrict(UUID accommodationId) {
        return new CancellationPolicy(
                accommodationId,
                PolicyType.STRICT,
                14, // Free cancellation 14 days before
                14, // Full refund 14 days before
                7, // Partial refund 7 days before
                50, // 50% refund
                "Free cancellation up to 14 days before check-in. 50% refund if cancelled 7-14 days before check-in.");
    }

    /**
     * Creates a super strict cancellation policy.
     *
     * @param accommodationId accommodation ID
     * @return super strict cancellation policy
     */
    public static CancellationPolicy createSuperStrict(UUID accommodationId) {
        return new CancellationPolicy(
                accommodationId,
                PolicyType.SUPER_STRICT,
                30, // Free cancellation 30 days before
                30, // Full refund 30 days before
                14, // Partial refund 14 days before
                50, // 50% refund
                "Free cancellation up to 30 days before check-in. 50% refund if cancelled 14-30 days before check-in.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CancellationPolicy that = (CancellationPolicy) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("CancellationPolicy{id=%s, accommodationId=%s, policyType=%s}",
                id, accommodationId, policyType);
    }
}
