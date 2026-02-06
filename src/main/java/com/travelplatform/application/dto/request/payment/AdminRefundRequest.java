package com.travelplatform.application.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Admin refund request payload.
 */
public class AdminRefundRequest {
    @NotBlank
    public String reason;

    /**
     * Optional partial refund amount; if null, full refund is performed.
     */
    @PositiveOrZero
    public BigDecimal amount;
}
