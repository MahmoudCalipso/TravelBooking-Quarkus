package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.service.admin.AdminAnalyticsService;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for admin analytics operations.
 * Handles platform analytics and reporting.
 */
@Path("/api/v1/admin/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin Analytics", description = "Admin analytics and reporting")
@RolesAllowed("SUPER_ADMIN")
public class AdminAnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AdminAnalyticsController.class);

    @Inject
    private AdminAnalyticsService adminAnalyticsService;

    /**
     * Get platform overview.
     *
     * @return Platform overview statistics
     */
    @GET
    @Path("/overview")
    @Authenticated
    @Operation(summary = "Get platform overview", description = "Get platform overview statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Overview retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getPlatformOverview() {
        try {
            log.info("Get platform overview request");
            
            AdminAnalyticsService.PlatformOverview overview = adminAnalyticsService.getPlatformOverview();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(overview, "Overview retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting platform overview", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get accommodation statistics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Accommodation statistics
     */
    @GET
    @Path("/accommodations")
    @Authenticated
    @Operation(summary = "Get accommodation statistics", description = "Get accommodation statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getAccommodationStatistics(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        try {
            log.info("Get accommodation statistics request");
            
            AdminAnalyticsService.AccommodationAnalytics statistics =
                    adminAnalyticsService.getAccommodationAnalytics();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting accommodation statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get reel statistics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Reel statistics
     */
    @GET
    @Path("/reels")
    @Authenticated
    @Operation(summary = "Get reel statistics", description = "Get reel statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getReelStatistics(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        try {
            log.info("Get reel statistics request");
            
            AdminAnalyticsService.ReelAnalytics statistics =
                    adminAnalyticsService.getReelAnalytics();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting reel statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get booking statistics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Booking statistics
     */
    @GET
    @Path("/bookings")
    @Authenticated
    @Operation(summary = "Get booking statistics", description = "Get booking statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getBookingStatistics(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        try {
            log.info("Get booking statistics request");
            
            AdminAnalyticsService.BookingAnalytics statistics =
                    adminAnalyticsService.getBookingAnalytics();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting booking statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get revenue reports.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Revenue reports
     */
    @GET
    @Path("/revenue")
    @Authenticated
    @Operation(summary = "Get revenue reports", description = "Get revenue reports")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Revenue reports retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getRevenueReports(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        try {
            log.info("Get revenue reports request");
            
            LocalDate resolvedStart = startDate != null ? startDate : LocalDate.now().minusDays(30);
            LocalDate resolvedEnd = endDate != null ? endDate : LocalDate.now();
            AdminAnalyticsService.RevenueAnalytics revenue =
                    adminAnalyticsService.getRevenueAnalytics(resolvedStart, resolvedEnd);
            
            return Response.ok()
                    .entity(new SuccessResponse<>(revenue, "Revenue reports retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting revenue reports", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get user statistics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return User statistics
     */
    @GET
    @Path("/users")
    @Authenticated
    @Operation(summary = "Get user statistics", description = "Get user statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getUserStatistics(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        try {
            log.info("Get user statistics request");
            
            AdminAnalyticsService.UserAnalytics statistics =
                    adminAnalyticsService.getUserAnalytics();
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting user statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get content statistics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Content statistics
     */
    @GET
    @Path("/content")
    @Authenticated
    @Operation(summary = "Get content statistics", description = "Get content statistics")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @APIResponse(responseCode = "401", description = "Not authenticated"),
        @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getContentStatistics(
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate) {
        try {
            log.info("Get content statistics request");
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("reels", adminAnalyticsService.getReelAnalytics());
            statistics.put("reviews", adminAnalyticsService.getReviewAnalytics());
            
            return Response.ok()
                    .entity(new SuccessResponse<>(statistics, "Statistics retrieved successfully"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Unexpected error getting content statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
