package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.application.dto.request.user.UpdatePreferencesRequest;
import com.travelplatform.application.dto.request.user.UpdateProfileRequest;
import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.user.PreferencesResponse;
import com.travelplatform.application.dto.response.user.ProfileResponse;
import com.travelplatform.application.dto.response.user.UserResponse;
import com.travelplatform.application.service.user.UserService;
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
 * REST controller for user operations.
 * Handles user profile, preferences, and account management.
 */
@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User profile and account management")
public class WebUserController {

    private static final Logger log = LoggerFactory.getLogger(WebUserController.class);

    @Inject
    private UserService userService;

    /**
     * Get current user profile.
     *
     * @param securityContext The security context
     * @return Profile response
     */
    @GET
    @Path("/profile")
    @Authorized
    @Operation(summary = "Get current user profile", description = "Get the current authenticated user's profile")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getProfile(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get profile request for user: {}", userId);

            ProfileResponse profile = userService.getProfile(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(profile, "Profile retrieved successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error getting profile", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Update current user profile.
     *
     * @param securityContext The security context
     * @param request         The profile update request
     * @return Updated profile response
     */
    @PUT
    @Path("/profile")
    @Authorized
    @Operation(summary = "Update user profile", description = "Update the current authenticated user's profile")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Profile updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response updateProfile(@Context SecurityContext securityContext, @Valid UpdateProfileRequest request) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Update profile request for user: {}", userId);

            ProfileResponse profile = userService.updateProfile(UUID.fromString(userId), request);

            return Response.ok()
                    .entity(new SuccessResponse<>(profile, "Profile updated successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Profile update failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error updating profile", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Update profile photo.
     *
     * @param securityContext The security context
     * @param photoUrl        The new photo URL
     * @return Updated profile response
     */
    @PATCH
    @Path("/profile/photo")
    @Authorized
    @Operation(summary = "Update profile photo", description = "Update the current user's profile photo")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Photo updated successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response updateProfilePhoto(@Context SecurityContext securityContext,
            @FormParam("photoUrl") String photoUrl) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Update profile photo request for user: {}", userId);

            ProfileResponse profile = userService.updateProfilePhoto(UUID.fromString(userId), photoUrl);

            return Response.ok()
                    .entity(new SuccessResponse<>(profile, "Photo updated successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error updating profile photo", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get user preferences.
     *
     * @param securityContext The security context
     * @return Preferences response
     */
    @GET
    @Path("/preferences")
    @Authorized
    @Operation(summary = "Get user preferences", description = "Get the current user's preferences")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Preferences retrieved successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getPreferences(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get preferences request for user: {}", userId);

            PreferencesResponse preferences = userService.getPreferences(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(preferences, "Preferences retrieved successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error getting preferences", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Update user preferences.
     *
     * @param securityContext The security context
     * @param request         The preferences update request
     * @return Updated preferences response
     */
    @PUT
    @Path("/preferences")
    @Authorized
    @Operation(summary = "Update user preferences", description = "Update the current user's preferences")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Preferences updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response updatePreferences(@Context SecurityContext securityContext,
            @Valid UpdatePreferencesRequest request) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Update preferences request for user: {}", userId);

            PreferencesResponse preferences = userService.updatePreferences(UUID.fromString(userId), request);

            return Response.ok()
                    .entity(new SuccessResponse<>(preferences, "Preferences updated successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Preferences update failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error updating preferences", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get user by ID (public profile).
     *
     * @param userId The user ID
     * @return User response
     */
    @GET
    @Path("/{userId}")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Get user by ID", description = "Get a user's public profile by ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User retrieved successfully"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response getUserById(@PathParam("userId") UUID userId) {
        try {
            log.info("Get user by ID request: {}", userId);

            UserResponse user = userService.getUserById(userId);

            return Response.ok()
                    .entity(new SuccessResponse<>(user, "User retrieved successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("User not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("USER_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Delete user account.
     *
     * @param securityContext The security context
     * @return Success response
     */
    @DELETE
    @Path("/account")
    @Authorized
    @Operation(summary = "Delete user account", description = "Delete the current user's account")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Account deleted successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response deleteAccount(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Delete account request for user: {}", userId);

            userService.deleteAccount(UUID.fromString(userId));

            return Response.noContent().build();

        } catch (Exception e) {
            log.error("Unexpected error deleting account", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Follow a user.
     *
     * @param securityContext The security context
     * @param userId          The user ID to follow
     * @return Success response
     */
    @POST
    @Path("/{userId}/follow")
    @Authorized
    @Operation(summary = "Follow user", description = "Follow another user")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User followed successfully"),
            @APIResponse(responseCode = "400", description = "Cannot follow yourself"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response followUser(@Context SecurityContext securityContext, @PathParam("userId") UUID userId) {
        try {
            String followerId = securityContext.getUserPrincipal().getName();
            log.info("Follow user request: {} following {}", followerId, userId);

            userService.followUser(UUID.fromString(followerId), userId);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "User followed successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Follow failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("FOLLOW_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error following user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Unfollow a user.
     *
     * @param securityContext The security context
     * @param userId          The user ID to unfollow
     * @return Success response
     */
    @DELETE
    @Path("/{userId}/follow")
    @Authorized
    @Operation(summary = "Unfollow user", description = "Unfollow a user")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User unfollowed successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response unfollowUser(@Context SecurityContext securityContext, @PathParam("userId") UUID userId) {
        try {
            String followerId = securityContext.getUserPrincipal().getName();
            log.info("Unfollow user request: {} unfollowing {}", followerId, userId);

            userService.unfollowUser(UUID.fromString(followerId), userId);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "User unfollowed successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error unfollowing user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get user's followers.
     *
     * @param userId   The user ID
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of followers
     */
    @GET
    @Path("/{userId}/followers")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Get user's followers", description = "Get a list of users following the specified user")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Followers retrieved successfully"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response getFollowers(
            @PathParam("userId") UUID userId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get followers request for user: {}", userId);

            int pageIndex = toPageIndex(page);
            List<UserResponse> followers = userService.getFollowers(userId, pageIndex, pageSize);
            PageResponse<UserResponse> response = toPageResponse(followers, page, pageSize, followers.size());

            return Response.ok().entity(response).build();

        } catch (Exception e) {
            log.error("Unexpected error getting followers", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get user's following.
     *
     * @param userId   The user ID
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of following
     */
    @GET
    @Path("/{userId}/following")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Get user's following", description = "Get a list of users the specified user is following")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Following retrieved successfully"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response getFollowing(
            @PathParam("userId") UUID userId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Get following request for user: {}", userId);

            int pageIndex = toPageIndex(page);
            List<UserResponse> following = userService.getFollowing(userId, pageIndex, pageSize);
            PageResponse<UserResponse> response = toPageResponse(following, page, pageSize, following.size());

            return Response.ok().entity(response).build();

        } catch (Exception e) {
            log.error("Unexpected error getting following", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Search users.
     *
     * @param query    The search query
     * @param page     The page number
     * @param pageSize The page size
     * @return Paginated list of users
     */
    @GET
    @Path("/search")
    @Authorized(allowAnonymous = true)
    @Operation(summary = "Search users", description = "Search for users by name or email")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public Response searchUsers(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            log.info("Search users request: {}", query);

            int pageIndex = toPageIndex(page);
            List<UserResponse> users = userService.searchUsers(query, pageIndex, pageSize);
            PageResponse<UserResponse> response = toPageResponse(users, page, pageSize, users.size());

            return Response.ok().entity(response).build();

        } catch (Exception e) {
            log.error("Unexpected error searching users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Activate user account (admin only).
     *
     * @param userId The user ID
     * @return Success response
     */
    @PUT
    @Path("/{userId}/activate")
    @Authorized(roles = { UserRole.SUPER_ADMIN })
    @Operation(summary = "Activate user account", description = "Activate a user account (admin only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User activated successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response activateUser(@PathParam("userId") UUID userId) {
        try {
            log.info("Activate user request: {}", userId);

            userService.activateUser(userId);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "User activated successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("User activation failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("USER_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error activating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Suspend user account (admin only).
     *
     * @param userId The user ID
     * @return Success response
     */
    @PUT
    @Path("/{userId}/suspend")
    @Authorized(roles = { UserRole.SUPER_ADMIN })
    @Operation(summary = "Suspend user account", description = "Suspend a user account (admin only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User suspended successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response suspendUser(@PathParam("userId") UUID userId) {
        try {
            log.info("Suspend user request: {}", userId);

            userService.suspendUser(userId);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "User suspended successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("User suspension failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("USER_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error suspending user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Verify user email (admin only).
     *
     * @param userId The user ID
     * @return Success response
     */
    @PUT
    @Path("/{userId}/verify")
    @Authorized(roles = { UserRole.SUPER_ADMIN })
    @Operation(summary = "Verify user email", description = "Verify a user's email address (admin only)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Email verified successfully"),
            @APIResponse(responseCode = "403", description = "Insufficient permissions"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response verifyUserEmail(@PathParam("userId") UUID userId) {
        try {
            log.info("Verify user email request: {}", userId);

            userService.verifyEmail(userId);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Email verified successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("USER_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error verifying email", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Update last login timestamp.
     *
     * @param userId The user ID
     */
    public void updateLastLogin(UUID userId) {
        try {
            userService.updateLastLogin(userId);
        } catch (Exception e) {
            log.error("Error updating last login for user: {}", userId, e);
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
