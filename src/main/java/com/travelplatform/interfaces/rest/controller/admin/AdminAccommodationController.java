package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin controller for accommodation management.
 * All endpoints require SUPER_ADMIN role.
 */
@Path("/api/v1/admin/accommodations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Accommodation Management", description = "SUPER_ADMIN endpoints for managing accommodations")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminAccommodationController {

        private static final Logger logger = LoggerFactory.getLogger(AdminAccommodationController.class);

        @Inject
        AccommodationRepository accommodationRepository;

        @Inject
        AuditService auditService;

        /**
         * List all accommodations with filters.
         */
        @GET
        @Operation(summary = "List all accommodations", description = "Get all accommodations with filters")
        public PaginatedResponse<Accommodation> listAccommodations(
                        @QueryParam("status") ApprovalStatus status,
                        @QueryParam("supplierId") UUID supplierId,
                        @QueryParam("page") @DefaultValue("0") int page,
                        @QueryParam("size") @DefaultValue("20") int size) {

                logger.info("Admin listing accommodations: status={}, page={}, size={}", status, page, size);

                List<Accommodation> accommodations;
                long totalCount;

                if (status != null) {
                        accommodations = accommodationRepository.findByStatusPaginated(status, page, size);
                        totalCount = accommodationRepository.countByStatus(status);
                } else if (supplierId != null) {
                        accommodations = accommodationRepository.findBySupplierIdPaginated(supplierId, page, size);
                        totalCount = accommodationRepository.countBySupplierId(supplierId);
                } else {
                        List<Accommodation> all = accommodationRepository.findAll();
                        totalCount = all.size();
                        int from = Math.max(0, page * size);
                        int to = Math.min(all.size(), from + size);
                        accommodations = from < to ? all.subList(from, to) : List.of();
                }

                return PaginatedResponse.of(accommodations, totalCount, page, size);
        }

        /**
         * Approve accommodation.
         */
        @POST
        @Path("/{id}/approve")
        @Transactional
        @Operation(summary = "Approve accommodation", description = "Make accommodation listing live")
        public BaseResponse<Accommodation> approveAccommodation(@PathParam("id") UUID accommodationId) {
                logger.info("Admin approving accommodation: accommodationId={}", accommodationId);

                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

                accommodation.approve(null);
                accommodationRepository.update(accommodation);

                auditService.logAction("ACCOMMODATION_APPROVED", "Accommodation", accommodationId,
                                Map.of("name", accommodation.getName(), "supplierId",
                                                accommodation.getSupplierId().toString()));

                return BaseResponse.success(accommodation, "Accommodation approved successfully");
        }

        /**
         * Reject accommodation.
         */
        @POST
        @Path("/{id}/reject")
        @Transactional
        @Operation(summary = "Reject accommodation", description = "Deny accommodation listing")
        public BaseResponse<Void> rejectAccommodation(
                        @PathParam("id") UUID accommodationId,
                        RejectAccommodationRequest request) {

                logger.info("Admin rejecting accommodation: accommodationId={}, reason={}",
                                accommodationId, request.reason);

                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

                accommodation.reject();
                accommodationRepository.update(accommodation);

                auditService.logAction("ACCOMMODATION_REJECTED", "Accommodation", accommodationId,
                                Map.of("reason", request.reason, "supplierId",
                                                accommodation.getSupplierId().toString()));

                return BaseResponse.success("Accommodation rejected successfully");
        }

        /**
         * Suspend accommodation.
         */
        @POST
        @Path("/{id}/suspend")
        @Transactional
        @Operation(summary = "Suspend accommodation", description = "Temporarily remove from listings")
        public BaseResponse<Void> suspendAccommodation(
                        @PathParam("id") UUID accommodationId,
                        SuspendAccommodationRequest request) {

                logger.info("Admin suspending accommodation: accommodationId={}, reason={}",
                                accommodationId, request.reason);

                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

                accommodation.flag();
                accommodationRepository.update(accommodation);

                auditService.logAction("ACCOMMODATION_SUSPENDED", "Accommodation", accommodationId,
                                Map.of("reason", request.reason));

                return BaseResponse.success("Accommodation suspended successfully");
        }

        /**
         * Delete accommodation.
         */
        @DELETE
        @Path("/{id}")
        @Transactional
        @Operation(summary = "Delete accommodation", description = "Permanently remove accommodation")
        public BaseResponse<Void> deleteAccommodation(
                        @PathParam("id") UUID accommodationId,
                        DeleteAccommodationRequest request) {

                logger.info("Admin deleting accommodation: accommodationId={}, reason={}",
                                accommodationId, request.reason);

                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

                accommodation.flag();
                accommodationRepository.update(accommodation);

                auditService.logAction("ACCOMMODATION_DELETED", "Accommodation", accommodationId,
                                Map.of("reason", request.reason, "supplierId",
                                                accommodation.getSupplierId().toString()));

                return BaseResponse.success("Accommodation deleted successfully");
        }

        /**
         * Update accommodation visibility rank (Premium Visibility).
         */
        @PUT
        @Path("/{id}/visibility")
        @Transactional
        @Operation(summary = "Update visibility rank", description = "Adjust manual visibility ranking for premium listings")
        public BaseResponse<Void> updateVisibilityRank(
                        @PathParam("id") UUID accommodationId,
                        UpdateVisibilityRequest request) {

                logger.info("Admin updating visibility rank: accommodationId={}, rank={}",
                                accommodationId, request.rank);

                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

                accommodation.updateVisibilityRank(request.rank);
                accommodationRepository.update(accommodation);

                auditService.logAction("ACCOMMODATION_VISIBILITY_UPDATED", "Accommodation", accommodationId,
                                Map.of("rank", String.valueOf(request.rank)));

                return BaseResponse.success("Visibility rank updated successfully");
        }

        /**
         * View accommodation analytics.
         */
        @GET
        @Path("/{id}/analytics")
        @Operation(summary = "View accommodation analytics", description = "Get performance metrics")
        public BaseResponse<AccommodationAnalyticsResponse> getAccommodationAnalytics(
                        @PathParam("id") UUID accommodationId) {

                logger.info("Admin viewing accommodation analytics: accommodationId={}", accommodationId);

                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

                AccommodationAnalyticsResponse analytics = new AccommodationAnalyticsResponse();
                analytics.viewCount = accommodation.getViewCount();
                analytics.averageRating = accommodation.getAverageRating();

                return BaseResponse.success(analytics);
        }

        // Request/Response DTOs

        public static class RejectAccommodationRequest {
                @NotBlank
                public String reason;
        }

        public static class SuspendAccommodationRequest {
                @NotBlank
                public String reason;
        }

        public static class DeleteAccommodationRequest {
                @NotBlank
                public String reason;
        }

        public static class UpdateVisibilityRequest {
                @NotNull
                public Integer rank;
        }

        public static class AccommodationAnalyticsResponse {
                public long viewCount;
                public long bookingCount;
                public Object revenue;
                public Double averageRating;
        }
}
