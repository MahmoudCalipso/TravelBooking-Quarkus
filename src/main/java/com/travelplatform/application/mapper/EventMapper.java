package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.response.event.EventResponse;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.event.Event;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mapper for Event domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface EventMapper {

    // Custom mapping methods for Money to BigDecimal
    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    @Named("locationToLatitude")
    default BigDecimal locationToLatitude(Location location) {
        return location != null ? BigDecimal.valueOf(location.getLatitude()) : null;
    }

    @Named("locationToLongitude")
    default BigDecimal locationToLongitude(Location location) {
        return location != null ? BigDecimal.valueOf(location.getLongitude()) : null;
    }

    // Entity to Response DTO
    default EventResponse toEventResponse(Event event) {
        if (event == null) {
            return null;
        }
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setCreatorId(event.getCreatorId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setEventType(event.getEventType() != null ? event.getEventType().name() : null);
        response.setLocationName(event.getLocationName());
        response.setLatitude(locationToLatitude(event.getLocation()));
        response.setLongitude(locationToLongitude(event.getLocation()));
        response.setStartDate(event.getStartDate());
        response.setEndDate(event.getEndDate());
        response.setPricePerPerson(moneyToBigDecimal(event.getPricePerPerson()));
        response.setCurrency(event.getCurrency());
        response.setMaxParticipants(event.getMaxParticipants());
        response.setCurrentParticipants(event.getCurrentParticipants());
        response.setStatus(event.getStatus());
        response.setCreatedAt(event.getCreatedAt());
        response.setApprovedAt(event.getApprovedAt());
        response.setIsRegisteredByCurrentUser(false);
        return response;
    }

    java.util.List<EventResponse> toEventResponseList(java.util.List<Event> events);

    @Named("creatorId")
    default UUID mapCreatorId(Event event) {
        return event != null ? event.getCreatorId() : null;
    }

    @Named("creatorName")
    default String mapCreatorName(Event event) {
        // TODO: Entity only has creatorId - need to fetch from UserService
        return null;
    }
}
