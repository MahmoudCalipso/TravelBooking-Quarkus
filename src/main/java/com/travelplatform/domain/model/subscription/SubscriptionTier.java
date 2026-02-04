package com.travelplatform.domain.model.subscription;

import com.travelplatform.domain.valueobject.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a subscription tier definition.
 * This is a value object-like entity defining subscription tiers.
 */
public class SubscriptionTier {
    private final UUID id;
    private String name;
    private final TierType tierType;
    private Money monthlyPrice;
    private Money yearlyPrice;
    private int priorityLevel;
    private int maxAccommodations;
    private boolean includesAnalytics;
    private boolean includesApiAccess;
    private boolean includesFeaturedBadge;
    private String features;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Tier type enumeration.
     */
    public enum TierType {
        BASIC,
        PREMIUM,
        ENTERPRISE
    }

    /**
     * Creates a new SubscriptionTier.
     *
     * @param name                  tier name
     * @param tierType              type of tier
     * @param monthlyPrice           monthly price
     * @param yearlyPrice            yearly price
     * @param priorityLevel          priority level for search ranking
     * @param maxAccommodations     maximum number of accommodations
     * @param includesAnalytics       includes analytics dashboard
     * @param includesApiAccess      includes API access
     * @param includesFeaturedBadge  includes featured badge
     * @param features               list of features
     * @param isActive               whether tier is active
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public SubscriptionTier(String name, TierType tierType, Money monthlyPrice, Money yearlyPrice,
                         int priorityLevel, int maxAccommodations, boolean includesAnalytics,
                         boolean includesApiAccess, boolean includesFeaturedBadge, String features,
                         boolean isActive) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (tierType == null) {
            throw new IllegalArgumentException("Tier type cannot be null");
        }
        if (monthlyPrice == null) {
            throw new IllegalArgumentException("Monthly price cannot be null");
        }
        if (yearlyPrice == null) {
            throw new IllegalArgumentException("Yearly price cannot be null");
        }
        if (priorityLevel < 0) {
            throw new IllegalArgumentException("Priority level cannot be negative");
        }
        if (maxAccommodations < 0) {
            throw new IllegalArgumentException("Max accommodations cannot be negative");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.tierType = tierType;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.priorityLevel = priorityLevel;
        this.maxAccommodations = maxAccommodations;
        this.includesAnalytics = includesAnalytics;
        this.includesApiAccess = includesApiAccess;
        this.includesFeaturedBadge = includesFeaturedBadge;
        this.features = features;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reconstructs a SubscriptionTier from persistence.
     */
    public SubscriptionTier(UUID id, String name, TierType tierType, Money monthlyPrice, Money yearlyPrice,
                         int priorityLevel, int maxAccommodations, boolean includesAnalytics,
                         boolean includesApiAccess, boolean includesFeaturedBadge, String features,
                         boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.tierType = tierType;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.priorityLevel = priorityLevel;
        this.maxAccommodations = maxAccommodations;
        this.includesAnalytics = includesAnalytics;
        this.includesApiAccess = includesApiAccess;
        this.includesFeaturedBadge = includesFeaturedBadge;
        this.features = features;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TierType getTierType() {
        return tierType;
    }

    public Money getMonthlyPrice() {
        return monthlyPrice;
    }

    public Money getYearlyPrice() {
        return yearlyPrice;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public int getMaxAccommodations() {
        return maxAccommodations;
    }

    public boolean includesAnalytics() {
        return includesAnalytics;
    }

    public boolean includesApiAccess() {
        return includesApiAccess;
    }

    public boolean includesFeaturedBadge() {
        return includesFeaturedBadge;
    }

    public String getFeatures() {
        return features;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the tier details.
     *
     * @param name                  new name
     * @param monthlyPrice           new monthly price
     * @param yearlyPrice            new yearly price
     * @param priorityLevel          new priority level
     * @param maxAccommodations     new max accommodations
     * @param includesAnalytics       new analytics flag
     * @param includesApiAccess      new API access flag
     * @param includesFeaturedBadge  new featured badge flag
     * @param features               new features
     */
    public void update(String name, Money monthlyPrice, Money yearlyPrice, int priorityLevel,
                   int maxAccommodations, boolean includesAnalytics, boolean includesApiAccess,
                   boolean includesFeaturedBadge, String features) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (monthlyPrice == null) {
            throw new IllegalArgumentException("Monthly price cannot be null");
        }
        if (yearlyPrice == null) {
            throw new IllegalArgumentException("Yearly price cannot be null");
        }
        if (priorityLevel < 0) {
            throw new IllegalArgumentException("Priority level cannot be negative");
        }
        if (maxAccommodations < 0) {
            throw new IllegalArgumentException("Max accommodations cannot be negative");
        }

        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.priorityLevel = priorityLevel;
        this.maxAccommodations = maxAccommodations;
        this.includesAnalytics = includesAnalytics;
        this.includesApiAccess = includesApiAccess;
        this.includesFeaturedBadge = includesFeaturedBadge;
        this.features = features;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activates the tier.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivates the tier.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the tier is basic.
     *
     * @return true if tier type is BASIC
     */
    public boolean isBasicTier() {
        return tierType == TierType.BASIC;
    }

    /**
     * Checks if the tier is premium.
     *
     * @return true if tier type is PREMIUM
     */
    public boolean isPremiumTier() {
        return tierType == TierType.PREMIUM;
    }

    /**
     * Checks if the tier is enterprise.
     *
     * @return true if tier type is ENTERPRISE
     */
    public boolean isEnterpriseTier() {
        return tierType == TierType.ENTERPRISE;
    }

    /**
     * Calculates the yearly savings compared to monthly.
     *
     * @return savings amount
     */
    public Money getYearlySavings() {
        return monthlyPrice.multiply(12).subtract(yearlyPrice);
    }

    /**
     * Calculates the discount percentage for yearly subscription.
     *
     * @return discount percentage (0-100)
     */
    public double getYearlyDiscountPercentage() {
        Money yearlyMonthly = monthlyPrice.multiply(12);
        if (yearlyMonthly.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        BigDecimal savings = yearlyMonthly.getAmount().subtract(yearlyPrice.getAmount());
        return savings.divide(yearlyMonthly.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionTier that = (SubscriptionTier) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("SubscriptionTier{id=%s, name=%s, tierType=%s, isActive=%s}",
                id, name, tierType, isActive);
    }
}
