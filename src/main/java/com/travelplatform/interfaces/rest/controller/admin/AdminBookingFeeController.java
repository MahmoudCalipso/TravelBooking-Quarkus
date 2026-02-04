package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.request.booking.UpdateBookingFeeConfigRequest;
import com.travelplatform.application.dto.response.booking.BookingFeeConfigResponse;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.service.booking.BookingFeeConfigService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * REST controller for booking fee configuration (admin only).
 */
@Path("/api/v1/admin/booking-fees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin Booking Fees", description = "Booking fee configuration management")
@RolesAllowed("SUPER_ADMIN")
public class AdminBookingFeeController {

    private static final Logger log = LoggerFactory.getLogger(AdminBookingFeeController.class);

    @Inject
    BookingFeeConfigService bookingFeeConfigService;

    @GET
    @Authenticated
    @Operation(summary = "Get booking fee configuration", description = "Get current booking fee configuration")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Configuration retrieved successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getBookingFeeConfig() {
        try {
            BookingFeeConfigResponse config = bookingFeeConfigService.getActiveConfigResponse();
            return Response.ok(new SuccessResponse<>(config, "Booking fee configuration retrieved successfully"))
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving booking fee configuration", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    @PUT
    @Authenticated
    @Operation(summary = "Update booking fee configuration", description = "Update booking fee configuration")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Configuration updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response updateBookingFeeConfig(
            @Context SecurityContext securityContext,
            @Valid UpdateBookingFeeConfigRequest request) {
        try {
            UUID adminId = UUID.fromString(securityContext.getUserPrincipal().getName());
            BookingFeeConfigResponse config = bookingFeeConfigService.updateConfig(adminId, request);
            return Response.ok(new SuccessResponse<>(config, "Booking fee configuration updated successfully"))
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid booking fee configuration: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error updating booking fee configuration", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
