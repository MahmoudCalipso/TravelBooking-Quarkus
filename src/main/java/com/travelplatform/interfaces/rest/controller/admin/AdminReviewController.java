package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.review.Review;
import com.travelplatform.domain.repository.ReviewRepository;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
 * Admin controller for review moderation.
 */
@Path("/api/v1/admin/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Review Moderation", description = "SUPER_ADMIN endpoints for moderating reviews")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminReviewController {

    private static final Logger logger = LoggerFactory.getLogger(AdminReviewController.class);

    @Inject
    ReviewRepository reviewRepository;

    @Inject
    AuditService auditService;

    @GET
    @Path("/flagged")
    @Operation(summary = "List flagged reviews", description = "Get reviews that have been reported")
    public PaginatedResponse<Review> listFlaggedReviews(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing flagged reviews");

        // TODO: Implement flagged reviews query
        List<Review> reviews = List.of();
        long totalCount = 0;

        return PaginatedResponse.of(reviews, totalCount, page, size);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete review", description = "Remove inappropriate review")
    public BaseResponse<Void> deleteReview(@PathParam("id") UUID reviewId, DeleteRequest request) {
        logger.info("Admin deleting review: reviewId={}, reason={}", reviewId, request.reason);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        reviewRepository.deleteById(reviewId);

        auditService.logAction("REVIEW_DELETED", "Review", reviewId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("Review deleted successfully");
    }

    public static class DeleteRequest {
        public String reason;
    }
}