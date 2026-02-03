package com.travelplatform.application.service.event;

import com.travelplatform.application.dto.response.event.EventResponse;
import com.travelplatform.application.mapper.EventMapper;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.model.event.EventParticipant;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.EventRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Event operations.
 * Orchestrates event-related business workflows.
 */
@ApplicationScoped
public class EventService {

    @Inject
    EventRepository eventRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    EventMapper eventMapper;

    /**
     * Create a new event.
     */
    @Transactional
    public EventResponse createEvent(UUID userId, String title, String description, String eventType,
            String locationName, Double latitude, Double longitude,
            LocalDateTime startDate, LocalDateTime endDate,
            BigDecimal pricePerPerson, String currency,
            Integer maxParticipants) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify user can create events
        if (user.getRole() != UserRole.SUPPLIER_SUBSCRIBER && user.getRole() != UserRole.ASSOCIATION_MANAGER) {
            throw new IllegalArgumentException("Only suppliers and association managers can create events");
        }

        // Create event
        // Create event
        Event event = new Event(
                userId,
                title,
                description,
                com.travelplatform.domain.model.event.Event.EventType.valueOf(eventType),
                locationName,
                new com.travelplatform.domain.valueobject.Location(latitude != null ? latitude : 0.0,
                        longitude != null ? longitude : 0.0),
                startDate,
                endDate,
                new com.travelplatform.domain.valueobject.Money(pricePerPerson, currency),
                currency,
                maxParticipants);

        // Save event
        eventRepository.save(event);

        return eventMapper.toEventResponse(event);
    }

    /**
     * Get event by ID.
     */
    @Transactional
    public EventResponse getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        return eventMapper.toEventResponse(event);
    }

    /**
     * Get events by creator.
     */
    @Transactional
    public List<EventResponse> getEventsByCreator(UUID creatorId, int page, int pageSize) {
        List<Event> events = eventRepository.findByCreatorIdPaginated(creatorId, page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Get events by status.
     */
    @Transactional
    public List<EventResponse> getEventsByStatus(ApprovalStatus status, int page, int pageSize) {
        List<Event> events = eventRepository.findByStatusPaginated(status, page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Get events by type.
     */
    @Transactional
    public List<EventResponse> getEventsByType(String eventType, int page, int pageSize) {
        List<Event> events = eventRepository.findByEventTypePaginated(eventType, page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Get events by location.
     */
    @Transactional
    public List<EventResponse> getEventsByLocation(double latitude, double longitude, double radiusKm, int page,
            int pageSize) {
        List<Event> events = eventRepository.findByLocation(latitude, longitude, radiusKm, page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Get upcoming events.
     */
    @Transactional
    public List<EventResponse> getUpcomingEvents(int page, int pageSize) {
        List<Event> events = eventRepository.findUpcoming(page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Update event.
     */
    @Transactional
    public EventResponse updateEvent(UUID userId, UUID eventId, String title, String description,
            String locationName, LocalDateTime startDate, LocalDateTime endDate,
            BigDecimal pricePerPerson, Integer maxParticipants) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Verify ownership
        if (!event.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own events");
        }

        // Update fields
        if (title != null) {
            event.setTitle(title);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (locationName != null) {
            event.setLocationName(locationName);
        }
        if (startDate != null) {
            event.setStartDate(startDate);
        }
        if (endDate != null) {
            event.setEndDate(endDate);
        }
        if (pricePerPerson != null) {
            event.setPricePerPerson(
                    new com.travelplatform.domain.valueobject.Money(pricePerPerson, event.getCurrency()));
        }
        if (maxParticipants != null) {
            event.setMaxParticipants(maxParticipants);
        }

        // Save updated event
        eventRepository.save(event);

        return eventMapper.toEventResponse(event);
    }

    /**
     * Delete event.
     */
    @Transactional
    public void deleteEvent(UUID userId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Verify ownership
        if (!event.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own events");
        }

        // Delete event
        eventRepository.deleteById(eventId);
    }

    /**
     * Register for event.
     */
    @Transactional
    public void registerForEvent(UUID userId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Check if event is approved
        if (event.getStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Event is not available for registration");
        }

        // Check if event is full
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            throw new IllegalArgumentException("Event is full");
        }

        // Check if user already registered
        if (eventRepository.isParticipant(userId, eventId)) {
            throw new IllegalArgumentException("You are already registered for this event");
        }

        // Create participant
        EventParticipant participant = new EventParticipant(
                UUID.randomUUID(),
                eventId,
                userId);

        // Save participant
        eventRepository.saveParticipant(participant);

        // Increment participant count
        event.incrementParticipantCount();
        eventRepository.save(event);
    }

    /**
     * Cancel registration.
     */
    @Transactional
    public void cancelRegistration(UUID userId, UUID eventId) {
        EventParticipant participant = eventRepository.findParticipant(userId, eventId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        // Delete participant
        eventRepository.deleteParticipant(participant.getId());

        // Decrement participant count
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        event.decrementParticipantCount();
        eventRepository.save(event);
    }

    /**
     * Get event participants.
     */
    @Transactional
    public List<UUID> getEventParticipants(UUID eventId, int page, int pageSize) {
        List<EventParticipant> participants = eventRepository.findParticipantsByEvent(eventId, page, pageSize);
        return participants.stream().map(EventParticipant::getUserId).toList();
    }

    /**
     * Get user's registered events.
     */
    @Transactional
    public List<EventResponse> getUserRegisteredEvents(UUID userId, int page, int pageSize) {
        List<Event> events = eventRepository.findUserRegisteredEvents(userId, page, pageSize);
        return eventMapper.toEventResponseList(events);
    }

    /**
     * Approve event (admin only).
     */
    @Transactional
    public EventResponse approveEvent(UUID adminId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Approve event
        event.approve(adminId);
        eventRepository.save(event);

        return eventMapper.toEventResponse(event);
    }

    /**
     * Reject event (admin only).
     */
    @Transactional
    public EventResponse rejectEvent(UUID adminId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Reject event
        event.reject(adminId);
        eventRepository.save(event);

        return eventMapper.toEventResponse(event);
    }

    /**
     * Cancel event (creator only).
     */
    @Transactional
    public EventResponse cancelEvent(UUID userId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Verify ownership
        if (!event.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("You can only cancel your own events");
        }

        // Cancel event
        event.cancel();
        eventRepository.save(event);

        return eventMapper.toEventResponse(event);
    }
}
