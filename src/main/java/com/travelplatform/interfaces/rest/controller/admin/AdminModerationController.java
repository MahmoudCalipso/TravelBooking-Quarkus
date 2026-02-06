package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.dto.response.user.UserResponse;
import com.travelplatform.application.service.admin.AdminAnalyticsService;
import com.travelplatform.application.service.admin.AdminModerationService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.reel.ReelReport;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminModerationController {

    private static final Logger log = LoggerFactory.getLogger(AdminModerationController.class);

    @Inject
    private AdminModerationService adminModerationService;

    /**
     * Get reported reels.
     *
     * @param status   The report status filter
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of reported reels
     */
    @GET
    @Path("/reports/reels")
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

            int pageIndex = toPageIndex(page);
            List<ReelReport> reports;
            if (status != null && !status.isBlank()) {
                ReelReport.ReportStatus reportStatus = ReelReport.ReportStatus.valueOf(status.toUpperCase());
                reports = adminModerationService.getReelReportsByStatus(reportStatus, pageIndex, pageSize);
            } else {
                reports = adminModerationService.getReelReports(pageIndex, pageSize);
            }

            PageResponse<ReelReport> response = toPageResponse(reports, page, pageSize, reports.size());

            return Response.ok().entity(response).build();

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
     * @param status   The report status filter
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of reported reviews
     */
    @GET
    @Path("/reports/reviews")
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

            int pageIndex = toPageIndex(page);
            List<ReviewResponse> reviews = adminModerationService.getFlaggedReviews(pageIndex, pageSize);
            PageResponse<ReviewResponse> response = toPageResponse(reviews, page, pageSize, reviews.size());

            return Response.ok().entity(response).build();

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
     * @param status   The report status filter
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of reported users
     */
    @GET
    @Path("/reports/users")
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

            int pageIndex = toPageIndex(page);
            UserStatus resolvedStatus = UserStatus.SUSPENDED;
            if (status != null && !status.isBlank()) {
                resolvedStatus = UserStatus.valueOf(status.toUpperCase());
            }
            List<UserResponse> users = adminModerationService.getUsersByStatus(resolvedStatus, pageIndex, pageSize);
            PageResponse<UserResponse> response = toPageResponse(users, page, pageSize, users.size());

            return Response.ok().entity(response).build();

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
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of all reports
     */
    @GET
    @Path("/reports")
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

            int pageIndex = toPageIndex(page);
            List<ReelReport> reports = adminModerationService.getReelReports(pageIndex, pageSize);
            PageResponse<ReelReport> response = toPageResponse(reports, page, pageSize, reports.size());

            return Response.ok().entity(response).build();

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

            ReelReport report = adminModerationService.getReelReportDetails(reportId);

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
     * @param reportId   The report ID
     * @param action     The action taken (DISMISSED, ACTION_TAKEN)
     * @param adminNotes The admin notes
     * @return Success response
     */
    @PUT
    @Path("/reports/{reportId}/review")
    @Operation(summary = "Review report", description = "Review and take action on a report")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Report reviewed successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response reviewReport(
            @Context SecurityContext securityContext,
            @PathParam("reportId") UUID reportId,
            @FormParam("action") String action,
            @FormParam("adminNotes") String adminNotes) {
        try {
            log.info("Review report request: {}", reportId);

            UUID adminId = UUID.fromString(securityContext.getUserPrincipal().getName());
            AdminModerationService.ReportAction resolvedAction = resolveReportAction(action);
            adminModerationService.reviewReelReport(adminId, reportId, resolvedAction, adminNotes);

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
    @Operation(summary = "Dismiss report", description = "Dismiss a report")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Report dismissed successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response dismissReport(
            @Context SecurityContext securityContext,
            @PathParam("reportId") UUID reportId) {
        try {
            log.info("Dismiss report request: {}", reportId);

            UUID adminId = UUID.fromString(securityContext.getUserPrincipal().getName());
            adminModerationService.dismissReelReport(adminId, reportId, null);

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
     * @param action   The action to take (FLAG_CONTENT, REMOVE_CONTENT,
     *                 SUSPEND_USER)
     * @param reason   The reason for action
     * @return Success response
     */
    @PUT
    @Path("/reports/{reportId}/action")
    @Operation(summary = "Take action on report", description = "Take moderation action on reported content")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Action taken successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "Report not found")
    })
    public Response takeActionOnReport(
            @Context SecurityContext securityContext,
            @PathParam("reportId") UUID reportId,
            @FormParam("action") String action,
            @FormParam("reason") String reason) {
        try {
            log.info("Take action on report request: {}", reportId);

            UUID adminId = UUID.fromString(securityContext.getUserPrincipal().getName());
            AdminModerationService.ReportAction resolvedAction = resolveReportAction(action);
            adminModerationService.reviewReelReport(adminId, reportId, resolvedAction, reason);

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
    @Operation(summary = "Get moderation statistics", description = "Get moderation statistics")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Response getModerationStatistics() {
        try {
            log.info("Get moderation statistics request");

            AdminModerationService.ModerationSummary summary = adminModerationService.getModerationSummary();

            return Response.ok()
                    .entity(new SuccessResponse<>(summary, "Statistics retrieved successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error getting moderation statistics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    private int toPageIndex(int page) {
        return Math.max(page - 1, 0);
    }

    private <T> PageResponse<T> toPageResponse(List<T> data, int page, int pageSize, long totalItems) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        PageResponse.PaginationInfo pagination = new PageResponse.PaginationInfo(
                safePage,
                safePageSize,
                totalItems);
        return new PageResponse<>(data, pagination);
    }

    private AdminModerationService.ReportAction resolveReportAction(String action) {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Action is required");
        }
        String normalized = action.trim().toUpperCase();
        if ("DISMISSED".equals(normalized)) {
            return AdminModerationService.ReportAction.DISMISS;
        }
        if ("ACTION_TAKEN".equals(normalized)) {
            return AdminModerationService.ReportAction.FLAG_CONTENT;
        }
        return AdminModerationService.ReportAction.valueOf(normalized);
    }

    /**
     * REST controller for admin analytics operations.
     * Handles platform analytics and reporting.
     */
    @Path("/api/v1/admin/analytics")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "Admin Analytics", description = "Admin analytics and reporting")
    @Authorized(roles = { UserRole.SUPER_ADMIN })
    public static class AdminAnalyticsController {

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
         * @param endDate   The end date
         * @return Accommodation statistics
         */
        @GET
        @Path("/accommodations")
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

                AdminAnalyticsService.AccommodationAnalytics statistics = adminAnalyticsService
                        .getAccommodationAnalytics();

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
         * @param endDate   The end date
         * @return Reel statistics
         */
        @GET
        @Path("/reels")
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

                AdminAnalyticsService.ReelAnalytics statistics = adminAnalyticsService.getReelAnalytics();

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
         * @param endDate   The end date
         * @return Booking statistics
         */
        @GET
        @Path("/bookings")
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

                AdminAnalyticsService.BookingAnalytics statistics = adminAnalyticsService.getBookingAnalytics();

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
         * @param endDate   The end date
         * @return Revenue reports
         */
        @GET
        @Path("/revenue")
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
                AdminAnalyticsService.RevenueAnalytics revenue = adminAnalyticsService
                        .getRevenueAnalytics(resolvedStart, resolvedEnd);

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
         * Get platform fee analytics (service fees collected from
         * suppliers/associations).
         */
        @GET
        @Path("/revenue/platform-fees")
        @Operation(summary = "Get platform fee analytics", description = "Summarize platform fees collected from suppliers and association managers")
        @APIResponses(value = {
                @APIResponse(responseCode = "200", description = "Platform fee analytics retrieved successfully"),
                @APIResponse(responseCode = "401", description = "Not authenticated"),
                @APIResponse(responseCode = "403", description = "Insufficient permissions")
        })
        public Response getPlatformFees(
                @QueryParam("startDate") LocalDate startDate,
                @QueryParam("endDate") LocalDate endDate) {
            try {
                log.info("Get platform fee analytics request");

                AdminAnalyticsService.PlatformFeeAnalytics analytics = adminAnalyticsService
                        .getPlatformFeeAnalytics(startDate, endDate);

                return Response.ok()
                        .entity(new SuccessResponse<>(analytics, "Platform fees retrieved successfully"))
                        .build();

            } catch (Exception e) {
                log.error("Unexpected error getting platform fee analytics", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                        .build();
            }
        }

        /**
         * Get user statistics.
         *
         * @param startDate The start date
         * @param endDate   The end date
         * @return User statistics
         */
        @GET
        @Path("/users")
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

                AdminAnalyticsService.UserAnalytics statistics = adminAnalyticsService.getUserAnalytics();

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
         * @param endDate   The end date
         * @return Content statistics
         */
        @GET
        @Path("/content")
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
}