package com.travelplatform.application.dto.response.payment;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Lightweight view of payment-related webhook/audit entries for admin monitoring.
 */
public class PaymentWebhookLogResponse {
    public UUID id;
    public String action;
    public String entityType;
    public UUID entityId;
    public Map<String, Object> payload;
    public LocalDateTime createdAt;
}
