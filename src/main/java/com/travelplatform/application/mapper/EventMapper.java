package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.response.event.EventResponse;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.model.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * Mapper for Event domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface EventMapper {

    // Entity to Response DTO
    EventResponse toEventResponse(Event event);

    java.util.List<EventResponse> toEventResponseList(java.util.List<Event> events);

    @Named("creatorId")
    default UUID mapCreatorId(Event event) {
        return event != null ? event.getCreatorId() : null;
    }

    @Named("creatorName")
    default String mapCreatorName(Event event) {
        // TODO: Entity only has creatorId
        return null;
        /*
         * return event != null && event.getCreator() != null &&
         * event.getCreator().getProfile() != null
         * ? event.getCreator().getProfile().getFullName()
         * : null;
         */
    }

    @Named("creatorPhotoUrl")
    default String mapCreatorPhotoUrl(Event event) {
        // TODO: Entity only has creatorId
        return null;
        /*
         * return event != null && event.getCreator() != null &&
         * event.getCreator().getProfile() != null
         * ? event.getCreator().getProfile().getPhotoUrl()
         * : null;
         */
    }

    @Named("status")
    default ApprovalStatus mapStatus(Event event) {
        return event != null ? event.getStatus() : null;
    }
}
