package com.travelplatform.application.dto.response.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for booking fee configuration response.
 */
public class BookingFeeConfigResponse {
    private UUID id;
    private BigDecimal serviceFeePercentage;
    private BigDecimal serviceFeeMinimum;
    private BigDecimal serviceFeeMaximum;
    private BigDecimal cleaningFeePercentage;
    private BigDecimal taxRate;
    private Boolean active;
    private LocalDateTime createdAt;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
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
