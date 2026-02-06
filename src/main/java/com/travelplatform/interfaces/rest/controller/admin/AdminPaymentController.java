package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.request.payment.AdminRefundRequest;
import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.application.service.admin.AdminPaymentService;
import com.travelplatform.application.dto.response.payment.AdminPaymentResponse;
import com.travelplatform.application.dto.response.payment.AdminPaymentStatsResponse;
import com.travelplatform.application.dto.response.payment.PaymentDisputeResponse;
import com.travelplatform.application.dto.response.payment.PaymentWebhookLogResponse;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Admin controller for payment management.
 * All endpoints require SUPER_ADMIN role.
 */
@Path("/api/v1/admin/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Payment Management", description = "SUPER_ADMIN endpoints for monitoring and managing payments")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPaymentController.class);

    @Inject
    AdminPaymentService adminPaymentService;

    @Inject
    AuditService auditService;

    /**
     * List all payments with filters.
     */
    @GET
    @Operation(summary = "List all payments", description = "Get all payment transactions with filters")
    public PaginatedResponse<AdminPaymentResponse> listPayments(
            @QueryParam("status") PaymentStatus status,
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate,
            @QueryParam("supplierId") UUID supplierId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing payments: status={}, page={}, size={}", status, page, size);

        return adminPaymentService.listPayments(status, supplierId, startDate, endDate, page, size);
    }

    /**
     * Get payment statistics.
     */
    @GET
    @Path("/stats")
    @Operation(summary = "Payment statistics", description = "Get platform revenue and payment analytics")
    public BaseResponse<AdminPaymentStatsResponse> getPaymentStats(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {

        logger.info("Admin viewing payment stats: startDate={}, endDate={}", startDate, endDate);

        AdminPaymentStatsResponse stats = adminPaymentService.stats(
                new AdminPaymentService.LocalDateRange(startDate, endDate));
        return BaseResponse.success(stats);
    }

    /**
     * Process refund.
     */
    @POST
    @Path("/{id}/refund")
    @Transactional
    @Operation(summary = "Process refund", description = "Manually issue refund to customer")
    public BaseResponse<Void> processRefund(@PathParam("id") UUID paymentId, AdminRefundRequest request) {
        logger.info("Admin processing refund: paymentId={}, reason={}", paymentId, request.reason);

        adminPaymentService.processRefund(paymentId, request.amount, request.reason);
        auditService.logAction("PAYMENT_REFUND_REQUESTED", "BookingPayment", paymentId,
                java.util.Map.of("reason", request.reason, "amount", request.amount));

        return new BaseResponse<>(true, "Refund processed", null);
    }

    /**
     * View Stripe webhook logs.
     */
    @GET
    @Path("/webhooks")
    @Operation(summary = "View webhook logs", description = "Get Stripe webhook event logs")
    public BaseResponse<List<PaymentWebhookLogResponse>> getWebhookLogs(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing webhook logs");

        return BaseResponse.success(List.copyOf(adminPaymentService.getWebhookLogs(page, size)));
    }

    /**
     * View disputes and chargebacks.
     */
    @GET
    @Path("/disputes")
    @Operation(summary = "View disputes", description = "Get payment disputes and chargebacks")
    public BaseResponse<List<PaymentDisputeResponse>> getDisputes(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        logger.info("Admin viewing payment disputes");

        return BaseResponse.success(List.copyOf(adminPaymentService.getDisputes(page, size)));
    }

}
