package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for subscription_tiers table.
 * This is persistence model for SubscriptionTier domain entity.
 */
@Entity
@Table(name = "subscription_tiers", indexes = {
    @Index(name = "idx_subscription_tiers_plan_type", columnList = "plan_type")
})
public class SubscriptionTierEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "plan_type", nullable = false, length = 50)
    private String planType;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "max_accommodations")
    private Integer maxAccommodations;

    @Column(name = "priority_placement", nullable = false)
    private boolean priorityPlacement = false;

    @Column(name = "featured_badge", nullable = false)
    private boolean featuredBadge = false;

    @Column(name = "analytics_access", nullable = false)
    private boolean analyticsAccess = false;

    @Column(name = "api_access", nullable = false)
    private boolean apiAccess = false;

    @Column(name = "support_level", length = 50)
    private String supportLevel;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public SubscriptionTierEntity() {
    }

    // Constructor for creating new entity
    public SubscriptionTierEntity(UUID id, String planType, String name, String description, 
                              BigDecimal monthlyPrice, String currency) {
        this.id = id;
        this.planType = planType;
        this.name = name;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.currency = currency;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callback for updating timestamp
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getMaxAccommodations() {
        return maxAccommodations;
    }

    public void setMaxAccommodations(Integer maxAccommodations) {
        this.maxAccommodations = maxAccommodations;
    }

    public boolean isPriorityPlacement() {
        return priorityPlacement;
    }

    public void setPriorityPlacement(boolean priorityPlacement) {
        this.priorityPlacement = priorityPlacement;
    }

    public boolean isFeaturedBadge() {
        return featuredBadge;
    }

    public void setFeaturedBadge(boolean featuredBadge) {
        this.featuredBadge = featuredBadge;
    }

    public boolean isAnalyticsAccess() {
        return analyticsAccess;
    }

    public void setAnalyticsAccess(boolean analyticsAccess) {
        this.analyticsAccess = analyticsAccess;
    }

    public boolean isApiAccess() {
        return apiAccess;
    }

    public void setApiAccess(boolean apiAccess) {
        this.apiAccess = apiAccess;
    }

    public String getSupportLevel() {
        return supportLevel;
    }

    public void setSupportLevel(String supportLevel) {
        this.supportLevel = supportLevel;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
