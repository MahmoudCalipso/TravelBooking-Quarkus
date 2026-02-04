package com.travelplatform.application.dto.request.booking;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * DTO for updating booking fee configuration.
 */
public class UpdateBookingFeeConfigRequest {

    @DecimalMin(value = "0.0", message = "Service fee percentage must be >= 0")
    @DecimalMax(value = "100.0", message = "Service fee percentage must be <= 100")
    private BigDecimal serviceFeePercentage;

    @DecimalMin(value = "0.0", message = "Service fee minimum must be >= 0")
    private BigDecimal serviceFeeMinimum;

    @DecimalMin(value = "0.0", message = "Service fee maximum must be >= 0")
    private BigDecimal serviceFeeMaximum;

    @DecimalMin(value = "0.0", message = "Cleaning fee percentage must be >= 0")
    @DecimalMax(value = "100.0", message = "Cleaning fee percentage must be <= 100")
    private BigDecimal cleaningFeePercentage;

    @DecimalMin(value = "0.0", message = "Tax rate must be >= 0")
    @DecimalMax(value = "1.0", message = "Tax rate must be <= 1")
    private BigDecimal taxRate;

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
}
