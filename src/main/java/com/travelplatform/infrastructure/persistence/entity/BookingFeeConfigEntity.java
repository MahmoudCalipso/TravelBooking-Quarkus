package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for booking fee configuration.
 */
@Entity
@Table(name = "booking_fee_config")
public class BookingFeeConfigEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "service_fee_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal serviceFeePercentage;

    @Column(name = "service_fee_minimum", precision = 10, scale = 2)
    private BigDecimal serviceFeeMinimum;

    @Column(name = "service_fee_maximum", precision = 10, scale = 2)
    private BigDecimal serviceFeeMaximum;

    @Column(name = "cleaning_fee_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal cleaningFeePercentage;

    @Column(name = "tax_rate", precision = 5, scale = 4, nullable = false)
    private BigDecimal taxRate;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getServiceFeePercentage() {
        return serviceFeePercentage;
    }

    public void setServiceFeePercentage(BigDecimal serviceFeePercentage) {
        this.serviceFeePercentage = serviceFeePercentage;
    }

    public BigDecimal getServiceFeeMinimum() {
        return serviceFeeMinimum;
    }

    public void setServiceFeeMinimum(BigDecimal serviceFeeMinimum) {
        this.serviceFeeMinimum = serviceFeeMinimum;
    }

    public BigDecimal getServiceFeeMaximum() {
        return serviceFeeMaximum;
    }

    public void setServiceFeeMaximum(BigDecimal serviceFeeMaximum) {
        this.serviceFeeMaximum = serviceFeeMaximum;
    }

    public BigDecimal getCleaningFeePercentage() {
        return cleaningFeePercentage;
    }

    public void setCleaningFeePercentage(BigDecimal cleaningFeePercentage) {
        this.cleaningFeePercentage = cleaningFeePercentage;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
