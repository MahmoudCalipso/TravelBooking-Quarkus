package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.enums.ApprovalStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for premium_visibility_plans table.
 * This is persistence model for PremiumVisibilityPlan domain entity.
 */
@Entity
@Table(name = "premium_visibility_plans", indexes = {
        @Index(name = "idx_premium_plans_supplier_id", columnList = "supplier_id"),
        @Index(name = "idx_premium_plans_accommodation_id", columnList = "accommodation_id"),
        @Index(name = "idx_premium_plans_status", columnList = "status"),
        @Index(name = "idx_premium_plans_dates", columnList = "start_date, end_date")
})
public class PremiumVisibilityPlanEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "plan_type", nullable = false, length = 50)
    private String planType;

    @Column(name = "accommodation_id")
    private UUID accommodationId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "priority_level", nullable = false)
    private Integer priorityLevel;

    @Column(name = "price_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status = ApprovalStatus.APPROVED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public PremiumVisibilityPlanEntity() {
    }

    // Constructor for creating new entity
    public PremiumVisibilityPlanEntity(UUID id, UUID supplierId, String planType, UUID accommodationId,
            LocalDate startDate, LocalDate endDate, Integer priorityLevel, BigDecimal pricePaid) {
        this.id = id;
        this.supplierId = supplierId;
        this.planType = planType;
        this.accommodationId = accommodationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priorityLevel = priorityLevel;
        this.pricePaid = pricePaid;
        this.status = ApprovalStatus.APPROVED;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(BigDecimal pricePaid) {
        this.pricePaid = pricePaid;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
