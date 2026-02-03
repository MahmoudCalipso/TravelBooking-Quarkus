package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.request.booking.CreateBookingRequest;
import com.travelplatform.application.dto.response.booking.BookingResponse;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.service.booking.BookingService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
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
 * REST controller for booking operations.
 * Handles booking creation, management, and payment processing.
 */
@Path("/api/v1/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Bookings", description = "Booking management and payment processing")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @Inject
    private BookingService bookingService;

    /**
     * Create new booking.
     *
     * @param securityContext The security context
     * @param request The booking creation request
     * @return Created booking response
     */
    @POST
    @Authenticated
    @Operation(summary = "Create booking", description = "Create a new booking")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Booking created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid input"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response createBooking(@Context SecurityContext securityContext, @Valid CreateBookingRequest request) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Create booking request by user: {}", userId);
            
            BookingResponse booking = bookingService.createBooking(UUID.fromString(userId), request);
            
            return Response.status(Response.Status.CREATED)
                    .entity(new SuccessResponse<>(booking, "Booking created successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Booking creation failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error creating booking", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get user's bookings.
     *
     * @param securityContext The security context
     * @param status The booking status filter
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of bookings
     */
    @GET
    @Authenticated
    @Operation(summary = "Get user's bookings", description = "Get current user's bookings")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Bookings retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getUserBookings(
            @Context SecurityContext securityContext,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get bookings request for user: {}", userId);
            
            PageResponse<BookingResponse> bookings = bookingService.getBookingsByUser(
                    UUID.fromString(userId), status, page, pageSize);
            
            return Response.ok()
                    .entity(bookings)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting bookings", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get booking by ID.
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @return Booking response
     */
    @GET
    @Path("/{bookingId}")
    @Authenticated
    @Operation(summary = "Get booking by ID", description = "Get booking details by ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Booking retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response getBookingById(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get booking by ID request: {} by user: {}", bookingId, userId);
            
            BookingResponse booking = bookingService.getBookingById(UUID.fromString(userId), bookingId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(booking, "Booking retrieved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Booking not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("BOOKING_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting booking", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Cancel booking.
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @param reason The cancellation reason
     * @return Success response
     */
    @PUT
    @Path("/{bookingId}/cancel")
    @Authenticated
    @Operation(summary = "Cancel booking", description = "Cancel a booking")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Booking cancelled successfully"),
        @APIResponse(responseCode = "400", description = "Cannot cancel booking"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response cancelBooking(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId,
            @FormParam("reason") String reason) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Cancel booking request: {} by user: {}", bookingId, userId);
            
            bookingService.cancelBooking(UUID.fromString(userId), bookingId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Booking cancelled successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Booking cancellation failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("CANCELLATION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error cancelling booking", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Complete booking.
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @return Success response
     */
    @POST
    @Path("/{bookingId}/complete")
    @Authenticated
    @RolesAllowed({"TRAVELER", "SUPPLIER_SUBSCRIBER"})
    @Operation(summary = "Complete booking", description = "Mark a booking as completed")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Booking completed successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response completeBooking(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Complete booking request: {} by user: {}", bookingId, userId);
            
            bookingService.completeBooking(UUID.fromString(userId), bookingId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Booking completed successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Booking completion failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("COMPLETION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error completing booking", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Process payment for booking.
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @param paymentMethod The payment method
     * @param paymentProvider The payment provider
     * @param transactionId The transaction ID
     * @return Success response
     */
    @POST
    @Path("/{bookingId}/payment")
    @Authenticated
    @Operation(summary = "Process payment", description = "Process payment for booking")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Payment processed successfully"),
        @APIResponse(responseCode = "400", description = "Payment failed"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response processPayment(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId,
            @FormParam("paymentMethod") String paymentMethod,
            @FormParam("paymentProvider") String paymentProvider,
            @FormParam("transactionId") String transactionId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Process payment request for booking: {} by user: {}", bookingId, userId);
            
            bookingService.processPayment(UUID.fromString(userId), bookingId, paymentMethod, paymentProvider, transactionId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Payment processed successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("PAYMENT_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error processing payment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get payment status.
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @return Payment status response
     */
    @GET
    @Path("/{bookingId}/payment/status")
    @Authenticated
    @Operation(summary = "Get payment status", description = "Get payment status for booking")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Payment status retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response getPaymentStatus(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get payment status request for booking: {} by user: {}", bookingId, userId);
            
            String paymentStatus = bookingService.getPaymentStatus(UUID.fromString(userId), bookingId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(paymentStatus, "Payment status retrieved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Payment status retrieval failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("BOOKING_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting payment status", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Request refund.
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @param reason The refund reason
     * @return Success response
     */
    @POST
    @Path("/{bookingId}/refund")
    @Authenticated
    @Operation(summary = "Request refund", description = "Request refund for booking")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Refund requested successfully"),
        @APIResponse(responseCode = "400", description = "Refund not allowed"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response requestRefund(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId,
            @FormParam("reason") String reason) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Request refund for booking: {} by user: {}", bookingId, userId);
            
            bookingService.refundBooking(UUID.fromString(userId), bookingId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Refund requested successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Refund request failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("REFUND_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error requesting refund", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get supplier's bookings.
     *
     * @param securityContext The security context
     * @param status The booking status filter
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of bookings
     */
    @GET
    @Path("/supplier/bookings")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Get supplier's bookings", description = "Get bookings for supplier's accommodations")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Bookings retrieved successfully"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getSupplierBookings(
            @Context SecurityContext securityContext,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Get supplier bookings request for supplier: {}", supplierId);
            
            PageResponse<BookingResponse> bookings = bookingService.getBookingsBySupplier(
                    UUID.fromString(supplierId), status, page, pageSize);
            
            return Response.ok()
                    .entity(bookings)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting supplier bookings", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Confirm booking (supplier only).
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @return Success response
     */
    @PUT
    @Path("/supplier/bookings/{bookingId}/confirm")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Confirm booking", description = "Confirm a booking (supplier only)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Booking confirmed successfully"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response confirmBooking(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Confirm booking request: {} by supplier: {}", bookingId, supplierId);
            
            bookingService.confirmBooking(UUID.fromString(supplierId), bookingId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Booking confirmed successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Booking confirmation failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("CONFIRMATION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error confirming booking", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Reject booking (supplier only).
     *
     * @param securityContext The security context
     * @param bookingId The booking ID
     * @param reason The rejection reason
     * @return Success response
     */
    @PUT
    @Path("/supplier/bookings/{bookingId}/reject")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Reject booking", description = "Reject a booking (supplier only)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Booking rejected successfully"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Booking not found")
    })
    public Response rejectBooking(
            @Context SecurityContext securityContext,
            @PathParam("bookingId") UUID bookingId,
            @FormParam("reason") String reason) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Reject booking request: {} by supplier: {}", bookingId, supplierId);
            
            bookingService.rejectBooking(UUID.fromString(supplierId), bookingId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Booking rejected successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Booking rejection failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("REJECTION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error rejecting booking", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get supplier booking statistics.
     *
     * @param securityContext The security context
     * @return Statistics response
     */
    @GET
    @Path("/supplier/statistics")
    @Authenticated
    @RolesAllowed("SUPPLIER_SUBSCRIBER")
    @Operation(summary = "Get supplier statistics", description = "Get booking statistics for supplier")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getSupplierStatistics(@Context SecurityContext securityContext) {
        try {
            String supplierId = securityContext.getUserPrincipal().getName();
            log.info("Get supplier statistics request for supplier: {}", supplierId);
            
            var statistics = bookingService.getSupplierBookingStatistics(UUID.fromString(supplierId));
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting supplier statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
