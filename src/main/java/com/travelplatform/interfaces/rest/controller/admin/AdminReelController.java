package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.repository.TravelReelRepository;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin controller for reel moderation.
 * All endpoints require SUPER_ADMIN role.
 */
@Path("/api/v1/admin/reels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Reel Moderation", description = "SUPER_ADMIN endpoints for moderating travel reels")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminReelController {

    private static final Logger logger = LoggerFactory.getLogger(AdminReelController.class);

    @Inject
    TravelReelRepository reelRepository;

    @Inject
    AuditService auditService;

    /**
     * View pending reels awaiting approval.
     */
    @GET
    @Path("/pending")
    @Operation(summary = "View pending reels", description = "Get all reels awaiting approval")
    public PaginatedResponse<TravelReel> getPendingReels(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin viewing pending reels: page={}, size={}", page, size);

        List<TravelReel> reels = reelRepository.findByStatusPaginated(ApprovalStatus.PENDING, page, size);
        long totalCount = reelRepository.countByStatus(ApprovalStatus.PENDING);

        return PaginatedResponse.of(reels, totalCount, page, size);
    }

    /**
     * Approve reel.
     */
    @POST
    @Path("/{id}/approve")
    @Transactional
    @Operation(summary = "Approve reel", description = "Make reel visible in feed")
    public BaseResponse<TravelReel> approveReel(@PathParam("id") UUID reelId) {
        logger.info("Admin approving reel: reelId={}", reelId);

        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new NotFoundException("Reel not found"));

        reel.approve(null);
        reelRepository.update(reel);

        auditService.logAction("REEL_APPROVED", "TravelReel", reelId,
                Map.of("title", reel.getTitle(), "creatorId", reel.getCreatorId().toString()));

        // TODO: Send notification to creator

        return BaseResponse.success(reel, "Reel approved successfully");
    }

    /**
     * Reject reel.
     */
    @POST
    @Path("/{id}/reject")
    @Transactional
    @Operation(summary = "Reject reel", description = "Deny reel publication")
    public BaseResponse<Void> rejectReel(@PathParam("id") UUID reelId, RejectReelRequest request) {
        logger.info("Admin rejecting reel: reelId={}, reason={}", reelId, request.reason);

        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new NotFoundException("Reel not found"));

        reel.reject(null);
        reelRepository.update(reel);

        auditService.logAction("REEL_REJECTED", "TravelReel", reelId,
                Map.of("reason", request.reason, "creatorId", reel.getCreatorId().toString()));

        // TODO: Send notification to creator with reason

        return BaseResponse.success("Reel rejected successfully");
    }

    /**
     * Delete reel.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete reel", description = "Remove reel from platform")
    public BaseResponse<Void> deleteReel(@PathParam("id") UUID reelId, DeleteReelRequest request) {
        logger.info("Admin deleting reel: reelId={}, reason={}", reelId, request.reason);

        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new NotFoundException("Reel not found"));

        // Domain has no soft delete; perform hard delete
        reelRepository.deleteById(reelId);

        auditService.logAction("REEL_DELETED", "TravelReel", reelId,
                Map.of("reason", request.reason, "creatorId", reel.getCreatorId().toString()));

        // TODO: Send notification to creator

        return BaseResponse.success("Reel deleted successfully");
    }

    /**
     * View reel engagement statistics.
     */
    @GET
    @Path("/{id}/stats")
    @Operation(summary = "View reel stats", description = "Get engagement analytics for a reel")
    public BaseResponse<ReelStatsResponse> getReelStats(@PathParam("id") UUID reelId) {
        logger.info("Admin viewing reel stats: reelId={}", reelId);

        TravelReel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new NotFoundException("Reel not found"));

        // TODO: Fetch engagement stats from database
        ReelStatsResponse stats = new ReelStatsResponse();
        stats.viewCount = reel.getViewCount();
        stats.likeCount = reel.getLikeCount();
        stats.commentCount = 0; // TODO: Get from comments
        stats.shareCount = 0; // TODO: Get from shares

        return BaseResponse.success(stats);
    }

    /**
     * View flagged/reported reels.
     */
    @GET
    @Path("/reports")
    @Operation(summary = "View flagged reels", description = "Get reels that have been reported by users")
    public BaseResponse<List<Object>> getFlaggedReels() {
        logger.info("Admin viewing flagged reels");

        // TODO: Implement report system and fetch flagged reels

        return BaseResponse.success(List.of());
    }

    // Request/Response DTOs

    public static class RejectReelRequest {
        @NotBlank
        public String reason;
    }

    public static class DeleteReelRequest {
        @NotBlank
        public String reason;
    }

    public static class ReelStatsResponse {
        public long viewCount;
        public long likeCount;
        public long commentCount;
        public long shareCount;
    }
}
