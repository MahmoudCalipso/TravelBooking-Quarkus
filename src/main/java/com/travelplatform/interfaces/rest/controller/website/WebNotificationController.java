package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.notification.NotificationResponse;
import com.travelplatform.application.service.notification.NotificationService;
import com.travelplatform.domain.enums.NotificationType;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
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

import java.util.UUID;

/**
 * REST controller for notification operations.
 * Handles user notifications.
 */
@Path("/api/v1/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Notifications", description = "Notification management")
public class WebNotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Inject
    private NotificationService notificationService;

    /**
     * Get notifications.
     *
     * @param securityContext The security context
     * @param type            The notification type filter
     * @param isRead          The read status filter
     * @param page            The page number
     * @param pageSize        The page size
     * @return Paginated list of notifications
     */
    @GET
    @Authorized
    @Operation(summary = "Get notifications", description = "Get current user's notifications")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getNotifications(
            @Context SecurityContext securityContext,
            @QueryParam("type") String type,
            @QueryParam("isRead") Boolean isRead,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get notifications request for user: {}", userId);

            UUID userUuid = UUID.fromString(userId);
            java.util.List<NotificationResponse> notifications;
            NotificationType typeFilter = parseType(type);

            if (typeFilter != null) {
                notifications = notificationService.getNotificationsByType(userUuid, typeFilter, page, pageSize);
            } else if (isRead != null && !isRead) {
                notifications = notificationService.getUnreadNotifications(userUuid, page, pageSize);
            } else {
                notifications = notificationService.getUserNotifications(userUuid, page, pageSize);
                if (isRead != null) {
                    notifications = notifications.stream()
                            .filter(n -> isRead.equals(n.getIsRead()))
                            .toList();
                }
            }

            PageResponse<NotificationResponse> response = buildPageResponse(notifications, page, pageSize);

            return Response.ok()
                    .entity(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid notification type: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("INVALID_TYPE", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error getting notifications", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Get unread count.
     *
     * @param securityContext The security context
     * @return Unread count response
     */
    @GET
    @Path("/unread-count")
    @Authorized
    @Operation(summary = "Get unread count", description = "Get current user's unread notification count")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Unread count retrieved successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response getUnreadCount(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Get unread count request for user: {}", userId);

            int unreadCount = notificationService.getUnreadCount(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(unreadCount, "Unread count retrieved successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error getting unread count", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Mark notification as read.
     *
     * @param securityContext The security context
     * @param notificationId  The notification ID
     * @return Success response
     */
    @PUT
    @Path("/{notificationId}/read")
    @Authorized
    @Operation(summary = "Mark notification as read", description = "Mark a notification as read")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "404", description = "Notification not found")
    })
    public Response markAsRead(
            @Context SecurityContext securityContext,
            @PathParam("notificationId") UUID notificationId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Mark notification as read request: {} by user: {}", notificationId, userId);

            notificationService.markAsRead(UUID.fromString(userId), notificationId);

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "Notification marked as read successfully"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Notification not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("NOTIFICATION_NOT_FOUND", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error marking notification as read", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Mark all notifications as read.
     *
     * @param securityContext The security context
     * @return Success response
     */
    @PUT
    @Path("/read-all")
    @Authorized
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "All notifications marked as read successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated")
    })
    public Response markAllAsRead(@Context SecurityContext securityContext) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Mark all notifications as read request for user: {}", userId);

            notificationService.markAllAsRead(UUID.fromString(userId));

            return Response.ok()
                    .entity(new SuccessResponse<>(null, "All notifications marked as read successfully"))
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error marking all notifications as read", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    /**
     * Delete notification.
     *
     * @param securityContext The security context
     * @param notificationId  The notification ID
     * @return Success response
     */
    @DELETE
    @Path("/{notificationId}")
    @Authorized
    @Operation(summary = "Delete notification", description = "Delete a notification")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Notification deleted successfully"),
            @APIResponse(responseCode = "401", description = "Not authenticated"),
            @APIResponse(responseCode = "404", description = "Notification not found")
    })
    public Response deleteNotification(
            @Context SecurityContext securityContext,
            @PathParam("notificationId") UUID notificationId) {
        try {
            String userId = securityContext.getUserPrincipal().getName();
            log.info("Delete notification request: {} by user: {}", notificationId, userId);

            notificationService.deleteNotification(UUID.fromString(userId), notificationId);

            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            log.error("Notification deletion failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("DELETION_FAILED", e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error deleting notification", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }

    private NotificationType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        return NotificationType.valueOf(type.toUpperCase());
    }

    private PageResponse<NotificationResponse> buildPageResponse(
            java.util.List<NotificationResponse> data, int page, int pageSize) {
        PageResponse.PaginationInfo pagination = new PageResponse.PaginationInfo(page, pageSize, (long) data.size());
        return new PageResponse<>(data, pagination);
    }
}