package com.travelplatform.domain.model.subscription;

import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a premium visibility plan for an accommodation.
 * This is the aggregate root for the subscription aggregate.
 */
public class PremiumVisibilityPlan {
    private final UUID id;
    private final UUID supplierId;
    private final PlanType planType;
    private final UUID accommodationId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int priorityLevel;
    private final Money pricePaid;
    private final String currency;
    private SubscriptionStatus status;
    private final LocalDateTime createdAt;

    /**
     * Plan type enumeration.
     */
    public enum PlanType {
        BASIC,
        PREMIUM,
        ENTERPRISE
    }

    /**
     * Subscription status enumeration.
     */
    public enum SubscriptionStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED
    }

    /**
     * Creates a new PremiumVisibilityPlan.
     *
     * @param supplierId       supplier user ID
     * @param planType         type of plan
     * @param accommodationId  accommodation ID (null for supplier-wide plan)
     * @param startDate        start date of plan
     * @param endDate          end date of plan
     * @param priorityLevel    priority level for search ranking
     * @param pricePaid        amount paid
     * @param currency         currency code
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public PremiumVisibilityPlan(UUID supplierId, PlanType planType, UUID accommodationId,
                              LocalDate startDate, LocalDate endDate, int priorityLevel,
                              Money pricePaid, String currency) {
        if (supplierId == null) {
            throw new IllegalArgumentException("Supplier ID cannot be null");
        }
        if (planType == null) {
            throw new IllegalArgumentException("Plan type cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (priorityLevel < 0) {
            throw new IllegalArgumentException("Priority level cannot be negative");
        }
        if (pricePaid == null) {
            throw new IllegalArgumentException("Price paid cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }

        this.id = UUID.randomUUID();
        this.supplierId = supplierId;
        this.planType = planType;
        this.accommodationId = accommodationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priorityLevel = priorityLevel;
        this.pricePaid = pricePaid;
        this.currency = currency;
        this.status = SubscriptionStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a PremiumVisibilityPlan from persistence.
     */
    public PremiumVisibilityPlan(UUID id, UUID supplierId, PlanType planType, UUID accommodationId,
                              LocalDate startDate, LocalDate endDate, int priorityLevel,
                              Money pricePaid, String currency, SubscriptionStatus status,
                              LocalDateTime createdAt) {
        this.id = id;
        this.supplierId = supplierId;
        this.planType = planType;
        this.accommodationId = accommodationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priorityLevel = priorityLevel;
        this.pricePaid = pricePaid;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public Money getPricePaid() {
        return pricePaid;
    }

    public String getCurrency() {
        return currency;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Cancels the subscription.
     */
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    /**
     * Marks the subscription as expired.
     */
    public void markAsExpired() {
        this.status = SubscriptionStatus.EXPIRED;
    }

    /**
     * Checks if the subscription is active.
     *
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE;
    }

    /**
     * Checks if the subscription is expired.
     *
     * @return true if status is EXPIRED
     */
    public boolean isExpired() {
        return this.status == SubscriptionStatus.EXPIRED;
    }

    /**
     * Checks if the subscription is cancelled.
     *
     * @return true if status is CANCELLED
     */
    public boolean isCancelled() {
        return this.status == SubscriptionStatus.CANCELLED;
    }

    /**
     * Checks if the subscription is currently valid (active and within date range).
     *
     * @return true if subscription is valid
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return isActive() && !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    /**
     * Checks if the subscription is for a specific accommodation.
     *
     * @return true if accommodation ID is present
     */
    public boolean isAccommodationSpecific() {
        return accommodationId != null;
    }

    /**
     * Checks if the subscription is supplier-wide (not tied to specific accommodation).
     *
     * @return true if accommodation ID is null
     */
    public boolean isSupplierWide() {
        return accommodationId == null;
    }

    /**
     * Checks if the subscription is for a basic plan.
     *
     * @return true if plan type is BASIC
     */
    public boolean isBasicPlan() {
        return planType == PlanType.BASIC;
    }

    /**
     * Checks if the subscription is for a premium plan.
     *
     * @return true if plan type is PREMIUM
     */
    public boolean isPremiumPlan() {
        return planType == PlanType.PREMIUM;
    }

    /**
     * Checks if the subscription is for an enterprise plan.
     *
     * @return true if plan type is ENTERPRISE
     */
    public boolean isEnterprisePlan() {
        return planType == PlanType.ENTERPRISE;
    }

    /**
     * Gets the remaining days in the subscription.
     *
     * @return remaining days, or 0 if expired
     */
    public long getRemainingDays() {
        if (!isValid()) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PremiumVisibilityPlan that = (PremiumVisibilityPlan) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("PremiumVisibilityPlan{id=%s, supplierId=%s, planType=%s, accommodationId=%s, status=%s}",
                id, supplierId, planType, accommodationId, status);
    }
}
