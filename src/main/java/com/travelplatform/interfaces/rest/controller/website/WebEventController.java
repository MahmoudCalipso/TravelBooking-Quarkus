package com.travelplatform.interfaces.rest.controller.website;

import com.travelplatform.application.dto.response.common.ErrorResponse;
import com.travelplatform.application.dto.response.common.PageResponse;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.event.EventResponse;
import com.travelplatform.application.service.event.EventService;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.UserRole;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for event operations.
 * Handles event creation, management, and registration.
 */
@Path("/api/v1/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Events", description = "Event management")
public class WebEventController {

        private static final Logger log = LoggerFactory.getLogger(EventController.class);

        @Inject
        private EventService eventService;

        /**
         * Browse events.
         *
         * @param type     The event type filter
         * @param status   The event status filter
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of events
         */
        @GET
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Browse events", description = "Browse events with filters")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Events retrieved successfully")
        })
        public Response browseEvents(
                        @QueryParam("type") String type,
                        @QueryParam("status") String status,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Browse events request - type: {}, status: {}", type, status);

                        List<EventResponse> events;
                        ApprovalStatus statusFilter = parseStatus(status);
                        if (statusFilter != null && type != null && !type.isBlank()) {
                                events = eventService.getEventsByStatus(statusFilter, page, pageSize).stream()
                                                .filter(event -> type.equalsIgnoreCase(event.getEventType()))
                                                .toList();
                        } else if (statusFilter != null) {
                                events = eventService.getEventsByStatus(statusFilter, page, pageSize);
                        } else if (type != null && !type.isBlank()) {
                                events = eventService.getEventsByType(type, page, pageSize);
                        } else {
                                events = eventService.getEventsByStatus(ApprovalStatus.APPROVED, page, pageSize);
                        }

                        PageResponse<EventResponse> response = buildPageResponse(events, page, pageSize);

                        return Response.ok()
                                        .entity(response)
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error browsing events", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get upcoming events.
         *
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of upcoming events
         */
        @GET
        @Path("/upcoming")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get upcoming events", description = "Get upcoming events")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Upcoming events retrieved successfully")
        })
        public Response getUpcomingEvents(
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get upcoming events request");

                        List<EventResponse> events = eventService.getUpcomingEvents(page, pageSize);
                        PageResponse<EventResponse> response = buildPageResponse(events, page, pageSize);

                        return Response.ok()
                                        .entity(response)
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error getting upcoming events", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get event by ID.
         *
         * @param eventId The event ID
         * @return Event response
         */
        @GET
        @Path("/{eventId}")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get event by ID", description = "Get event details by ID")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Event retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response getEventById(@PathParam("eventId") UUID eventId) {
                try {
                        log.info("Get event by ID request: {}", eventId);

                        EventResponse event = eventService.getEventById(eventId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(event, "Event retrieved successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event not found: {}", e.getMessage());
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(new ErrorResponse("EVENT_NOT_FOUND", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error getting event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Create event.
         *
         * @param securityContext The security context
         * @param title           The event title
         * @param description     The event description
         * @param eventType       The event type
         * @param locationName    The location name
         * @param latitude        The latitude
         * @param longitude       The longitude
         * @param startDate       The start date
         * @param endDate         The end date
         * @param pricePerPerson  The price per person
         * @param maxParticipants The maximum participants
         * @return Created event response
         */
        @POST
        @Authorized(roles = { UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
        @Operation(summary = "Create event", description = "Create a new event")
        @APIResponses(value = {
                        @APIResponse(responseCode = "201", description = "Event created successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions")
        })
        public Response createEvent(
                        @Context SecurityContext securityContext,
                        @FormParam("title") String title,
                        @FormParam("description") String description,
                        @FormParam("eventType") String eventType,
                        @FormParam("locationName") String locationName,
                        @FormParam("latitude") Double latitude,
                        @FormParam("longitude") Double longitude,
                        @FormParam("startDate") LocalDateTime startDate,
                        @FormParam("endDate") LocalDateTime endDate,
                        @FormParam("pricePerPerson") Double pricePerPerson,
                        @FormParam("currency") String currency,
                        @FormParam("maxParticipants") Integer maxParticipants) {
                try {
                        String creatorId = securityContext.getUserPrincipal().getName();
                        log.info("Create event request by user: {}", creatorId);

                        BigDecimal price = pricePerPerson != null ? BigDecimal.valueOf(pricePerPerson)
                                        : BigDecimal.ZERO;
                        String resolvedCurrency = (currency == null || currency.isBlank()) ? "USD" : currency;
                        EventResponse event = eventService.createEvent(
                                        UUID.fromString(creatorId), title, description, eventType,
                                        locationName, latitude, longitude, startDate, endDate,
                                        price, resolvedCurrency, maxParticipants);

                        return Response.status(Response.Status.CREATED)
                                        .entity(new SuccessResponse<>(event, "Event created successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event creation failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error creating event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Update event.
         *
         * @param securityContext The security context
         * @param eventId         The event ID
         * @param title           The event title
         * @param description     The event description
         * @param eventType       The event type
         * @param locationName    The location name
         * @param latitude        The latitude
         * @param longitude       The longitude
         * @param startDate       The start date
         * @param endDate         The end date
         * @param pricePerPerson  The price per person
         * @param maxParticipants The maximum participants
         * @return Updated event response
         */
        @PUT
        @Path("/{eventId}")
        @Authorized(roles = { UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
        @Operation(summary = "Update event", description = "Update an event")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Event updated successfully"),
                        @APIResponse(responseCode = "400", description = "Invalid input"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response updateEvent(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId,
                        @FormParam("title") String title,
                        @FormParam("description") String description,
                        @FormParam("eventType") String eventType,
                        @FormParam("locationName") String locationName,
                        @FormParam("latitude") Double latitude,
                        @FormParam("longitude") Double longitude,
                        @FormParam("startDate") LocalDateTime startDate,
                        @FormParam("endDate") LocalDateTime endDate,
                        @FormParam("pricePerPerson") Double pricePerPerson,
                        @FormParam("maxParticipants") Integer maxParticipants) {
                try {
                        String creatorId = securityContext.getUserPrincipal().getName();
                        log.info("Update event request: {} by user: {}", eventId, creatorId);

                        BigDecimal price = pricePerPerson != null ? BigDecimal.valueOf(pricePerPerson) : null;
                        EventResponse event = eventService.updateEvent(
                                        UUID.fromString(creatorId), eventId, title, description,
                                        locationName, startDate, endDate,
                                        price, maxParticipants);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(event, "Event updated successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event update failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("VALIDATION_ERROR", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error updating event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Delete event.
         *
         * @param securityContext The security context
         * @param eventId         The event ID
         * @return Success response
         */
        @DELETE
        @Path("/{eventId}")
        @Authorized(roles = { UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
        @Operation(summary = "Delete event", description = "Delete an event")
        @APIResponses(value = {
                        @APIResponse(responseCode = "204", description = "Event deleted successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response deleteEvent(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId) {
                try {
                        String creatorId = securityContext.getUserPrincipal().getName();
                        log.info("Delete event request: {} by user: {}", eventId, creatorId);

                        eventService.deleteEvent(UUID.fromString(creatorId), eventId);

                        return Response.noContent().build();

                } catch (IllegalArgumentException e) {
                        log.error("Event deletion failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("DELETION_FAILED", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error deleting event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Cancel event.
         *
         * @param securityContext The security context
         * @param eventId         The event ID
         * @return Success response
         */
        @PUT
        @Path("/{eventId}/cancel")
        @Authorized(roles = { UserRole.SUPPLIER_SUBSCRIBER, UserRole.ASSOCIATION_MANAGER })
        @Operation(summary = "Cancel event", description = "Cancel an event")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Event cancelled successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response cancelEvent(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId) {
                try {
                        String creatorId = securityContext.getUserPrincipal().getName();
                        log.info("Cancel event request: {} by user: {}", eventId, creatorId);

                        eventService.cancelEvent(UUID.fromString(creatorId), eventId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Event cancelled successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event cancellation failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("CANCELLATION_FAILED", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error cancelling event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Register for event.
         *
         * @param securityContext The security context
         * @param eventId         The event ID
         * @return Success response
         */
        @POST
        @Path("/{eventId}/register")
        @Authorized(roles = { UserRole.TRAVELER })
        @Operation(summary = "Register for event", description = "Register for an event")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Registration successful"),
                        @APIResponse(responseCode = "400", description = "Registration failed"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response registerForEvent(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Register for event request: {} by user: {}", eventId, userId);

                        eventService.registerForEvent(UUID.fromString(userId), eventId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Registration successful"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event registration failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("REGISTRATION_FAILED", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error registering for event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Cancel event registration.
         *
         * @param securityContext The security context
         * @param eventId         The event ID
         * @return Success response
         */
        @DELETE
        @Path("/{eventId}/registration")
        @Authorized
        @Operation(summary = "Cancel event registration", description = "Cancel event registration")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Registration cancelled successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response cancelEventRegistration(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Cancel event registration request: {} by user: {}", eventId, userId);

                        eventService.cancelRegistration(UUID.fromString(userId), eventId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Registration cancelled successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event registration cancellation failed: {}", e.getMessage());
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(new ErrorResponse("CANCELLATION_FAILED", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error cancelling event registration", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get event participants.
         *
         * @param eventId  The event ID
         * @param page     The page number
         * @param pageSize The page size
         * @return Paginated list of participants
         */
        @GET
        @Path("/{eventId}/participants")
        @Authorized(allowAnonymous = true)
        @Operation(summary = "Get event participants", description = "Get participants for an event")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Participants retrieved successfully"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response getEventParticipants(
                        @PathParam("eventId") UUID eventId,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        log.info("Get event participants request for event: {}", eventId);

                        var participants = eventService.getEventParticipants(eventId, page, pageSize);

                        return Response.ok()
                                        .entity(participants)
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error getting event participants", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Get user registered events.
         *
         * @param securityContext The security context
         * @param page            The page number
         * @param pageSize        The page size
         * @return Paginated list of events
         */
        @GET
        @Path("/my-events")
        @Authorized
        @Operation(summary = "Get my registered events", description = "Get events user is registered for")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Events retrieved successfully"),
                        @APIResponse(responseCode = "401", description = "Not authenticated")
        })
        public Response getMyRegisteredEvents(
                        @Context SecurityContext securityContext,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("pageSize") @DefaultValue("20") int pageSize) {
                try {
                        String userId = securityContext.getUserPrincipal().getName();
                        log.info("Get my registered events request for user: {}", userId);

                        List<EventResponse> events = eventService.getUserRegisteredEvents(
                                        UUID.fromString(userId), page, pageSize);
                        PageResponse<EventResponse> response = buildPageResponse(events, page, pageSize);

                        return Response.ok()
                                        .entity(response)
                                        .build();

                } catch (Exception e) {
                        log.error("Unexpected error getting my registered events", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Approve event (admin only).
         *
         * @param eventId The event ID
         * @return Success response
         */
        @PUT
        @Path("/{eventId}/approve")
        @Authorized(roles = { UserRole.SUPER_ADMIN })
        @Operation(summary = "Approve event", description = "Approve an event (admin only)")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Event approved successfully"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response approveEvent(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId) {
                try {
                        log.info("Approve event request: {}", eventId);

                        String adminId = securityContext.getUserPrincipal().getName();
                        eventService.approveEvent(UUID.fromString(adminId), eventId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Event approved successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event approval failed: {}", e.getMessage());
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(new ErrorResponse("EVENT_NOT_FOUND", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error approving event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        /**
         * Reject event (admin only).
         *
         * @param eventId The event ID
         * @param reason  The rejection reason
         * @return Success response
         */
        @PUT
        @Path("/{eventId}/reject")
        @Authorized(roles = { UserRole.SUPER_ADMIN })
        @Operation(summary = "Reject event", description = "Reject an event (admin only)")
        @APIResponses(value = {
                        @APIResponse(responseCode = "200", description = "Event rejected successfully"),
                        @APIResponse(responseCode = "403", description = "Insufficient permissions"),
                        @APIResponse(responseCode = "404", description = "Event not found")
        })
        public Response rejectEvent(
                        @Context SecurityContext securityContext,
                        @PathParam("eventId") UUID eventId,
                        @FormParam("reason") String reason) {
                try {
                        log.info("Reject event request: {}", eventId);

                        String adminId = securityContext.getUserPrincipal().getName();
                        eventService.rejectEvent(UUID.fromString(adminId), eventId);

                        return Response.ok()
                                        .entity(new SuccessResponse<>(null, "Event rejected successfully"))
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.error("Event rejection failed: {}", e.getMessage());
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(new ErrorResponse("EVENT_NOT_FOUND", e.getMessage()))
                                        .build();
                } catch (Exception e) {
                        log.error("Unexpected error rejecting event", e);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                        .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
                                        .build();
                }
        }

        private ApprovalStatus parseStatus(String status) {
                if (status == null || status.isBlank()) {
                        return null;
                }
                return ApprovalStatus.valueOf(status.toUpperCase());
        }

        private PageResponse<EventResponse> buildPageResponse(List<EventResponse> data, int page, int pageSize) {
                PageResponse.PaginationInfo pagination = new PageResponse.PaginationInfo(page, pageSize,
                                (long) data.size());
                return new PageResponse<>(data, pagination);
        }
}