package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.model.event.EventParticipant;
import com.travelplatform.domain.enums.ApprovalStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Event aggregate.
 * Defines the contract for event data access operations.
 */
public interface EventRepository {

    /**
     * Saves a new event.
     *
     * @param event event to save
     * @return saved event
     */
    Event save(Event event);

    /**
     * Updates an existing event.
     *
     * @param event event to update
     * @return updated event
     */
    Event update(Event event);

    /**
     * Deletes an event by ID.
     *
     * @param id event ID
     */
    void deleteById(UUID id);

    /**
     * Finds an event by ID.
     *
     * @param id event ID
     * @return optional event
     */
    Optional<Event> findById(UUID id);

    /**
     * Finds all events.
     *
     * @return list of all events
     */
    List<Event> findAll();

    /**
     * Finds events by creator ID.
     *
     * @param creatorId creator user ID
     * @return list of events by creator
     */
    List<Event> findByCreatorId(UUID creatorId);

    /**
     * Finds events by status.
     *
     * @param status approval status
     * @return list of events with the status
     */
    List<Event> findByStatus(ApprovalStatus status);

    /**
     * Finds events by event type.
     *
     * @param eventType event type (TOUR, WORKSHOP, FESTIVAL, ACTIVITY)
     * @return list of events with the type
     */
    List<Event> findByEventType(String eventType);

    /**
     * Finds events by creator ID with pagination.
     *
     * @param creatorId creator user ID
     * @param page      page number (0-indexed)
     * @param pageSize  page size
     * @return list of events
     */
    List<Event> findByCreatorIdPaginated(UUID creatorId, int page, int pageSize);

    /**
     * Finds events by status with pagination.
     *
     * @param status   approval status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of events
     */
    List<Event> findByStatusPaginated(ApprovalStatus status, int page, int pageSize);

    /**
     * Finds events by creator and status.
     *
     * @param creatorId creator user ID
     * @param status    approval status
     * @return list of events
     */
    List<Event> findByCreatorIdAndStatus(UUID creatorId, ApprovalStatus status);

    /**
     * Finds upcoming events (start date in the future).
     *
     * @return list of upcoming events
     */
    List<Event> findUpcoming();

    /**
     * Finds past events (end date in the past).
     *
     * @return list of past events
     */
    List<Event> findPast();

    /**
     * Finds ongoing events (currently happening).
     *
     * @return list of ongoing events
     */
    List<Event> findOngoing();

    /**
     * Finds events starting after a date.
     *
     * @param date start date threshold
     * @return list of events starting after the date
     */
    List<Event> findByStartDateAfter(LocalDateTime date);

    /**
     * Finds events ending before a date.
     *
     * @param date end date threshold
     * @return list of events ending before the date
     */
    List<Event> findByEndDateBefore(LocalDateTime date);

    /**
     * Finds events between dates.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of events
     */
    List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds events by location name.
     *
     * @param locationName location name
     * @return list of events at the location
     */
    List<Event> findByLocationName(String locationName);

    /**
     * Finds events by price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of events within price range
     */
    List<Event> findByPriceRange(double minPrice, double maxPrice);

    /**
     * Finds free events (price = 0 or null).
     *
     * @return list of free events
     */
    List<Event> findFree();

    /**
     * Finds paid events (price > 0).
     *
     * @return list of paid events
     */
    List<Event> findPaid();

    /**
     * Finds events with available spots.
     *
     * @return list of events with available spots
     */
    List<Event> findWithAvailableSpots();

    /**
     * Finds events that are fully booked.
     *
     * @return list of fully booked events
     */
    List<Event> findFullyBooked();

    /**
     * Counts events by creator.
     *
     * @param creatorId creator user ID
     * @return count of events by creator
     */
    long countByCreatorId(UUID creatorId);

    /**
     * Counts events by status.
     *
     * @param status approval status
     * @return count of events with the status
     */
    long countByStatus(ApprovalStatus status);

    /**
     * Counts all events.
     *
     * @return total count of events
     */
    long countAll();

    /**
     * Searches events by keyword.
     *
     * @param keyword search term
     * @return list of matching events
     */
    List<Event> searchByKeyword(String keyword);

    /**
     * Finds events sorted by start date.
     *
     * @param limit maximum number of results
     * @return list of events
     */
    List<Event> findUpcomingSortedByStartDate(int limit);

    /**
     * Finds events sorted by participant count.
     *
     * @param limit maximum number of results
     * @return list of most popular events
     */
    List<Event> findMostPopular(int limit);

    /**
     * Finds events by currency.
     *
     * @param currency currency code
     * @return list of events with the currency
     */
    List<Event> findByCurrency(String currency);

