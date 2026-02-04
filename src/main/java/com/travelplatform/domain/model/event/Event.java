package com.travelplatform.domain.model.event;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing an event.
 * This is the aggregate root for the event aggregate.
 */
public class Event {
    private final UUID id;
    private final UUID creatorId;
    private String title;
    private String description;
    private final EventType eventType;
    private String locationName;
    private Location location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Money pricePerPerson;
    private final String currency;
    private Integer maxParticipants;
    private int currentParticipants;
    private ApprovalStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private UUID approvedBy;

    /**
     * Event type enumeration.
     */
    public enum EventType {
        TOUR,
        WORKSHOP,
        FESTIVAL,
        ACTIVITY,
        EXCURSION,
        MEETUP,
        WEBINAR,
        OTHER
    }

    /**
     * Creates a new Event.
     *
     * @param creatorId       user ID who created the event
     * @param title           event title
     * @param description     event description
     * @param eventType       type of event
     * @param locationName    location name
     * @param location        GPS location
     * @param startDate       event start date and time
     * @param endDate         event end date and time
     * @param pricePerPerson  price per person
     * @param currency        currency code
     * @param maxParticipants maximum number of participants
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Event(UUID creatorId, String title, String description, EventType eventType, String locationName,
            Location location, LocalDateTime startDate, LocalDateTime endDate, Money pricePerPerson,
            String currency, Integer maxParticipants) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (locationName == null || locationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or empty");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (pricePerPerson == null) {
            throw new IllegalArgumentException("Price per person cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (maxParticipants != null && maxParticipants <= 0) {
            throw new IllegalArgumentException("Max participants must be positive");
        }

        this.id = UUID.randomUUID();
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.locationName = locationName;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricePerPerson = pricePerPerson;
        this.currency = currency;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.status = ApprovalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvedAt = null;
        this.approvedBy = null;
    }

    /**
     * Reconstructs an Event from persistence.
     */
    public Event(UUID id, UUID creatorId, String title, String description, EventType eventType, String locationName,
            Location location, LocalDateTime startDate, LocalDateTime endDate, Money pricePerPerson,
            String currency, Integer maxParticipants, int currentParticipants, ApprovalStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime approvedAt, UUID approvedBy) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.locationName = locationName;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricePerPerson = pricePerPerson;
        this.currency = currency;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getLocationName() {
        return locationName;
    }

    public Location getLocation() {
        return location;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Money getPricePerPerson() {
        return pricePerPerson;
    }

    public String getCurrency() {
        return currency;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setPricePerPerson(Money pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * Updates the event details.
     *
     * @param title           new title
     * @param description     new description
     * @param locationName    new location name
     * @param location        new location
     * @param startDate       new start date
     * @param endDate         new end date
     * @param pricePerPerson  new price per person
     * @param maxParticipants new max participants
     */
    public void update(String title, String description, String locationName, Location location,
            LocalDateTime startDate, LocalDateTime endDate, Money pricePerPerson, Integer maxParticipants) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (locationName == null || locationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or empty");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (pricePerPerson == null) {
            throw new IllegalArgumentException("Price per person cannot be null");
        }
        if (maxParticipants != null && maxParticipants <= 0) {
            throw new IllegalArgumentException("Max participants must be positive");
        }

        this.title = title;
        this.description = description;
        this.locationName = locationName;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pricePerPerson = pricePerPerson;
        this.maxParticipants = maxParticipants;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approves the event.
     *
     * @param approvedBy user ID who approved
     */
    public void approve(UUID approvedBy) {
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approved by cannot be null");
        }
        this.status = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rejects the event.
     */
    public void reject() {
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(UUID rejectedBy) {
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
        // optionally usage of rejectedBy
    }

    /**
     * Flags the event for review.
     */
    public void flag() {
        this.status = ApprovalStatus.FLAGGED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancels the event.
     */
    public void cancel() {
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the participant count.
     */
    public void incrementParticipants() {
        if (maxParticipants != null && currentParticipants >= maxParticipants) {
            throw new IllegalStateException("Event is full");
        }
        this.currentParticipants++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Decrements the participant count.
     */
    public void decrementParticipants() {
        if (currentParticipants > 0) {
            this.currentParticipants--;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void incrementParticipantCount() {
        incrementParticipants();
    }

    public void decrementParticipantCount() {
        decrementParticipants();
    }

    /**
     * Checks if the event is approved.
     *
     * @return true if status is APPROVED
     */
    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    /**
     * Checks if the event is pending.
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    /**
     * Checks if the event is rejected.
     *
     * @return true if status is REJECTED
     */
    public boolean isRejected() {
        return this.status == ApprovalStatus.REJECTED;
    }

    /**
     * Checks if the event is flagged.
     *
     * @return true if status is FLAGGED
     */
    public boolean isFlagged() {
        return this.status == ApprovalStatus.FLAGGED;
    }

    /**
     * Checks if the event is full.
     *
     * @return true if max participants reached
     */
    public boolean isFull() {
        return maxParticipants != null && currentParticipants >= maxParticipants;
    }

    /**
     * Checks if the event is in the future.
     *
     * @return true if start date is in the future
     */
    public boolean isInFuture() {
        return startDate.isAfter(LocalDateTime.now());
    }

    /**
     * Checks if the event is in the past.
     *
     * @return true if end date is in the past
     */
    public boolean isInPast() {
        return endDate.isBefore(LocalDateTime.now());
    }

    /**
     * Checks if the event is currently ongoing.
     *
     * @return true if event is happening now
     */
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return !startDate.isAfter(now) && !endDate.isBefore(now);
    }

    /**
     * Gets the number of available spots.
     *
     * @return available spots or null if unlimited
     */
    public Integer getAvailableSpots() {
        if (maxParticipants == null) {
            return null;
        }
        return Math.max(0, maxParticipants - currentParticipants);
    }

    /**
     * Checks if there are available spots.
     *
     * @return true if spots are available
     */
    public boolean hasAvailableSpots() {
        if (maxParticipants == null) {
            return true;
        }
        return currentParticipants < maxParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event that = (Event) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Event{id=%s, creatorId=%s, title=%s, eventType=%s, status=%s}",
                id, creatorId, title, eventType, status);
    }
}
