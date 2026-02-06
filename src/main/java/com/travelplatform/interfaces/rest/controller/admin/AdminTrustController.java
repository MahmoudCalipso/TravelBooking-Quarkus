package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.admin.AdminTrustService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin trust, fraud, and verification endpoints.
 */
@Path("/api/v1/admin/trust")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Trust & Safety", description = "SUPER_ADMIN endpoints for fraud detection and identity verification")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminTrustController {

    private static final Logger logger = LoggerFactory.getLogger(AdminTrustController.class);

    @Inject
    AdminTrustService trustService;

    @GET
    @Path("/fraud/dashboard")
    @Operation(summary = "Fraud detection dashboard", description = "High-level fraud and trust metrics")
    public BaseResponse<Map<String, Object>> fraudDashboard() {
        logger.info("Admin viewing fraud detection dashboard");
        return BaseResponse.success(trustService.fraudDashboard());
    }

    @GET
    @Path("/fraud/users")
    @Operation(summary = "Suspicious user activity", description = "List suspicious user activities and risk signals")
    public PaginatedResponse<Map<String, Object>> suspiciousUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing suspicious users page={} size={}", page, size);
        List<Map<String, Object>> items = trustService.suspiciousUsers(page, size);
        return PaginatedResponse.of(items, items.size(), page, size);
    }

    @GET
    @Path("/fraud/bookings")
    @Operation(summary = "Suspicious bookings", description = "List high-risk bookings for review")
    public PaginatedResponse<Map<String, Object>> suspiciousBookings(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing suspicious bookings page={} size={}", page, size);
        List<Map<String, Object>> items = trustService.suspiciousBookings(page, size);
        return PaginatedResponse.of(items, items.size(), page, size);
    }

    @POST
    @Path("/fraud/users/{userId}/block")
    @Operation(summary = "Block user (fraud)", description = "Immediately block a user account for fraud reasons")
    public BaseResponse<Void> blockUser(@PathParam("userId") UUID userId, ReasonRequest request) {
        logger.info("Admin blocking userId={} reason={}", userId, request != null ? request.reason : null);
        trustService.blockUser(userId, request != null ? request.reason : null);
        return new BaseResponse<>(true, "User blocked for fraud", null);
    }

    @POST
    @Path("/fraud/payments/{paymentId}/flag")
    @Operation(summary = "Flag suspicious payment", description = "Flag a payment for manual review")
    public BaseResponse<Void> flagPayment(@PathParam("paymentId") UUID paymentId, ReasonRequest request) {
        logger.info("Admin flagging paymentId={} reason={}", paymentId, request != null ? request.reason : null);
        trustService.flagPayment(paymentId, request != null ? request.reason : null);
        return new BaseResponse<>(true, "Payment flagged for review", null);
    }

    @GET
    @Path("/identity/requests")
    @Operation(summary = "Identity verification requests", description = "Queue of identity verification submissions")
    public PaginatedResponse<Map<String, Object>> identityRequests(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing identity verification requests page={} size={}", page, size);
        List<Map<String, Object>> items = trustService.identityRequests(page, size);
        return PaginatedResponse.of(items, items.size(), page, size);
    }

    @POST
    @Path("/identity/{requestId}/approve")
    @Operation(summary = "Approve identity verification", description = "Approve an identity verification request")
    public BaseResponse<Void> approveIdentity(@PathParam("requestId") UUID requestId) {
        logger.info("Admin approving identity requestId={}", requestId);
        trustService.approveIdentity(requestId);
        return new BaseResponse<>(true, "Identity verification approved", null);
    }

    @POST
    @Path("/identity/{requestId}/reject")
    @Operation(summary = "Reject identity verification", description = "Reject an identity verification request with reason")
    public BaseResponse<Void> rejectIdentity(@PathParam("requestId") UUID requestId, ReasonRequest request) {
        logger.info("Admin rejecting identity requestId={} reason={}", requestId, request != null ? request.reason : null);
        trustService.rejectIdentity(requestId, request != null ? request.reason : null);
        return new BaseResponse<>(true, "Identity verification rejected", null);
    }

    @GET
    @Path("/disputes")
    @Operation(summary = "Monitor payment disputes", description = "List disputes and chargebacks with statuses")
    public PaginatedResponse<Map<String, Object>> disputes(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing disputes page={} size={}", page, size);
        List<Map<String, Object>> items = trustService.disputes(page, size);
        return PaginatedResponse.of(items, items.size(), page, size);
    }

    @POST
    @Path("/alerts")
    @Operation(summary = "Create trust alert", description = "Create and broadcast a trust & safety alert")
    public BaseResponse<Void> createAlert(ReasonRequest request) {
        logger.info("Admin creating trust alert reason={}", request != null ? request.reason : null);
        trustService.createTrustAlert(request != null ? request.reason : null);
        return new BaseResponse<>(true, "Trust alert created", null);
    }

    public static class ReasonRequest {
        @NotBlank
        public String reason;
    }
}
