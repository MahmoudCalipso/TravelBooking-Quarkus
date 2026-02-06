package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for viewing system permissions and role mappings.
 * In this implementation, permissions are statically mapped to roles
 * via @Authorized.
 */
@Path("/api/v1/admin/permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Permission Management", description = "SUPER_ADMIN endpoints for viewing system permissions")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminPermissionController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPermissionController.class);

    /**
     * List all system permissions grouped by module.
     */
    @GET
    @Operation(summary = "List all permissions", description = "Get a list of all predefined system permissions")
    public BaseResponse<Map<String, List<String>>> listAllPermissions() {
        logger.info("Admin listing all system permissions");

        Map<String, List<String>> permissions = Map.of(
                "USER_MANAGEMENT", List.of("READ_USERS", "WRITE_USERS", "BAN_USERS", "SUSPEND_USERS", "MANAGE_ROLES"),
                "ACCOMMODATION", List.of("READ_ACCOMMODATIONS", "WRITE_ACCOMMODATIONS", "APPROVE_ACCOMMODATIONS"),
                "BOOKING", List.of("READ_BOOKINGS", "MANAGE_BOOKINGS", "REFUND_BOOKINGS"),
                "CONTENT_MODERATION", List.of("MODERATE_REELS", "MODERATE_REVIEWS", "MODERATE_EVENTS"),
                "SYSTEM", List.of("VIEW_AUDIT_LOGS", "VIEW_ANALYTICS", "MANAGE_GLOBALIZATION"));

        return BaseResponse.success(permissions);
    }

    /**
     * View which roles have a specific permission (Statically defined).
     */
    @GET
    @Path("/{permission}/roles")
    @Operation(summary = "View roles for permission", description = "Get a list of roles that have a specific permission")
    public BaseResponse<List<String>> getRolesForPermission(@PathParam("permission") String permission) {
        logger.info("Admin viewing roles for permission: {}", permission);

        // Static mapping for demonstration as per Pattern 3 (No dynamic permissions)
        List<String> roles;
        if (permission.startsWith("READ_")) {
            roles = List.of("SUPER_ADMIN", "ASSOCIATION_MANAGER");
        } else if (permission.contains("APPROVE") || permission.contains("BAN")) {
            roles = List.of("SUPER_ADMIN");
        } else {
            roles = List.of("SUPER_ADMIN");
        }

        return BaseResponse.success(roles);
    }
}
