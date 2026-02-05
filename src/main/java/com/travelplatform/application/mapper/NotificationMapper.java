package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.response.notification.NotificationResponse;
import com.travelplatform.domain.model.notification.Notification;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Notification domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface NotificationMapper {

    default NotificationResponse toNotificationResponse(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setRelatedEntityType(notification.getEntityType());
        response.setRelatedEntityId(notification.getEntityId());
        response.setActionUrl(notification.getActionUrl());
        response.setIsRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        return response;
    }

    default List<NotificationResponse> toNotificationResponseList(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }
        List<NotificationResponse> responses = new ArrayList<>(notifications.size());
        for (Notification notification : notifications) {
            responses.add(toNotificationResponse(notification));
        }
        return responses;
    }
}
