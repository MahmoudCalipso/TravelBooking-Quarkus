package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.application.dto.request.reel.CreateReelRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.reel.ReelResponse;
import com.travelplatform.application.service.reel.ReelService;
import com.travelplatform.domain.enums.VisibilityScope;
import com.travelplatform.domain.model.reel.ReelComment;
import com.travelplatform.domain.model.reel.ReelReport;
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
 * REST controller for travel reel operations.
 * Handles reel creation, engagement, comments, and reporting.
 */
@Path("/api/v1/reels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Travel Reels", description = "Short video content management")
public class WebReelController {

        private static final Logger log = LoggerFactory.getLogger(ReelController.class);

        @Inject
        private ReelService reelService;

        /**
         * Get personalized reel feed.
         *
         * @param securityContext The security context
         * @param page            The page number
         * @param pageSize        The page size
         * @return Paginated list of reels
         */
        @GET
        @Path("/feed")
        @Authorized
        @Operation(summary = "Get personalized feed", description = "Get personalized reel feed for current user")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Feed retrieved successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response getPersonalizedFeed(
                        @Context SecurityContext securityContext,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Get personalized feed request for user: {}", userId);

                        int pageIndex = toPageIndex(page);
                        List<ReelResponse> feed = reelService.getReelFeed(
                                        UUID.fromString(userId), pageIndex, pageSize);
                        PageResponse<ReelResponse> response = toPageResponse(feed, page, pageSize, feed.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting personalized feed", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get trending reels.
         *
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of trending reels
         */
        @GET
        @Path("/trending")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get trending reels", description = "Get trending reels")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Trending reels retrieved successfully")
        })
        public Response getTrendingReels(
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get trending reels request");

                        int pageIndex = toPageIndex(page);
                        List<ReelResponse> trending = reelService.getTrendingReels(pageIndex, pageSize);
                        PageResponse<ReelResponse> response = toPageResponse(trending, page, pageSize, trending.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting trending reels", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get reel by ID.
         *
         * @param reelId The reel ID
         * @return Reel response
         */
        @GET
        @Path("/{reelId}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get reel by ID", description = "Get reel details by ID")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reel retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response getReelById(@PathParam("reelId") UUID reelId) {
                try {
                        log.info("Get reel by ID request: {}", reelId);

                        ReelResponse reel = reelService.getReelById(reelId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(reel, "Reel retrieved successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Reel not found: {}", e.getMessage());
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(new ErrorResponse("REEL_NOT_FOUND", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error getting reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get reels by location.
         *
         * @param latitude  The latitude
         * @param longitude The longitude
         * @param radiusKm  The radius in kilometers
         * @param page      The page number
         * @param pageSize  The page size
         * @return Paginated list of reels
         */
        @GET
        @Path("/location/{latitude}/{longitude}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get reels by location", description = "Get reels near a location")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reels retrieved successfully")
        })
        public Response getReelsByLocation(
                        @PathParam("latitude") Double latitude,
                        @PathParam("longitude") Double longitude,
                        @QueryParam("radiusKm") @DefaultValue("10") Double radiusKm,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get reels by location request - lat: {}, lng: {}, radius: {}km", latitude, longitude,
                                        radiusKm);

                        int pageIndex = toPageIndex(page);
                        List<ReelResponse> reels = reelService.getReelsByLocation(
                                        latitude, longitude, radiusKm, pageIndex, pageSize);
                        PageResponse<ReelResponse> response = toPageResponse(reels, page, pageSize, reels.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting reels by location", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get user's public reels.
         *
         * @param userId   The user ID
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of reels
         */
        @GET
        @Path("/user/{userId}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get user's reels", description = "Get a user's public reels")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reels retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "User not found")
        })
        public Response getUserReels(
                        @PathParam("userId") UUID userId,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get user reels request for user: {}", userId);

                        int pageIndex = toPageIndex(page);
                        List<ReelResponse> reels = reelService.getReelsByCreator(userId, pageIndex, pageSize);
                        PageResponse<ReelResponse> response = toPageResponse(reels, page, pageSize, reels.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting user reels", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Create new reel.
         *
         * @param securityContext The security context
         * @param request         The reel creation request
         * @return Created reel response
         */
        @POST
        @Authorized
        @Operation(summary = "Create reel", description = "Create a new travel reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "201", description = "Reel created successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response createReel(@Context SecurityContext securityContext, @Valid CreateReelRequest request) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Create reel request by user: {}", userId);

                        ReelResponse reel = reelService.createReel(UUID.fromString(userId), request);

                        return Response.status(Response.Status.CREATED)
                                        .entity(new SuccessResponse<>(reel, "Reel created successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Reel creation failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error creating reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Update reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @param request         The reel update request
         * @return Updated reel response
         */
        @PUT
        @Path("/{reelId}")
        @Authorized
        @Operation(summary = "Update reel", description = "Update a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reel updated successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Not authorized"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response updateReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId,
                        @Valid CreateReelRequest request) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Update reel request: {} by user: {}", reelId, userId);

                        VisibilityScope visibility = request.getVisibility() != null
                                        ? VisibilityScope.valueOf(request.getVisibility())
                                        : null;

                        ReelResponse reel = reelService.updateReel(
                                        UUID.fromString(userId),
                                        reelId,
                                        request.getTitle(),
                                        request.getDescription(),
                                        visibility);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(reel, "Reel updated successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Reel update failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error updating reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Delete reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @return Success response
         */
        @DELETE
        @Path("/{reelId}")
        @Authorized
        @Operation(summary = "Delete reel", description = "Delete a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "204", description = "Reel deleted successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Not authorized"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response deleteReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Delete reel request: {} by user: {}", reelId, userId);

                        reelService.deleteReel(UUID.fromString(userId), reelId);

                        return Response.noContent().build();

                } catch (IllegalArgumentException e) {
                        log.error("Reel deletion failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("DELETION_FAILED", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error deleting reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Track view.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @param watchDuration   The watch duration in seconds
         * @return Success response
         */
        @POST
        @Path("/{reelId}/view")
        @Authorized
        @Operation(summary = "Track view", description = "Track a view on a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "View tracked successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response trackView(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId,
                        @FormParam("watchDuration") Integer watchDuration) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Track view request for reel: {} by user: {}", reelId, userId);

                        reelService.trackView(UUID.fromString(userId), reelId, watchDuration);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "View tracked successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error tracking view", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Like reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @return Success response
         */
        @POST
        @Path("/{reelId}/like")
        @Authorized
        @Operation(summary = "Like reel", description = "Like a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reel liked successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response likeReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Like reel request: {} by user: {}", reelId, userId);

                        reelService.likeReel(UUID.fromString(userId), reelId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Reel liked successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error liking reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Unlike reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @return Success response
         */
        @DELETE
        @Path("/{reelId}/like")
        @Authorized
        @Operation(summary = "Unlike reel", description = "Unlike a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reel unliked successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response unlikeReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Unlike reel request: {} by user: {}", reelId, userId);

                        reelService.unlikeReel(UUID.fromString(userId), reelId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Reel unliked successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error unliking reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Share reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @return Success response
         */
        @POST
        @Path("/{reelId}/share")
        @Authorized
        @Operation(summary = "Share reel", description = "Track a share on a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Share tracked successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response shareReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Share reel request: {} by user: {}", reelId, userId);

                        reelService.shareReel(UUID.fromString(userId), reelId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Share tracked successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error sharing reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Bookmark reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @return Success response
         */
        @POST
        @Path("/{reelId}/bookmark")
        @Authorized
        @Operation(summary = "Bookmark reel", description = "Bookmark a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reel bookmarked successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response bookmarkReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Bookmark reel request: {} by user: {}", reelId, userId);

                        reelService.bookmarkReel(UUID.fromString(userId), reelId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Reel bookmarked successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error bookmarking reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Remove bookmark.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @return Success response
         */
        @DELETE
        @Path("/{reelId}/bookmark")
        @Authorized
        @Operation(summary = "Remove bookmark", description = "Remove bookmark from a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Bookmark removed successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response removeBookmark(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Remove bookmark request: {} by user: {}", reelId, userId);

                        reelService.removeBookmark(UUID.fromString(userId), reelId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Bookmark removed successfully"))
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error removing bookmark", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get bookmarked reels.
         *
         * @param securityContext The security context
         * @param page            The page number
         * @param pageSize        The page size
         * @return Paginated list of bookmarked reels
         */
        @GET
        @Path("/bookmarked")
        @Authorized
        @Operation(summary = "Get bookmarked reels", description = "Get current user's bookmarked reels")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Bookmarked reels retrieved successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response getBookmarkedReels(
                        @Context SecurityContext securityContext,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Get bookmarked reels request for user: {}", userId);

                        int pageIndex = toPageIndex(page);
                        List<ReelResponse> reels = reelService.getBookmarkedReels(
                                        UUID.fromString(userId), pageIndex, pageSize);
                        PageResponse<ReelResponse> response = toPageResponse(reels, page, pageSize, reels.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting bookmarked reels", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Add comment.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @param content         The comment content
         * @param parentCommentId The parent comment ID (for replies)
         * @return Success response
         */
        @POST
        @Path("/{reelId}/comments")
        @Authorized
        @Operation(summary = "Add comment", description = "Add a comment to a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "201", description = "Comment added successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response addComment(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId,
                        @FormParam("content") String content,
                        @FormParam("parentCommentId") UUID parentCommentId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Add comment request for reel: {} by user: {}", reelId, userId);

                        reelService.addComment(UUID.fromString(userId), reelId, content, parentCommentId);

                        return Response.status(Response.Status.CREATED)
                                        .entity(new SuccessResponse<>(null, "Comment added successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Comment addition failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error adding comment", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get comments.
         *
         * @param reelId   The reel ID
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of comments
         */
        @GET
        @Path("/{reelId}/comments")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get comments", description = "Get comments for a reel")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Comments retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response getComments(
                        @PathParam("reelId") UUID reelId,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get comments request for reel: {}", reelId);

                        int pageIndex = toPageIndex(page);
                        List<ReelComment> comments = reelService.getComments(reelId, pageIndex, pageSize);
                        PageResponse<ReelComment> response = toPageResponse(comments, page, pageSize, comments.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting comments", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Report reel.
         *
         * @param securityContext The security context
         * @param reelId          The reel ID
         * @param reason          The report reason
         * @param description     The report description
         * @return Success response
         */
        @POST
        @Path("/{reelId}/report")
        @Authorized
        @Operation(summary = "Report reel", description = "Report a reel for inappropriate content")
        @APIResponses(value = {
                        @APIResponse(responseCode = "201", description = "Report submitted successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Reel not found")
        })
        public Response reportReel(
                        @Context SecurityContext securityContext,
                        @PathParam("reelId") UUID reelId,
                        @FormParam("reason") String reason,
                        @FormParam("description") String description) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Report reel request: {} by user: {}", reelId, userId);

                        reelService.reportReel(UUID.fromString(userId), reelId, reason, description);

                        return Response.status(Response.Status.CREATED)
                                        .entity(new SuccessResponse<>(null, "Report submitted successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Report submission failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error reporting reel", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get user's reports.
         *
         * @param securityContext The security context
         * @param page            The page number
         * @param pageSize        The page size
         * @return Paginated list of reports
         */
        @GET
        @Path("/my-reports")
        @Authorized
        @Operation(summary = "Get my reports", description = "Get current user's submitted reports")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Reports retrieved successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response getMyReports(
                        @Context SecurityContext securityContext,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Get my reports request for user: {}", userId);

                        int pageIndex = toPageIndex(page);
                        List<ReelReport> reports = reelService.getUserReports(UUID.fromString(userId), pageIndex,
                                        pageSize);
                        PageResponse<ReelReport> response = toPageResponse(reports, page, pageSize, reports.size());

                        return Response.ok().entity(response).build();

                } catch (Exception e) {
                        log.error("Unexpected error getting my reports", e);
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
