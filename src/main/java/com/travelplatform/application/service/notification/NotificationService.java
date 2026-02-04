package com.travelplatform.application.service.notification;

import com.travelplatform.application.dto.response.notification.NotificationResponse;
import com.travelplatform.application.mapper.NotificationMapper;
import com.travelplatform.domain.enums.NotificationType;
import com.travelplatform.domain.model.notification.Notification;
import com.travelplatform.domain.repository.NotificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Notification operations.
 * Orchestrates notification-related business workflows.
 */
@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    NotificationMapper notificationMapper;

    /**
     * Create a new notification.
     */
    @Transactional
    public UUID createNotification(UUID userId, NotificationType type, String title, String message,
            String relatedEntityType, UUID relatedEntityId, String actionUrl) {
        // Create notification
        Notification notification = new Notification(
                UUID.randomUUID(),
                userId,
                type,
                title,
                message);

        // Set optional fields
        if (relatedEntityType != null) {
            notification.setRelatedEntityType(relatedEntityType);
        }
        if (relatedEntityId != null) {
            notification.setRelatedEntityId(relatedEntityId);
        }
        if (actionUrl != null) {
            notification.setActionUrl(actionUrl);
        }

        // Save notification
        notificationRepository.save(notification);

        return notification.getId();
    }

    /**
     * Get notification by ID.
     */
    @Transactional
    public NotificationResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        return notificationMapper.toNotificationResponse(notification);
    }

    /**
     * Get user's notifications.
     */
    @Transactional
    public List<NotificationResponse> getUserNotifications(UUID userId, int page, int pageSize) {
        List<Notification> notifications = notificationRepository.findByUserIdPaginated(userId, page, pageSize);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    /**
     * Get user's unread notifications.
     */
    @Transactional
    public List<NotificationResponse> getUnreadNotifications(UUID userId, int page, int pageSize) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadPaginated(userId, false, page,
                pageSize);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    /**
     * Get user's notifications by type.
     */
    @Transactional
    public List<NotificationResponse> getNotificationsByType(UUID userId, NotificationType type, int page,
            int pageSize) {
        List<Notification> notifications = notificationRepository.findByUserIdAndTypePaginated(userId, type, page,
                pageSize);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    /**
     * Get unread notification count.
     */
    @Transactional
    public int getUnreadCount(UUID userId) {
        return Math.toIntExact(notificationRepository.countUnreadByUserId(userId));
    }

    /**
     * Mark notification as read.
     */
    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Verify ownership
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only mark your own notifications as read");
        }

        // Mark as read
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read.
     */
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    /**
     * Delete notification.
     */
    @Transactional
    public void deleteNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Verify ownership
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own notifications");
        }

        // Delete notification
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Delete old notifications (cleanup job).
     */
    @Transactional
    public void deleteOldNotifications(LocalDateTime beforeDate) {
        notificationRepository.deleteOldNotificationsBeforeDate(beforeDate);
    }

    /**
     * Create booking confirmation notification.
     */
    @Transactional
    public void createBookingConfirmationNotification(UUID userId, UUID bookingId) {
        createNotification(
                userId,
                NotificationType.BOOKING_CONFIRMED,
                "Booking Confirmed",
                "Your booking has been confirmed. Get ready for your trip!",
                "BOOKING",
                bookingId,
                "/bookings/" + bookingId);
    }

    /**
     * Create booking cancelled notification.
     */
    @Transactional
    public void createBookingCancelledNotification(UUID userId, UUID bookingId) {
        createNotification(
                userId,
                NotificationType.BOOKING_CANCELLED,
                "Booking Cancelled",
                "Your booking has been cancelled.",
                "BOOKING",
                bookingId,
                "/bookings/" + bookingId);
    }

    /**
     * Create payment received notification.
     */
    @Transactional
    public void createPaymentReceivedNotification(UUID userId, UUID bookingId) {
        createNotification(
                userId,
                NotificationType.PAYMENT_RECEIVED,
                "Payment Received",
                "Your payment has been received successfully.",
                "BOOKING",
                bookingId,
                "/bookings/" + bookingId);
    }

    /**
     * Create reel approved notification.
     */
    @Transactional
    public void createReelApprovedNotification(UUID userId, UUID reelId) {
        createNotification(
                userId,
                NotificationType.REEL_APPROVED,
                "Reel Approved",
                "Your reel has been approved and is now visible to others!",
                "REEL",
                reelId,
                "/reels/" + reelId);
    }

    /**
     * Create reel liked notification.
     */
    @Transactional
    public void createReelLikedNotification(UUID userId, UUID reelId, String likerName) {
        createNotification(
                userId,
                NotificationType.REEL_LIKED,
                "New Like",
                likerName + " liked your reel!",
                "REEL",
                reelId,
                "/reels/" + reelId);
    }

    /**
     * Create new comment notification.
     */
    @Transactional
    public void createNewCommentNotification(UUID userId, UUID reelId, String commenterName) {
        createNotification(
                userId,
                NotificationType.NEW_COMMENT,
                "New Comment",
                commenterName + " commented on your reel!",
                "REEL",
                reelId,
                "/reels/" + reelId);
    }

    /**
     * Create new review notification.
     */
    @Transactional
    public void createNewReviewNotification(UUID userId, UUID accommodationId, String reviewerName) {
        createNotification(
                userId,
                NotificationType.NEW_REVIEW,
                "New Review",
                reviewerName + " left a review on your accommodation!",
                "ACCOMMODATION",
                accommodationId,
                "/accommodations/" + accommodationId);
    }

    /**
     * Create new message notification.
     */
    @Transactional
    public void createNewMessageNotification(UUID userId, UUID conversationId, String senderName) {
        createNotification(
                userId,
                NotificationType.NEW_MESSAGE,
                "New Message",
                "You have a new message from " + senderName,
                "CONVERSATION",
                conversationId,
                "/messages/" + conversationId);
    }

    /**
     * Create event reminder notification.
     */
    @Transactional
    public void createEventReminderNotification(UUID userId, UUID eventId, String eventName, LocalDateTime eventDate) {
        createNotification(
                userId,
                NotificationType.EVENT_REMINDER,
                "Event Reminder",
                "Reminder: " + eventName + " is starting soon!",
                "EVENT",
                eventId,
                "/events/" + eventId);
    }
}
