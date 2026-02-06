package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import com.travelplatform.infrastructure.security.password.PasswordEncoder;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin controller for user management.
 * All endpoints require SUPER_ADMIN role.
 */
@Path("/api/v1/admin/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - User Management", description = "SUPER_ADMIN endpoints for managing users")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    AuditService auditService;

    /**
     * List all users with filters.
     */
    @GET
    @Operation(summary = "List users", description = "Get all users with optional filters")
    public PaginatedResponse<UserResponse> listUsers(
            @QueryParam("role") UserRole role,
            @QueryParam("status") UserStatus status,
            @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing users: role={}, status={}, range={} to {}, page={}",
                role, status, startDate, endDate, page);

        List<User> users = userRepository.findAll(role, status, startDate, endDate, page, size);
        long totalCount = userRepository.count(role, status, startDate, endDate);

        List<UserResponse> responses = users.stream()
                .map(UserResponse::new)
                .toList();

        return PaginatedResponse.of(responses, totalCount, page, size);
    }

    /**
     * Create new user manually.
     */
    @POST
    @Transactional
    @Operation(summary = "Create user", description = "Manually create user account")
    public BaseResponse<UserResponse> createUser(CreateUserRequest request) {
        logger.info("Admin creating user: email={}, role={}", request.email, request.role);

        if (userRepository.findByEmail(request.email).isPresent()) {
            return BaseResponse.error("Email already exists");
        }

        User user = new User(request.email, passwordEncoder.encode(request.password), request.role);

        if (request.fullName != null) {
            UserProfile profile = new UserProfile(user.getId());
            profile.setFullName(request.fullName);
            user.setProfile(profile);
        }

        userRepository.save(user);

        auditService.logAction("USER_CREATED", "User", user.getId(),
                Map.of("email", request.email, "role", request.role.toString()));

        return BaseResponse.success(new UserResponse(user), "User created successfully");
    }

    /**
     * Get user details.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get user", description = "View user details and statistics")
    public BaseResponse<UserResponse> getUser(@PathParam("id") UUID userId) {
        logger.info("Admin viewing user: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return BaseResponse.success(new UserResponse(user));
    }

    /**
     * Update user.
     */
    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update user", description = "Update user information")
    public BaseResponse<UserResponse> updateUser(@PathParam("id") UUID userId, UpdateUserRequest request) {
        logger.info("Admin updating user: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.fullName != null) {
            UserProfile profile = user.getProfile();
            if (profile == null) {
                profile = new UserProfile(user.getId());
                user.setProfile(profile);
            }
            profile.setFullName(request.fullName);
        }

        if (request.status != null) {
            user.setStatus(request.status);
        }

        userRepository.update(user);

        auditService.logAction("USER_UPDATED", "User", userId, Map.of());

        return BaseResponse.success(new UserResponse(user), "User updated successfully");
    }

    /**
     * Suspend user account.
     */
    @POST
    @Path("/{id}/suspend")
    @Transactional
    @Operation(summary = "Suspend user", description = "Temporarily suspend user account")
    public BaseResponse<Void> suspendUser(@PathParam("id") UUID userId, ActionRequest request) {
        logger.info("Admin suspending user: userId={}, reason={}", userId, request.reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setStatus(UserStatus.SUSPENDED);
        userRepository.update(user);

        auditService.logAction("USER_SUSPENDED", "User", userId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("User suspended successfully");
    }

    /**
     * Ban user account permanently.
     */
    @POST
    @Path("/{id}/ban")
    @Transactional
    @Operation(summary = "Ban user", description = "Permanently ban user account")
    public BaseResponse<Void> banUser(@PathParam("id") UUID userId, ActionRequest request) {
        logger.info("Admin banning user: userId={}, reason={}", userId, request.reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setStatus(UserStatus.BANNED);
        userRepository.update(user);

        auditService.logAction("USER_BANNED", "User", userId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("User banned successfully");
    }

    /**
     * Disable user account.
     */
    @POST
    @Path("/{id}/disable")
    @Transactional
    @Operation(summary = "Disable user", description = "Disable user account")
    public BaseResponse<Void> disableUser(@PathParam("id") UUID userId, ActionRequest request) {
        logger.info("Admin disabling user: userId={}, reason={}", userId, request.reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setStatus(UserStatus.DISABLED);
        userRepository.update(user);

        auditService.logAction("USER_DISABLED", "User", userId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("User disabled successfully");
    }

    /**
     * Delete user account (Soft Delete).
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete user", description = "Mark user account as deleted")
    public BaseResponse<Void> deleteUser(@PathParam("id") UUID userId, ActionRequest request) {
        logger.info("Admin deleting user: userId={}, reason={}", userId, request.reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setStatus(UserStatus.DELETED);
        userRepository.update(user);

        auditService.logAction("USER_DELETED", "User", userId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("User deleted successfully");
    }

    /**
     * Restore user account to active status.
     */
    @POST
    @Path("/{id}/restore")
    @Transactional
    @Operation(summary = "Restore user", description = "Restore suspended/banned/disabled user account")
    public BaseResponse<Void> restoreUser(@PathParam("id") UUID userId) {
        logger.info("Admin restoring user: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.update(user);

        auditService.logAction("USER_RESTORED", "User", userId, Map.of());

        return BaseResponse.success("User restored successfully");
    }

    // Request/Response DTOs

    public static class CreateUserRequest {
        public String email;
        public String password;
        public String fullName;
        public UserRole role;
    }

    public static class UpdateUserRequest {
        public String fullName;
        public UserStatus status;
    }

    public static class ActionRequest {
        public String reason;
    }

    public static class UserResponse {
        public String id;
        public String email;
        public String fullName;
        public UserRole role;
        public UserStatus status;
        public boolean emailVerified;
        public String createdAt;
        public String lastLoginAt;

        public UserResponse(User user) {
            this.id = user.getId().toString();
            this.email = user.getEmail();
            this.fullName = user.getProfile() != null ? user.getProfile().getFullName() : null;
            this.role = user.getRole();
            this.status = user.getStatus();
            this.emailVerified = user.isEmailVerified();
            this.createdAt = user.getCreatedAt().toString();
            this.lastLoginAt = user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null;
        }
    }
}