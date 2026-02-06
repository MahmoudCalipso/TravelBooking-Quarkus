package com.travelplatform.application.service.admin;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service layer for admin supplier tooling.
 * Currently returns placeholder data; wire to repositories/payment providers as needed.
 */
@ApplicationScoped
public class AdminSupplierToolsService {

    public List<Map<String, Object>> getVerificationQueue(int page, int size) {
        return Collections.emptyList();
    }

    public void approveSupplier(UUID supplierId) {
        // TODO: implement approval logic and audit
    }

    public void rejectSupplier(UUID supplierId, String reason) {
        // TODO: implement rejection logic and notification
    }

    public List<Map<String, Object>> getPayoutAccounts(UUID supplierId) {
        return Collections.emptyList();
    }

    public void processManualPayout(UUID supplierId, BigDecimal amount, String reference, String note) {
        // TODO: call payout provider and persist audit record
    }

    public void suspendSupplier(UUID supplierId, String reason, Integer durationDays, Instant suspensionEnd) {
        // TODO: set suspension flags and schedule restoration
    }

    public void removeSupplier(UUID supplierId, String reason) {
        // TODO: soft delete supplier and related access
    }

    public List<Map<String, Object>> getPerformanceIssues(int page, int size) {
        return Collections.emptyList();
    }
}
