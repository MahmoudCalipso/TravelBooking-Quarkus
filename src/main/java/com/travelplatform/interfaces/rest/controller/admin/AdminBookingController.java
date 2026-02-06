package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.dto.response.booking.BookingResponse;
import com.travelplatform.application.service.admin.AdminBookingService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Admin controller for booking management and oversight.
 */
@Path("/api/v1/admin/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Booking Management", description = "SUPER_ADMIN endpoints for managing bookings")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminBookingController {

    private static final Logger logger = LoggerFactory.getLogger(AdminBookingController.class);

    @Inject
    AdminBookingService adminBookingService;

    @GET
    @Operation(summary = "List all bookings", description = "Get all bookings with filters")
    public PaginatedResponse<BookingResponse> listBookings(
            @QueryParam("status") String status,
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing bookings: status={}, page={}", status, page);

        return adminBookingService.listBookings(status, startDate, endDate, page, size);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get booking details", description = "View detailed booking information")
    public BaseResponse<BookingResponse> getBooking(@PathParam("id") UUID bookingId) {
        logger.info("Admin viewing booking: bookingId={}", bookingId);

        return BaseResponse.success(adminBookingService.getBooking(bookingId));
    }

    @POST
    @Path("/{id}/cancel")
    @Transactional
    @Operation(summary = "Cancel booking", description = "Admin cancellation of booking")
    public BaseResponse<Void> cancelBooking(@PathParam("id") UUID bookingId, CancelRequest request) {
        logger.info("Admin cancelling booking: bookingId={}, reason={}", bookingId, request.reason);

        adminBookingService.cancelBooking(bookingId, null, request.reason);

        return BaseResponse.success("Booking cancelled successfully");
    }

    public static class CancelRequest {
        public String reason;
    }
}
