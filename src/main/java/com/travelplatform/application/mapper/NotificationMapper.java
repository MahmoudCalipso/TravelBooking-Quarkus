package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.response.notification.NotificationResponse;
import com.travelplatform.domain.enums.NotificationType;
import com.travelplatform.domain.model.notification.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

/**
 * Mapper for Notification domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface NotificationMapper {

    // Entity to Response DTO
    NotificationResponse toNotificationResponse(Notification notification);

    java.util.List<NotificationResponse> toNotificationResponseList(java.util.List<Notification> notifications);

    @Named("type")
    default NotificationType mapType(Notification notification) {
        return notification != null ? notification.getType() : null;
    }
}
