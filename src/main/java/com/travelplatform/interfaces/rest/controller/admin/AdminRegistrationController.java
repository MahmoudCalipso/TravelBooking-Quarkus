package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Admin controller for managing user registrations and approvals.
 */
@Path("/api/v1/admin/registrations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Registration Management", description = "SUPER_ADMIN endpoints for moderating new user registrations")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminRegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRegistrationController.class);

    @Inject
    UserRepository userRepository;

    /**
     * List all pending registrations.
     */
    @GET
    @Path("/pending")
    @Operation(summary = "List pending registrations", description = "View users awaiting account approval")
    public PaginatedResponse<AdminUserController.UserResponse> listPendingRegistrations(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing pending registrations: page={}", page);

        List<User> users = userRepository.findAll(null, UserStatus.PENDING, null, null, page, size);
        long totalCount = userRepository.count(null, UserStatus.PENDING, null, null);

        List<AdminUserController.UserResponse> responses = users.stream()
                .map(AdminUserController.UserResponse::new)
                .toList();

        return PaginatedResponse.of(responses, totalCount, page, size);
    }

    /**
     * Approve a registration request.
     */
    @POST
    @Path("/{userId}/approve")
    @Transactional
    @Operation(summary = "Approve registration", description = "Activate a pending user account")
    public BaseResponse<Void> approveRegistration(@PathParam("userId") UUID userId) {
        logger.info("Admin approving registration: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getStatus() != UserStatus.PENDING) {
            return BaseResponse.error("User is not in PENDING status");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.update(user);

        return BaseResponse.success("User registration approved successfully");
    }

    /**
     * Reject a registration request.
     */
    @POST
    @Path("/{userId}/reject")
    @Transactional
    @Operation(summary = "Reject registration", description = "Reject and delete a pending user account")
    public BaseResponse<Void> rejectRegistration(
            @PathParam("userId") UUID userId,
            @QueryParam("reason") String reason) {
        logger.info("Admin rejecting registration: userId={}, reason={}", userId, reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getStatus() != UserStatus.PENDING) {
            return BaseResponse.error("User is not in PENDING status");
        }

        // Either delete or mark as deleted/rejected
        user.setStatus(UserStatus.DELETED);
        userRepository.update(user);

        return BaseResponse.success("User registration rejected successfully");
    }

    /**
     * Get registration statistics.
     */
    @GET
    @Path("/statistics")
    @Operation(summary = "Get registration stats", description = "View counts of pending, active, and rejected registrations")
    public BaseResponse<Map<String, Long>> getRegistrationStatistics() {
        logger.info("Admin viewing registration statistics");

        long pending = userRepository.count(null, UserStatus.PENDING, null, null);
        long active = userRepository.count(null, UserStatus.ACTIVE, null, null);
        long total = userRepository.count();

        return BaseResponse.success(Map.of(
                "pending", pending,
                "active", active,
                "total", total));
    }
}
