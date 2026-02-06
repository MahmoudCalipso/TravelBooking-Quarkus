package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.repository.EventRepository;
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

/**
 * Admin controller for event approval and oversight.
 */
@Path("/api/v1/admin/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Event Management", description = "SUPER_ADMIN endpoints for managing events")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminEventController {

    private static final Logger logger = LoggerFactory.getLogger(AdminEventController.class);

    @Inject
    EventRepository eventRepository;

    @Inject
    AuditService auditService;

    /**
     * List all events with filters.
     */
    @GET
    @Operation(summary = "List all events", description = "Get all events with filters")
    public PaginatedResponse<Event> listEvents(
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing events: status={}, page={}", status, page);

        List<Event> events = eventRepository.findAll(); // Simple catch-all for now
        long totalCount = events.size();

        int from = Math.max(0, page * size);
        int to = Math.min(events.size(), from + size);
        List<Event> paginated = from < to ? events.subList(from, to) : List.of();

        return PaginatedResponse.of(paginated, totalCount, page, size);
    }

    /**
     * Approve event.
     */
    @POST
    @Path("/{id}/approve")
    @Transactional
    @Operation(summary = "Approve event", description = "Make event visible to users")
    public BaseResponse<Void> approveEvent(@PathParam("id") UUID eventId) {
        logger.info("Admin approving event: eventId={}", eventId);

        Event event = eventRepository.findByIdOptional(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        event.setStatus("APPROVED");
        eventRepository.update(event);

        auditService.logAction("EVENT_APPROVED", "Event", eventId, Map.of());

        return BaseResponse.success("Event approved successfully");
    }

    /**
     * Reject event.
     */
    @POST
    @Path("/{id}/reject")
    @Transactional
    @Operation(summary = "Reject event", description = "Deny event publication")
    public BaseResponse<Void> rejectEvent(@PathParam("id") UUID eventId, ActionRequest request) {
        logger.info("Admin rejecting event: eventId={}, reason={}", eventId, request.reason);

        Event event = eventRepository.findByIdOptional(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        event.setStatus("REJECTED");
        eventRepository.update(event);

        auditService.logAction("EVENT_REJECTED", "Event", eventId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("Event rejected successfully");
    }

    /**
     * Force cancel event.
     */
    @POST
    @Path("/{id}/cancel")
    @Transactional
    @Operation(summary = "Cancel event", description = "Admin force cancellation of event")
    public BaseResponse<Void> cancelEvent(@PathParam("id") UUID eventId, ActionRequest request) {
        logger.info("Admin cancelling event: eventId={}, reason={}", eventId, request.reason);

        Event event = eventRepository.findByIdOptional(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        event.setStatus("CANCELLED");
        eventRepository.update(event);

        auditService.logAction("EVENT_CANCELLED", "Event", eventId,
                Map.of("reason", request.reason != null ? request.reason : ""));

        return BaseResponse.success("Event cancelled successfully");
    }

    /**
     * View event participant list.
     */
    @GET
    @Path("/{id}/participants")
    @Operation(summary = "View participants", description = "Get list of users participating in event")
    public BaseResponse<List<Map<String, Object>>> getParticipants(@PathParam("id") UUID eventId) {
        logger.info("Admin viewing participants for event: eventId={}", eventId);

        // This would normally call a dedicated participant repository
        return BaseResponse.success(List.of());
    }

    public static class ActionRequest {
        public String reason;
    }
}