package com.travelplatform.application.dto.response.payment;

import com.travelplatform.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dispute/chargeback view for admins to triage problematic payments.
 */
public class PaymentDisputeResponse {
    public UUID paymentId;
    public UUID bookingId;
    public String transactionId;
    public BigDecimal amount;
    public String currency;
    public PaymentStatus status;
    public String reason;
    public BigDecimal refundAmount;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
