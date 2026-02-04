package com.travelplatform.domain.model.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing booking fee configuration.
 */
public class BookingFeeConfig {
    private final UUID id;
    private BigDecimal serviceFeePercentage;
    private BigDecimal serviceFeeMinimum;
    private BigDecimal serviceFeeMaximum;
    private BigDecimal cleaningFeePercentage;
    private BigDecimal taxRate;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingFeeConfig(BigDecimal serviceFeePercentage,
            BigDecimal serviceFeeMinimum,
            BigDecimal serviceFeeMaximum,
            BigDecimal cleaningFeePercentage,
            BigDecimal taxRate) {
        this.id = UUID.randomUUID();
        applyValidation(serviceFeePercentage, serviceFeeMinimum, serviceFeeMaximum, cleaningFeePercentage, taxRate);
        this.serviceFeePercentage = serviceFeePercentage;
        this.serviceFeeMinimum = serviceFeeMinimum;
        this.serviceFeeMaximum = serviceFeeMaximum;
        this.cleaningFeePercentage = cleaningFeePercentage;
        this.taxRate = taxRate;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BookingFeeConfig(UUID id,
            BigDecimal serviceFeePercentage,
            BigDecimal serviceFeeMinimum,
            BigDecimal serviceFeeMaximum,
            BigDecimal cleaningFeePercentage,
            BigDecimal taxRate,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.serviceFeePercentage = serviceFeePercentage;
        this.serviceFeeMinimum = serviceFeeMinimum;
        this.serviceFeeMaximum = serviceFeeMaximum;
        this.cleaningFeePercentage = cleaningFeePercentage;
        this.taxRate = taxRate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getServiceFeePercentage() {
        return serviceFeePercentage;
    }

    public BigDecimal getServiceFeeMinimum() {
        return serviceFeeMinimum;
    }

    public BigDecimal getServiceFeeMaximum() {
        return serviceFeeMaximum;
    }

    public BigDecimal getCleaningFeePercentage() {
        return cleaningFeePercentage;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(BigDecimal serviceFeePercentage,
            BigDecimal serviceFeeMinimum,
            BigDecimal serviceFeeMaximum,
            BigDecimal cleaningFeePercentage,
            BigDecimal taxRate) {
        applyValidation(serviceFeePercentage, serviceFeeMinimum, serviceFeeMaximum, cleaningFeePercentage, taxRate);
        this.serviceFeePercentage = serviceFeePercentage;
        this.serviceFeeMinimum = serviceFeeMinimum;
        this.serviceFeeMaximum = serviceFeeMaximum;
        this.cleaningFeePercentage = cleaningFeePercentage;
        this.taxRate = taxRate;
        this.updatedAt = LocalDateTime.now();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    private void applyValidation(BigDecimal serviceFeePercentage,
            BigDecimal serviceFeeMinimum,
            BigDecimal serviceFeeMaximum,
            BigDecimal cleaningFeePercentage,
            BigDecimal taxRate) {
        if (serviceFeePercentage == null) {
            throw new IllegalArgumentException("Service fee percentage cannot be null");
        }
        if (serviceFeePercentage.compareTo(BigDecimal.ZERO) < 0
                || serviceFeePercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Service fee percentage must be between 0 and 100");
        }
        if (cleaningFeePercentage == null) {
            throw new IllegalArgumentException("Cleaning fee percentage cannot be null");
        }
        if (cleaningFeePercentage.compareTo(BigDecimal.ZERO) < 0
                || cleaningFeePercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Cleaning fee percentage must be between 0 and 100");
        }
        if (taxRate == null) {
            throw new IllegalArgumentException("Tax rate cannot be null");
        }
        if (taxRate.compareTo(BigDecimal.ZERO) < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1");
        }
        if (serviceFeeMinimum != null && serviceFeeMinimum.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Service fee minimum cannot be negative");
        }
        if (serviceFeeMaximum != null && serviceFeeMaximum.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Service fee maximum cannot be negative");
        }
        if (serviceFeeMinimum != null && serviceFeeMaximum != null
                && serviceFeeMinimum.compareTo(serviceFeeMaximum) > 0) {
            throw new IllegalArgumentException("Service fee minimum cannot exceed maximum");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingFeeConfig that = (BookingFeeConfig) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
