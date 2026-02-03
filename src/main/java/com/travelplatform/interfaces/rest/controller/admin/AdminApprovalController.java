package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.accommodation.AccommodationResponse;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.event.EventResponse;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.service.admin.AdminApprovalService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for admin approval operations.
 * Handles content approval workflow for accommodations, reels, events, and reviews.
 */
@Path("/api/v1/admin/approvals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin Approvals", description = "Admin content approval management")
@RolesAllowed("SUPER_ADMIN")
public class AdminApprovalController {

    private static final Logger log = LoggerFactory.getLogger(AdminApprovalController.class);

    @Inject
    private AdminApprovalService adminApprovalService;

    /**
     * Get pending accommodations.
     *
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of pending accommodations
     */
    @GET
    @Path("/accommodations/pending")
    @Authenticated
    @Operation(summary = "Get pending accommodations", description = "Get accommodations awaiting approval")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Pending accommodations retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getPendingAccommodations(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get pending accommodations request");
            
            PageResponse<AccommodationResponse> accommodations = 
                    adminApprovalService.getPendingAccommodations(page, pageSize);
            
            return Response.ok()
                    .entity(accommodations)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting pending accommodations", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Approve accommodation.
     *
     * @param accommodationId The accommodation ID
     * @return Success response
     */
    @PUT
    @Path("/accommodations/{accommodationId}/approve")
    @Authenticated
    @Operation(summary = "Approve accommodation", description = "Approve an accommodation")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Accommodation approved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response approveAccommodation(@PathParam("accommodationId") UUID accommodationId) {
        try {
            log.info("Approve accommodation request: {}", accommodationId);
            
            adminApprovalService.approveAccommodation(accommodationId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Accommodation approved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Accommodation approval failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("ACCOMMODATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error approving accommodation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Reject accommodation.
     *
     * @param accommodationId The accommodation ID
     * @param reason The rejection reason
     * @return Success response
     */
    @PUT
    @Path("/accommodations/{accommodationId}/reject")
    @Authenticated
    @Operation(summary = "Reject accommodation", description = "Reject an accommodation")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Accommodation rejected successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Accommodation not found")
    })
    public Response rejectAccommodation(
            @PathParam("accommodationId") UUID accommodationId,
            @FormParam("reason") String reason) {
        try {
            log.info("Reject accommodation request: {}", accommodationId);
            
            adminApprovalService.rejectAccommodation(accommodationId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Accommodation rejected successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Accommodation rejection failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("ACCOMMODATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error rejecting accommodation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get pending reels.
     *
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of pending reels
     */
    @GET
    @Path("/reels/pending")
    @Authenticated
    @Operation(summary = "Get pending reels", description = "Get reels awaiting approval")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Pending reels retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getPendingReels(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get pending reels request");
            
            PageResponse<ReelResponse> reels = 
                    adminApprovalService.getPendingReels(page, pageSize);
            
            return Response.ok()
                    .entity(reels)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting pending reels", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Approve reel.
     *
     * @param reelId The reel ID
     * @return Success response
     */
    @PUT
    @Path("/reels/{reelId}/approve")
    @Authenticated
    @Operation(summary = "Approve reel", description = "Approve a reel")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Reel approved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Reel not found")
    })
    public Response approveReel(@PathParam("reelId") UUID reelId) {
        try {
            log.info("Approve reel request: {}", reelId);
            
            adminApprovalService.approveReel(reelId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Reel approved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Reel approval failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("REEL_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error approving reel", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Reject reel.
     *
     * @param reelId The reel ID
     * @param reason The rejection reason
     * @return Success response
     */
    @PUT
    @Path("/reels/{reelId}/reject")
    @Authenticated
    @Operation(summary = "Reject reel", description = "Reject a reel")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Reel rejected successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Reel not found")
    })
    public Response rejectReel(
            @PathParam("reelId") UUID reelId,
            @FormParam("reason") String reason) {
        try {
            log.info("Reject reel request: {}", reelId);
            
            adminApprovalService.rejectReel(reelId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Reel rejected successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Reel rejection failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("REEL_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error rejecting reel", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get pending events.
     *
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of pending events
     */
    @GET
    @Path("/events/pending")
    @Authenticated
    @Operation(summary = "Get pending events", description = "Get events awaiting approval")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Pending events retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getPendingEvents(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get pending events request");
            
            PageResponse<EventResponse> events = 
                    adminApprovalService.getPendingEvents(page, pageSize);
            
            return Response.ok()
                    .entity(events)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting pending events", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Approve event.
     *
     * @param eventId The event ID
     * @return Success response
     */
    @PUT
    @Path("/events/{eventId}/approve")
    @Authenticated
    @Operation(summary = "Approve event", description = "Approve an event")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Event approved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Event not found")
    })
    public Response approveEvent(@PathParam("eventId") UUID eventId) {
        try {
            log.info("Approve event request: {}", eventId);
            
            adminApprovalService.approveEvent(eventId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Event approved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Event approval failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("EVENT_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error approving event", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Reject event.
     *
     * @param eventId The event ID
     * @param reason The rejection reason
     * @return Success response
     */
    @PUT
    @Path("/events/{eventId}/reject")
    @Authenticated
    @Operation(summary = "Reject event", description = "Reject an event")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Event rejected successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Event not found")
    })
    public Response rejectEvent(
            @PathParam("eventId") UUID eventId,
            @FormParam("reason") String reason) {
        try {
            log.info("Reject event request: {}", eventId);
            
            adminApprovalService.rejectEvent(eventId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Event rejected successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Event rejection failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("EVENT_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error rejecting event", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get pending reviews.
     *
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of pending reviews
     */
    @GET
    @Path("/reviews/pending")
    @Authenticated
    @Operation(summary = "Get pending reviews", description = "Get reviews awaiting approval")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Pending reviews retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getPendingReviews(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get pending reviews request");
            
            PageResponse<ReviewResponse> reviews = 
                    adminApprovalService.getPendingReviews(page, pageSize);
            
            return Response.ok()
                    .entity(reviews)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting pending reviews", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Approve review.
     *
     * @param reviewId The review ID
     * @return Success response
     */
    @PUT
    @Path("/reviews/{reviewId}/approve")
    @Authenticated
    @Operation(summary = "Approve review", description = "Approve a review")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Review approved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Review not found")
    })
    public Response approveReview(@PathParam("reviewId") UUID reviewId) {
        try {
            log.info("Approve review request: {}", reviewId);
            
            adminApprovalService.approveReview(reviewId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Review approved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Review approval failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("REVIEW_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error approving review", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Reject review.
     *
     * @param reviewId The review ID
     * @param reason The rejection reason
     * @return Success response
     */
    @PUT
    @Path("/reviews/{reviewId}/reject")
    @Authenticated
    @Operation(summary = "Reject review", description = "Reject a review")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Review rejected successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Review not found")
    })
    public Response rejectReview(
            @PathParam("reviewId") UUID reviewId,
            @FormParam("reason") String reason) {
        try {
            log.info("Reject review request: {}", reviewId);
            
            adminApprovalService.rejectReview(reviewId, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Review rejected successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Review rejection failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("REVIEW_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error rejecting review", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get approval queue statistics.
     *
     * @return Statistics response
     */
    @GET
    @Path("/statistics")
    @Authenticated
    @Operation(summary = "Get approval queue statistics", description = "Get statistics for approval queues")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getApprovalQueueStatistics() {
        try {
            log.info("Get approval queue statistics request");
            
            Map<String, Long> statistics = adminApprovalService.getApprovalQueueStatistics();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting approval queue statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
