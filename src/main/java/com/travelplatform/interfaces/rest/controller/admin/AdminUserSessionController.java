package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.repository.UserSessionRepository;
import com.travelplatform.infrastructure.persistence.entity.UserSessionEntity;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Admin controller for managing user sessions.
 */
@Path("/api/v1/admin/user-sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Session Management", description = "SUPER_ADMIN endpoints for managing active sessions")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminUserSessionController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserSessionController.class);

    @Inject
    UserSessionRepository sessionRepository;

    /**
     * List all active sessions with pagination.
     */
    @GET
    @Operation(summary = "List all sessions", description = "View all user sessions in the system")
    public PaginatedResponse<SessionResponse> listAllSessions(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing all sessions: page={}", page);

        List<UserSessionEntity> sessions = sessionRepository.findAll(page, size);
        long totalCount = sessionRepository.count();

        List<SessionResponse> responses = sessions.stream()
                .map(SessionResponse::new)
                .toList();

        return PaginatedResponse.of(responses, totalCount, page, size);
    }

    /**
     * View active sessions for a specific user.
     */
    @GET
    @Path("/user/{userId}/active")
    @Operation(summary = "View active sessions for user", description = "Get all active sessions for a specific user")
    public BaseResponse<List<SessionResponse>> getUserActiveSessions(@PathParam("userId") UUID userId) {
        logger.info("Admin viewing active sessions for user: userId={}", userId);

        List<UserSessionEntity> sessions = sessionRepository.findActiveByUserId(userId);
        List<SessionResponse> responses = sessions.stream()
                .map(SessionResponse::new)
                .toList();

        return BaseResponse.success(responses);
    }

    /**
     * View login history for a specific user.
     */
    @GET
    @Path("/user/{userId}/history")
    @Operation(summary = "View login history for user", description = "Get full login session history for a specific user")
    public BaseResponse<List<SessionResponse>> getUserSessionHistory(@PathParam("userId") UUID userId) {
        logger.info("Admin viewing session history for user: userId={}", userId);

        List<UserSessionEntity> sessions = sessionRepository.findByUserId(userId);
        List<SessionResponse> responses = sessions.stream()
                .map(SessionResponse::new)
                .toList();

        return BaseResponse.success(responses);
    }

    /**
     * Revoke a specific session.
     */
    @DELETE
    @Path("/{sessionId}")
    @Transactional
    @Operation(summary = "Revoke session", description = "Terminate a specific user session")
    public BaseResponse<Void> revokeSession(@PathParam("sessionId") UUID sessionId) {
        logger.info("Admin revoking session: sessionId={}", sessionId);

        sessionRepository.deleteById(sessionId);

        return BaseResponse.success("Session revoked successfully");
    }

    /**
     * Revoke all sessions for a specific user.
     */
    @DELETE
    @Path("/user/{userId}")
    @Transactional
    @Operation(summary = "Revoke all user sessions", description = "Terminate all sessions for a specific user")
    public BaseResponse<Void> revokeAllUserSessions(@PathParam("userId") UUID userId) {
        logger.info("Admin revoking all sessions for user: userId={}", userId);

        sessionRepository.deleteAllByUserId(userId);

        return BaseResponse.success("All sessions for user revoked successfully");
    }

    /**
     * Batch revoke multiple sessions.
     */
    @POST
    @Path("/batch-revoke")
    @Transactional
    @Operation(summary = "Batch revoke sessions", description = "Terminate multiple specific sessions")
    public BaseResponse<Void> batchRevokeSessions(List<UUID> sessionIds) {
        logger.info("Admin batch revoking sessions: count={}", sessionIds.size());

        for (UUID sessionId : sessionIds) {
            sessionRepository.deleteById(sessionId);
        }

        return BaseResponse.success("Sessions revoked successfully");
    }

    /**
     * Session response DTO.
     */
    public static class SessionResponse {
        public UUID id;
        public UUID userId;
        public String ipAddress;
        public String userAgent;
        public LocalDateTime createdAt;
        public LocalDateTime expiresAt;
        public LocalDateTime lastActivityAt;
        public boolean isActive;

        public SessionResponse(UserSessionEntity entity) {
            this.id = entity.getId();
            this.userId = entity.getUserId();
            this.ipAddress = entity.getIpAddress();
            this.userAgent = entity.getUserAgent();
            this.createdAt = entity.getCreatedAt();
            this.expiresAt = entity.getExpiresAt();
            this.lastActivityAt = entity.getLastActivityAt();
            this.isActive = entity.getExpiresAt().isAfter(LocalDateTime.now());
        }
    }
}