    /**
     * Finds event participants by event ID.
     *
     * @param eventId event ID
     * @return list of participants
     */
    List<EventParticipant> findParticipantsByEventId(UUID eventId);

    /**
     * Finds event participants by user ID.
     *
     * @param userId user ID
     * @return list of events the user is participating in
     */
    List<EventParticipant> findParticipantsByUserId(UUID userId);

    /**
     * Finds event participant by event ID and user ID.
     *
     * @param eventId event ID
     * @param userId  user ID
     * @return optional participant
     */
    Optional<EventParticipant> findParticipantByEventIdAndUserId(UUID eventId, UUID userId);

    /**
     * Counts participants for an event.
     *
     * @param eventId event ID
     * @return count of participants
     */
    long countParticipantsByEventId(UUID eventId);

    /**
     * Finds events by user participation.
     *
     * @param userId user ID
     * @return list of events the user is participating in
     */
    List<Event> findEventsByUserId(UUID userId);

    /**
     * Finds upcoming events by user participation.
     *
     * @param userId user ID
     * @return list of upcoming events the user is participating in
     */
    List<Event> findUpcomingEventsByUserId(UUID userId);

    /**
     * Finds past events by user participation.
     *
     * @param userId user ID
     * @return list of past events the user participated in
     */
    List<Event> findPastEventsByUserId(UUID userId);

    /**
     * Finds events by creator with pagination.
     *
     * @param creatorId creator user ID
     * @param page      page number (0-indexed)
     * @param pageSize  page size
     * @return list of events
     */

    /**
     * Finds events by event type with pagination.
     *
     * @param eventType event type
     * @param page      page number (0-indexed)
     * @param pageSize  page size
     * @return list of events
     */
    List<Event> findByEventTypePaginated(String eventType, int page, int pageSize);

    /**
     * Finds events by location name with pagination.
     *
     * @param locationName location name
     * @param page         page number (0-indexed)
     * @param pageSize     page size
     * @return list of events
     */
    List<Event> findByLocationNamePaginated(String locationName, int page, int pageSize);

    /**
     * Finds events by price range with pagination.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of events
     */
    List<Event> findByPriceRangePaginated(double minPrice, double maxPrice, int page, int pageSize);

    /**
     * Finds events by status and event type.
     *
     * @param status    approval status
     * @param eventType event type
     * @return list of events
     */
    List<Event> findByStatusAndEventType(ApprovalStatus status, String eventType);

    /**
     * Finds events by status and location.
     *
     * @param status       approval status
     * @param locationName location name
     * @return list of events
     */
    List<Event> findByStatusAndLocationName(ApprovalStatus status, String locationName);

    /**
     * Finds events by status and date range.
     *
     * @param status    approval status
     * @param startDate start date
     * @param endDate   end date
     * @return list of events
     */
    List<Event> findByStatusAndDateRange(ApprovalStatus status, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds events by creator and date range.
     *
     * @param creatorId creator user ID
     * @param startDate start date
     * @param endDate   end date
     * @return list of events
     */
    List<Event> findByCreatorIdAndDateRange(UUID creatorId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds events sorted by price (ascending).
     *
     * @param limit maximum number of results
     * @return list of events
     */
    List<Event> findCheapest(int limit);

    /**
     * Finds events sorted by price (descending).
     *
     * @param limit maximum number of results
     * @return list of events
     */
    List<Event> findMostExpensive(int limit);

    /**
     * Finds events by creator and status with pagination.
     *
     * @param creatorId creator user ID
     * @param status    approval status
     * @param page      page number (0-indexed)
     * @param pageSize  page size
     * @return list of events
     */
    List<Event> findByCreatorIdAndStatusPaginated(UUID creatorId, ApprovalStatus status, int page, int pageSize);

    /**
     * Finds events by status and pagination.
     *
     * @param status   approval status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of events
     */
    // Already defined above as findByStatusPaginated

    /**
     * Finds events within a location radius.
     */
    List<Event> findByLocation(double latitude, double longitude, double radiusKm, int page, int pageSize);

    /**
     * Checks if user is a participant.
     */
    boolean isParticipant(UUID userId, UUID eventId);

    /**
     * Saves a participant.
     */
    void saveParticipant(EventParticipant participant);

    /**
     * Finds a participant.
     */
    Optional<EventParticipant> findParticipant(UUID userId, UUID eventId);

    /**
     * Deletes a participant.
     */
    void deleteParticipant(UUID participantId);

    /**
     * Finds participants by event.
     */
    List<EventParticipant> findParticipantsByEvent(UUID eventId, int page, int pageSize);

    /**
     * Finds events user is registered for.
     */
    List<Event> findUserRegisteredEvents(UUID userId, int page, int pageSize);

    /**
     * Finds upcoming events with pagination.
     */
    List<Event> findUpcoming(int page, int pageSize);

}
