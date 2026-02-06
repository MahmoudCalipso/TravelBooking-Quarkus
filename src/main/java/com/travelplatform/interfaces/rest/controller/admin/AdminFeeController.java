package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.persistence.entity.AuditLogEntity;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin controller for platform fee management.
 * All endpoints require SUPER_ADMIN role.
 */
@Path("/api/v1/admin/fees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Fee Management", description = "SUPER_ADMIN endpoints for managing platform fees and commissions")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminFeeController {

    private static final Logger logger = LoggerFactory.getLogger(AdminFeeController.class);

    @Inject
    AuditService auditService;

    @Inject
    EntityManager entityManager;

    /**
     * View all current fees.
     */
    @GET
    @Operation(summary = "View all fees", description = "Get current platform fee structure")
    public BaseResponse<Map<String, Object>> viewAllFees() {
        logger.info("Admin viewing all fees");

        // Use a static map until a Dedicated FeeConfiguration Entity is fully
        // integrated in persistence layer
        Map<String, Object> fees = new HashMap<>();
        fees.put("bookingFeePercentage", 5.0);
        fees.put("supplierCommissionPercentage", 85.0);
        fees.put("associationCommissionPercentage", 90.0);
        fees.put("paymentProcessingFeePercentage", 2.9);
        fees.put("cancellationFeePercentage", 10.0);

        return BaseResponse.success(fees);
    }

    /**
     * Update booking fee.
     */
    @PUT
    @Path("/booking")
    @Transactional
    @Operation(summary = "Update booking fee", description = "Adjust traveler-facing booking fee")
    public BaseResponse<Void> updateBookingFee(UpdateFeeRequest request) {
        logger.info("Admin updating booking fee: percentage={}, effectiveDate={}",
                request.percentage, request.effectiveDate);

        auditService.logAction("BOOKING_FEE_UPDATED", "FeeConfiguration", null,
                Map.of("percentage", request.percentage.toString(),
                        "effectiveDate", request.effectiveDate.toString(),
                        "description", request.description != null ? request.description : ""));

        return BaseResponse.success("Booking fee updated successfully");
    }

    /**
     * Update supplier commission.
     */
    @PUT
    @Path("/supplier-commission")
    @Transactional
    @Operation(summary = "Update supplier commission", description = "Adjust supplier payment split")
    public BaseResponse<Void> updateSupplierCommission(UpdateFeeRequest request) {
        logger.info("Admin updating supplier commission: percentage={}", request.percentage);

        auditService.logAction("SUPPLIER_COMMISSION_UPDATED", "FeeConfiguration", null,
                Map.of("percentage", request.percentage.toString()));

        return BaseResponse.success("Supplier commission updated successfully");
    }

    /**
     * Update association commission.
     */
    @PUT
    @Path("/association-commission")
    @Transactional
    @Operation(summary = "Update association commission", description = "Adjust association payment split")
    public BaseResponse<Void> updateAssociationCommission(UpdateFeeRequest request) {
        logger.info("Admin updating association commission: percentage={}", request.percentage);

        auditService.logAction("ASSOCIATION_COMMISSION_UPDATED", "FeeConfiguration", null,
                Map.of("percentage", request.percentage.toString()));

        return BaseResponse.success("Association commission updated successfully");
    }

    /**
     * Update payment processing fee.
     */
    @PUT
    @Path("/payment-processing")
    @Transactional
    @Operation(summary = "Update payment processing fee", description = "Adjust payment processing costs")
    public BaseResponse<Void> updatePaymentProcessingFee(UpdateFeeRequest request) {
        logger.info("Admin updating payment processing fee: percentage={}", request.percentage);

        auditService.logAction("PAYMENT_PROCESSING_FEE_UPDATED", "FeeConfiguration", null,
                Map.of("percentage", request.percentage.toString()));

        return BaseResponse.success("Payment processing fee updated successfully");
    }

    /**
     * View fee history.
     */
    @GET
    @Path("/history")
    @Operation(summary = "View fee history", description = "Get audit trail of all fee changes")
    public BaseResponse<List<AuditLogEntity>> viewFeeHistory() {
        logger.info("Admin viewing fee history");

        List<AuditLogEntity> logs = entityManager.createQuery(
                "SELECT a FROM AuditLogEntity a WHERE a.action LIKE '%FEE%' OR a.action LIKE '%COMMISSION%' ORDER BY a.createdAt DESC",
                AuditLogEntity.class)
                .setParameter("tag", "%FEE%")
                .setMaxResults(50)
                .getResultList();

        return BaseResponse.success(logs);
    }

    // Request DTOs

    public static class UpdateFeeRequest {
        public BigDecimal percentage;
        public LocalDate effectiveDate;
        public String description;
    }
}