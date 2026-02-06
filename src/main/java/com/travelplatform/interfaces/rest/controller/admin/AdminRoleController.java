package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Admin controller for managing roles and their assignments.
 */
@Path("/api/v1/admin/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Role Management", description = "SUPER_ADMIN endpoints for managing system roles and user assignments")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminRoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRoleController.class);

    @Inject
    UserRepository userRepository;

    /**
     * List all available roles in the system.
     */
    @GET
    @Operation(summary = "List all roles", description = "Get a list of all defined roles in the system")
    public BaseResponse<List<String>> listAllRoles() {
        logger.info("Admin listing all roles");
        List<String> roles = Arrays.stream(UserRole.values())
                .map(UserRole::name)
                .collect(Collectors.toList());
        return BaseResponse.success(roles);
    }

    /**
     * List users assigned to a specific role.
     */
    @GET
    @Path("/{roleName}/users")
    @Operation(summary = "List users by role", description = "Get all users assigned to a specific role")
    public BaseResponse<List<AdminUserController.UserResponse>> getUsersByRole(@PathParam("roleName") String roleName) {
        logger.info("Admin listing users for role: {}", roleName);

        UserRole role = UserRole.valueOf(roleName.toUpperCase());
        List<User> users = userRepository.findByRole(role);

        List<AdminUserController.UserResponse> responses = users.stream()
                .map(AdminUserController.UserResponse::new)
                .toList();

        return BaseResponse.success(responses);
    }

    /**
     * Assign a role to a user.
     */
    @POST
    @Path("/assign")
    @Transactional
    @Operation(summary = "Assign role to user", description = "Update a user's role assignment")
    public BaseResponse<Void> assignRoleToUser(RoleAssignmentRequest request) {
        logger.info("Admin assigning role {} to user {}", request.roleName, request.userId);

        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserRole role = UserRole.valueOf(request.roleName.toUpperCase());
        user.setRole(role);
        userRepository.update(user);

        return BaseResponse.success("Role assigned successfully");
    }

    /**
     * Revoke a role from a user (resets to TRAVELER).
     */
    @POST
    @Path("/revoke")
    @Transactional
    @Operation(summary = "Revoke role from user", description = "Reset a user's role to the default TRAVELER role")
    public BaseResponse<Void> revokeRoleFromUser(RevokeRoleRequest request) {
        logger.info("Admin revoking role from user {}", request.userId);

        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setRole(UserRole.TRAVELER);
        userRepository.update(user);

        return BaseResponse.success("Role revoked/reset successfully");
    }

    /**
     * Request DTO for role assignment.
     */
    public static class RoleAssignmentRequest {
        public UUID userId;
        public String roleName;
    }

    /**
     * Request DTO for role revocation.
     */
    public static class RevokeRoleRequest {
        public UUID userId;
    }
}
