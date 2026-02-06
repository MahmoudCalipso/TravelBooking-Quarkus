package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.application.dto.request.review.CreateReviewRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.review.ReviewResponse;
import com.travelplatform.application.service.review.ReviewService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
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

import java.util.List;
import java.util.UUID;

/**
 * REST controller for review operations.
 * Handles review creation, management, and helpful votes.
 */
@Path("/api/v1/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Reviews", description = "Review management")
public class WebReviewController {

        private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

        @Inject
        private ReviewService reviewService;

        /**
         * Create review.
         *
         * @param securityContext The security context
         * @param request         The review creation request
         * @return Created review response
         */
        @POST
        @Authorized
        @Operation(summary = "Create review", description = "Create a new review for accommodation")
        @APIResponses(value = {
                        @APIResponse(responseCode = "201", description = "Review created successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response createReview(@Context SecurityContext securityContext, @Valid CreateReviewRequest request) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Create review request by user: {}", userId);

                        ReviewResponse review = reviewService.createReview(UUID.fromString(userId), request);

                        return Response.status(Response.Status.CREATED)
                                        .entity(new SuccessResponse<>(review, "Review created successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Review creation failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error creating review", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get review by ID.
         *
         * @param reviewId The review ID
         * @return Review response
         */
        @GET
        @Path("/{reviewId}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get review by ID", description = "Get review details by ID")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Review retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response getReviewById(@PathParam("reviewId") UUID reviewId) {
                try {
                        log.info("Get review by ID request: {}", reviewId);

                        ReviewResponse review = reviewService.getReviewById(reviewId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(review, "Review retrieved successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Review not found: {}", e.getMessage());
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(new ErrorResponse("REVIEW_NOT_FOUND", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error getting review", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get accommodation reviews.
         *
         * @param accommodationId The accommodation ID
         * @param rating          The rating filter
         * @param page            The page number
         * @param pageSize        The page size
         * @return Paginated list of reviews
         */
        @GET
        @Path("/accommodation/{accommodationId}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get accommodation reviews", description = "Get reviews for an accommodation")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reviews retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "Accommodation not found")
        })
        public Response getAccommodationReviews(
                        @PathParam("accommodationId") UUID accommodationId,
                        @QueryParam("rating") Integer rating,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get accommodation reviews request for accommodation: {}", accommodationId);

                        int pageIndex = toPageIndex(page);
                        List<ReviewResponse> reviews = reviewService.getReviewsByAccommodation(
                                        accommodationId, rating, pageIndex, pageSize);
                        PageResponse<ReviewResponse> response = toPageResponse(reviews, page, pageSize, reviews.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting accommodation reviews", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get user's reviews.
         *
         * @param userId   The user ID
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of reviews
         */
        @GET
        @Path("/user/{userId}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get user's reviews", description = "Get reviews by a user")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reviews retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "User not found")
        })
        public Response getUserReviews(
                        @PathParam("userId") UUID userId,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get user reviews request for user: {}", userId);

                        int pageIndex = toPageIndex(page);
                        List<ReviewResponse> reviews = reviewService.getReviewsByReviewer(userId, pageIndex, pageSize);
                        PageResponse<ReviewResponse> response = toPageResponse(reviews, page, pageSize, reviews.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting user reviews", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Update review.
         *
         * @param securityContext The security context
         * @param reviewId        The review ID
         * @param request         The review update request
         * @return Updated review response
         */
        @PUT
        @Path("/{reviewId}")
        @Authorized
        @Operation(summary = "Update review", description = "Update a review")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Review updated successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Not authorized"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response updateReview(
                        @Context SecurityContext securityContext,
                        @PathParam("reviewId") UUID reviewId,
                        @Valid CreateReviewRequest request) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Update review request: {} by user: {}", reviewId, userId);

                        ReviewResponse review = reviewService.updateReview(
                                        UUID.fromString(userId),
                                        reviewId,
                                        request.getContent(),
                                        request.getTitle(),
                                        request.getPros(),
                                        request.getCons());

                        return Response.ok()
                                        .entity(new SuccessResponse<>(review, "Review updated successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Review update failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error updating review", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Delete review.
         *
         * @param securityContext The security context
         * @param reviewId        The review ID
         * @return Success response
         */
        @DELETE
        @Path("/{reviewId}")
        @Authorized
        @Operation(summary = "Delete review", description = "Delete a review")
        @APIResponses(value = {
                        @APIResponse(responseCode = "204", description = "Review deleted successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Not authorized"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response deleteReview(
                        @Context SecurityContext securityContext,
                        @PathParam("reviewId") UUID reviewId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Delete review request: {} by user: {}", reviewId, userId);

                        reviewService.deleteReview(UUID.fromString(userId), reviewId);

                        return Response.noContent().build();

                } catch (IllegalArgumentException e) {
                        log.error("Review deletion failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("DELETION_FAILED", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error deleting review", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Mark review as helpful.
         *
         * @param securityContext The security context
         * @param reviewId        The review ID
         * @param isHelpful       Whether the review is helpful
         * @return Success response
         */
        @POST
        @Path("/{reviewId}/helpful")
        @Authorized
        @Operation(summary = "Mark review helpful", description = "Mark a review as helpful or not helpful")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Review marked successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response markReviewHelpful(
                        @Context SecurityContext securityContext,
                        @PathParam("reviewId") UUID reviewId,
                        @FormParam("isHelpful") boolean isHelpful) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Mark review helpful request: {} by user: {}", reviewId, userId);

                        reviewService.markReviewHelpful(UUID.fromString(userId), reviewId, isHelpful);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Review marked successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error marking review helpful", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Respond to review (supplier only).
         *
         * @param securityContext The security context
         * @param reviewId        The review ID
         * @param response        The supplier response
         * @return Success response
         */
        @POST
        @Path("/{reviewId}/respond")
        @Authorized(roles = { UserRole.SUPPLIER_SUBSCRIBER })
        @Operation(summary = "Respond to review", description = "Respond to a review (supplier only)")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Response added successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response respondToReview(
                        @Context SecurityContext securityContext,
                        @PathParam("reviewId") UUID reviewId,
                        @FormParam("response") String response) {
                try {
                        String supplierId = securityContext.getUserPrincipal().getName();
                        log.info("Respond to review request: {} by supplier: {}", reviewId, supplierId);

                        reviewService.respondToReview(UUID.fromString(supplierId), reviewId, response);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Response added successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Response addition failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error responding to review", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Approve review (admin only).
         *
         * @param reviewId The review ID
         * @return Success response
         */
        @PUT
        @Path("/{reviewId}/approve")
        @Authorized(roles = { UserRole.SUPER_ADMIN })
        @Operation(summary = "Approve review", description = "Approve a review (admin only)")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Review approved successfully"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response approveReview(
                        @Context SecurityContext securityContext,
                        @PathParam("reviewId") UUID reviewId) {
                try {
                        log.info("Approve review request: {}", reviewId);

                        UUID adminId = UUID.fromString(securityContext.getUserPrincipal().getName());
                        reviewService.approveReview(adminId, reviewId);

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
         * Reject review (admin only).
         *
         * @param reviewId The review ID
         * @param reason   The rejection reason
         * @return Success response
         */
        @PUT
        @Path("/{reviewId}/reject")
        @Authorized(roles = { UserRole.SUPER_ADMIN })
        @Operation(summary = "Reject review", description = "Reject a review (admin only)")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Review rejected successfully"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Review not found")
        })
        public Response rejectReview(
                        @Context SecurityContext securityContext,
                        @PathParam("reviewId") UUID reviewId,
                        @FormParam("reason") String reason) {
                try {
                        log.info("Reject review request: {}", reviewId);

                        UUID adminId = UUID.fromString(securityContext.getUserPrincipal().getName());
                        reviewService.rejectReview(adminId, reviewId);

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
}