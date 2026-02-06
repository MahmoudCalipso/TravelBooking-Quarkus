package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.admin.AdminSupplierToolsService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin supplier tooling endpoints.
 * All endpoints are SUPER_ADMIN only.
 */
@Path("/api/v1/admin/supplier-tools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Supplier Tools", description = "SUPER_ADMIN tools for supplier verification and payouts")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminSupplierToolsController {

    private static final Logger logger = LoggerFactory.getLogger(AdminSupplierToolsController.class);

    @Inject
    AdminSupplierToolsService supplierToolsService;

    @GET
    @Path("/verification-queue")
    @Operation(summary = "View supplier verification queue", description = "List suppliers pending verification review")
    public PaginatedResponse<Map<String, Object>> verificationQueue(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing supplier verification queue page={} size={}", page, size);
        List<Map<String, Object>> items = supplierToolsService.getVerificationQueue(page, size);
        return PaginatedResponse.of(items, items.size(), page, size);
    }

    @POST
    @Path("/verification/{supplierId}/approve")
    @Operation(summary = "Approve supplier verification", description = "Approve supplier documents and mark verified")
    public BaseResponse<Void> approveSupplier(@PathParam("supplierId") UUID supplierId) {
        logger.info("Admin approving supplier verification supplierId={}", supplierId);
        supplierToolsService.approveSupplier(supplierId);
        return new BaseResponse<>(true, "Supplier verification approved", null);
    }

    @POST
    @Path("/verification/{supplierId}/reject")
    @Operation(summary = "Reject supplier verification", description = "Reject supplier verification with reason")
    public BaseResponse<Void> rejectSupplier(@PathParam("supplierId") UUID supplierId, RejectRequest request) {
        logger.info("Admin rejecting supplier verification supplierId={} reason={}", supplierId, request != null ? request.reason : null);
        supplierToolsService.rejectSupplier(supplierId, request != null ? request.reason : null);
        return new BaseResponse<>(true, "Supplier verification rejected", null);
    }

    @GET
    @Path("/suppliers/{supplierId}/payout-accounts")
    @Operation(summary = "View supplier payout accounts", description = "List payout destinations for a supplier")
    public BaseResponse<List<Map<String, Object>>> getPayoutAccounts(@PathParam("supplierId") UUID supplierId) {
        logger.info("Admin viewing payout accounts for supplierId={}", supplierId);
        return BaseResponse.success(supplierToolsService.getPayoutAccounts(supplierId));
    }

    @POST
    @Path("/suppliers/{supplierId}/payouts")
    @Operation(summary = "Process manual payout", description = "Initiate a manual payout to a supplier")
    public BaseResponse<Void> processManualPayout(@PathParam("supplierId") UUID supplierId, ManualPayoutRequest request) {
        logger.info("Admin processing manual payout supplierId={} amount={}", supplierId, request != null ? request.amount : null);
        if (request != null) {
            supplierToolsService.processManualPayout(supplierId, request.amount, request.reference, request.note);
        }
        return new BaseResponse<>(true, "Manual payout initiated", null);
    }

    @POST
    @Path("/suppliers/{supplierId}/suspend")
    @Operation(summary = "Suspend supplier account", description = "Temporarily suspend a supplier account with reason")
    public BaseResponse<Void> suspendSupplier(@PathParam("supplierId") UUID supplierId, SuspensionRequest request) {
        logger.info("Admin suspending supplierId={} reason={} durationDays={}", supplierId,
                request != null ? request.reason : null, request != null ? request.durationDays : null);
        if (request != null) {
            supplierToolsService.suspendSupplier(supplierId, request.reason, request.durationDays, request.suspensionEnd);
        }
        return new BaseResponse<>(true, "Supplier suspended", null);
    }

    @DELETE
    @Path("/suppliers/{supplierId}")
    @Operation(summary = "Remove supplier", description = "Soft remove supplier account while preserving history")
    public BaseResponse<Void> removeSupplier(@PathParam("supplierId") UUID supplierId, RemovalRequest request) {
        logger.info("Admin removing supplierId={} reason={}", supplierId, request != null ? request.reason : null);
        supplierToolsService.removeSupplier(supplierId, request != null ? request.reason : null);
        return new BaseResponse<>(true, "Supplier removed", null);
    }

    @GET
    @Path("/suppliers/performance-issues")
    @Operation(summary = "View supplier performance issues", description = "List suppliers with performance or complaint issues")
    public PaginatedResponse<Map<String, Object>> performanceIssues(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing supplier performance issues page={} size={}", page, size);
        List<Map<String, Object>> items = supplierToolsService.getPerformanceIssues(page, size);
        return PaginatedResponse.of(items, items.size(), page, size);
    }

    public static class RejectRequest {
        @NotBlank
        public String reason;
    }

    public static class ManualPayoutRequest {
        @NotNull
        public BigDecimal amount;
        public String reference;
        public String note;
    }

    public static class SuspensionRequest {
        @NotBlank
        public String reason;
        public Integer durationDays;
        public Instant suspensionEnd;
    }

    public static class RemovalRequest {
        @NotBlank
        public String reason;
    }
}
