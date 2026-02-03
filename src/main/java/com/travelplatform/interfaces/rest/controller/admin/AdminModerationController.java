package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.dto.response.user.UserResponse;
import com.travelplatform.application.service.admin.AdminModerationService;
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
 * REST controller for admin moderation operations.
 * Handles content reports and moderation actions.
 */
@Path("/api/v1/admin/moderation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin Moderation", description = "Admin content moderation")
@RolesAllowed("SUPER_ADMIN")
public class AdminModerationController {

    private static final Logger log = LoggerFactory.getLogger(AdminModerationController.class);

    @Inject
    private AdminModerationService adminModerationService;

    /**
     * Get reported reels.
     *
     * @param status The report status filter
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of reported reels
     */
    @GET
    @Path("/reports/reels")
    @Authenticated
    @Operation(summary = "Get reported reels", description = "Get reels that have been reported")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Reported reels retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getReportedReels(
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get reported reels request");
            
            PageResponse<ReelResponse> reels = 
                    adminModerationService.getReportedReels(status, page, pageSize);
            
            return Response.ok()
                    .entity(reels)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting reported reels", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get reported reviews.
     *
     * @param status The report status filter
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of reported reviews
     */
    @GET
    @Path("/reports/reviews")
    @Authenticated
    @Operation(summary = "Get reported reviews", description = "Get reviews that have been reported")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Reported reviews retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getReportedReviews(
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get reported reviews request");
            
            PageResponse<ReviewResponse> reviews = 
                    adminModerationService.getReportedReviews(status, page, pageSize);
            
            return Response.ok()
                    .entity(reviews)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting reported reviews", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get reported users.
     *
     * @param status The report status filter
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of reported users
     */
    @GET
    @Path("/reports/users")
    @Authenticated
    @Operation(summary = "Get reported users", description = "Get users that have been reported")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Reported users retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getReportedUsers(
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get reported users request");
            
            PageResponse<UserResponse> users = 
                    adminModerationService.getReportedUsers(status, page, pageSize);
            
            return Response.ok()
                    .entity(users)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting reported users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get all reports.
     *
     * @param page The page number
     * @param pageSize The page size
     * @return Paginated list of all reports
     */
    @GET
    @Path("/reports")
    @Authenticated
    @Operation(summary = "Get all reports", description = "Get all content reports")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Reports retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getAllReports(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get all reports request");
            
            var reports = adminModerationService.getAllReports(page, pageSize);
            
            return Response.ok()
                    .entity(reports)
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting all reports", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get report details.
     *
     * @param reportId The report ID
     * @return Report details
     */
    @GET
    @Path("/reports/{reportId}")
    @Authenticated
    @Operation(summary = "Get report details", description = "Get details of a specific report")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Report details retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response getReportDetails(@PathParam("reportId") UUID reportId) {
        try {
            log.info("Get report details request: {}", reportId);
            
            var report = adminModerationService.getReportDetails(reportId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(report, "Report details retrieved successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Report not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("REPORT_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting report details", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Review report.
     *
     * @param reportId The report ID
     * @param action The action taken (DISMISSED, ACTION_TAKEN)
     * @param adminNotes The admin notes
     * @return Success response
     */
    @PUT
    @Path("/reports/{reportId}/review")
    @Authenticated
    @Operation(summary = "Review report", description = "Review and take action on a report")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Report reviewed successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response reviewReport(
            @PathParam("reportId") UUID reportId,
            @FormParam("action") String action,
            @FormParam("adminNotes") String adminNotes) {
        try {
            log.info("Review report request: {}", reportId);
            
            adminModerationService.reviewReport(reportId, action, adminNotes);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Report reviewed successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Report review failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("REVIEW_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error reviewing report", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Dismiss report.
     *
     * @param reportId The report ID
     * @return Success response
     */
    @PUT
    @Path("/reports/{reportId}/dismiss")
    @Authenticated
    @Operation(summary = "Dismiss report", description = "Dismiss a report")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Report dismissed successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response dismissReport(@PathParam("reportId") UUID reportId) {
        try {
            log.info("Dismiss report request: {}", reportId);
            
            adminModerationService.dismissReport(reportId);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Report dismissed successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Report dismissal failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("REPORT_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error dismissing report", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Take action on report.
     *
     * @param reportId The report ID
     * @param action The action to take (FLAG_CONTENT, REMOVE_CONTENT, SUSPEND_USER)
     * @param reason The reason for action
     * @return Success response
     */
    @PUT
    @Path("/reports/{reportId}/action")
    @Authenticated
    @Operation(summary = "Take action on report", description = "Take moderation action on reported content")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Action taken successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
        @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response takeActionOnReport(
            @PathParam("reportId") UUID reportId,
            @FormParam("action") String action,
            @FormParam("reason") String reason) {
        try {
            log.info("Take action on report request: {}", reportId);
            
            adminModerationService.takeActionOnReport(reportId, action, reason);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Action taken successfully"))
                    .build();
                    
        } catch (IllegalArgumentException e) {
            log.error("Action on report failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("ACTION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error taking action on report", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get moderation statistics.
     *
     * @return Statistics response
     */
    @GET
    @Path("/statistics")
    @Authenticated
    @Operation(summary = "Get moderation statistics", description = "Get moderation statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getModerationStatistics() {
        try {
            log.info("Get moderation statistics request");
            
            Map<String, Long> statistics = adminModerationService.getModerationStatistics();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting moderation statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
