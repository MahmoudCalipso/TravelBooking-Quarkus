package com.travelplatform.infrastructure.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.travelplatform.infrastructure.persistence.entity.DeviceTokenEntity;
import com.travelplatform.infrastructure.persistence.repository.JpaDeviceTokenRepository;
import com.travelplatform.interfaces.websocket.NotificationWebSocketEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Push notification service for sending real-time notifications to mobile devices.
 * This service uses Firebase Cloud Messaging (FCM) for push notifications.
 */
@ApplicationScoped
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    @Inject
    @ConfigProperty(name = "fcm.server.key")
    private String fcmServerKey;

    @Inject
    @ConfigProperty(name = "fcm.enabled")
    private boolean fcmEnabled;

    @Inject
    FirebaseApp firebaseApp;

    @Inject
    JpaDeviceTokenRepository deviceTokenRepository;

    private final Map<UUID, List<String>> userDeviceTokens = new ConcurrentHashMap<>();

    /**
     * Send a push notification to a single device.
     *
     * @param deviceToken The device token
     * @param title The notification title
     * @param body The notification body
     * @param data Additional data payload
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendPushNotification(String deviceToken, String title, String body, Map<String, Object> data) {
        if (!fcmEnabled) {
            log.warn("FCM service is disabled. Skipping push notification to device: {}", deviceToken);
            return false;
        }

        log.info("Sending push notification to device: {}", deviceToken);

        try {
            return sendToFcm(deviceToken, title, body, data);

        } catch (Exception e) {
            log.error("Failed to send push notification to device: {}", deviceToken, e);
            return false;
        }
    }

    /**
     * Send a push notification to multiple devices.
     *
     * @param deviceTokens The list of device tokens
     * @param title The notification title
     * @param body The notification body
     * @param data Additional data payload
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendPushNotificationToMultiple(java.util.List<String> deviceTokens, String title, 
                                               String body, Map<String, Object> data) {
        if (!fcmEnabled) {
            log.warn("FCM service is disabled. Skipping push notification to {} devices", deviceTokens.size());
            return false;
        }

        log.info("Sending push notification to {} devices", deviceTokens.size());

        try {
            return sendMulticastToFcm(deviceTokens, title, body, data);

        } catch (Exception e) {
            log.error("Failed to send push notification to multiple devices", e);
            return false;
        }
    }

    /**
     * Send a booking confirmation push notification.
     *
     * @param deviceToken The device token
     * @param bookingId The booking ID
     * @param accommodationTitle The accommodation title
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendBookingConfirmation(String deviceToken, String bookingId, String accommodationTitle) {
        String title = "Booking Confirmed";
        String body = "Your booking at " + accommodationTitle + " has been confirmed!";
        
        Map<String, Object> data = Map.of(
                "type", "BOOKING_CONFIRMED",
                "bookingId", bookingId,
                "accommodationTitle", accommodationTitle,
                "action", "view_booking"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a booking cancellation push notification.
     *
     * @param deviceToken The device token
     * @param bookingId The booking ID
     * @param accommodationTitle The accommodation title
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendBookingCancellation(String deviceToken, String bookingId, String accommodationTitle) {
        String title = "Booking Cancelled";
        String body = "Your booking at " + accommodationTitle + " has been cancelled.";
        
        Map<String, Object> data = Map.of(
                "type", "BOOKING_CANCELLED",
                "bookingId", bookingId,
                "accommodationTitle", accommodationTitle,
                "action", "view_bookings"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a payment received push notification.
     *
     * @param deviceToken The device token
     * @param amount The payment amount
     * @param currency The currency code
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendPaymentReceived(String deviceToken, String amount, String currency) {
        String title = "Payment Received";
        String body = "Your payment of " + amount + " " + currency + " was successful!";
        
        Map<String, Object> data = Map.of(
                "type", "PAYMENT_RECEIVED",
                "amount", amount,
                "currency", currency,
                "action", "view_bookings"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a reel liked push notification.
     *
     * @param deviceToken The device token
     * @param reelId The reel ID
     * @param likerName The name of the user who liked the reel
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendReelLiked(String deviceToken, String reelId, String likerName) {
        String title = "New Like";
        String body = likerName + " liked your reel!";
        
        Map<String, Object> data = Map.of(
                "type", "REEL_LIKED",
                "reelId", reelId,
                "likerName", likerName,
                "action", "view_reel"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a new comment push notification.
     *
     * @param deviceToken The device token
     * @param reelId The reel ID
     * @param commenterName The name of the user who commented
     * @param comment The comment text
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendNewComment(String deviceToken, String reelId, String commenterName, String comment) {
        String title = "New Comment";
        String body = commenterName + " commented on your reel!";
        
        Map<String, Object> data = Map.of(
                "type", "NEW_COMMENT",
                "reelId", reelId,
                "commenterName", commenterName,
                "comment", comment,
                "action", "view_reel"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a new review push notification.
     *
     * @param deviceToken The device token
     * @param accommodationId The accommodation ID
     * @param accommodationTitle The accommodation title
     * @param rating The review rating
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendNewReview(String deviceToken, String accommodationId, String accommodationTitle, int rating) {
        String title = "New Review";
        String body = "Your property " + accommodationTitle + " received a " + rating + "-star review!";
        
        Map<String, Object> data = Map.of(
                "type", "NEW_REVIEW",
                "accommodationId", accommodationId,
                "accommodationTitle", accommodationTitle,
                "rating", rating,
                "action", "view_reviews"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a new message push notification.
     *
     * @param deviceToken The device token
     * @param senderName The name of the sender
     * @param message The message content
     * @param conversationId The conversation ID
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendNewMessage(String deviceToken, String senderName, String message, String conversationId) {
        String title = "New Message";
        String body = senderName + ": " + message;
        
        Map<String, Object> data = Map.of(
                "type", "NEW_MESSAGE",
                "senderName", senderName,
                "message", message,
                "conversationId", conversationId,
                "action", "view_conversation"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send an event reminder push notification.
     *
     * @param deviceToken The device token
     * @param eventId The event ID
     * @param eventTitle The event title
     * @param eventDate The event date
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendEventReminder(String deviceToken, String eventId, String eventTitle, String eventDate) {
        String title = "Event Reminder";
        String body = eventTitle + " is starting soon!";
        
        Map<String, Object> data = Map.of(
                "type", "EVENT_REMINDER",
                "eventId", eventId,
                "eventTitle", eventTitle,
                "eventDate", eventDate,
                "action", "view_event"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a reel approved push notification.
     *
     * @param deviceToken The device token
     * @param reelId The reel ID
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendReelApproved(String deviceToken, String reelId) {
        String title = "Reel Approved";
        String body = "Your reel has been approved and is now live!";
        
        Map<String, Object> data = Map.of(
                "type", "REEL_APPROVED",
                "reelId", reelId,
                "action", "view_reel"
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    /**
     * Send a custom push notification.
     *
     * @param deviceToken The device token
     * @param title The notification title
     * @param body The notification body
     * @param data Additional data payload
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendCustomNotification(String deviceToken, String title, String body, Map<String, Object> data) {
        if (!fcmEnabled) {
            log.warn("FCM service is disabled. Skipping custom push notification to device: {}", deviceToken);
            return false;
        }

        log.info("Sending custom push notification to device: {}", deviceToken);

        try {
            return sendToFcm(deviceToken, title, body, data);

        } catch (Exception e) {
            log.error("Failed to send custom push notification to device: {}", deviceToken, e);
            return false;
        }
    }

    /**
     * Register a device token for a user to enable user-targeted notifications.
     */
    public void registerDeviceToken(UUID userId, String deviceToken) {
        userDeviceTokens.compute(userId, (id, tokens) -> {
            if (tokens == null) {
                tokens = new ArrayList<>();
            }
            if (!tokens.contains(deviceToken)) {
                tokens.add(deviceToken);
            }
            return tokens;
        });
        deviceTokenRepository.findByToken(deviceToken)
                .orElseGet(() -> {
                    DeviceTokenEntity entity = new DeviceTokenEntity(UUID.randomUUID(), userId, deviceToken);
                    deviceTokenRepository.persist(entity);
                    return entity;
                });
    }

    /**
     * Remove a device token (e.g., on logout).
     */
    public void unregisterDeviceToken(UUID userId, String deviceToken) {
        userDeviceTokens.computeIfPresent(userId, (id, tokens) -> {
            tokens.remove(deviceToken);
            return tokens.isEmpty() ? null : tokens;
        });
        deviceTokenRepository.deleteByToken(deviceToken);
    }

    /**
     * Send a push notification by user id (all registered devices).
     */
    public boolean sendPushNotification(UUID userId, String title, String message) {
        List<String> tokens = userDeviceTokens.getOrDefault(userId, List.of());
        if (tokens.isEmpty()) {
            // Load from persistence if cache empty
            tokens = deviceTokenRepository.findByUserId(userId).stream()
                    .map(DeviceTokenEntity::getToken)
                    .toList();
            if (!tokens.isEmpty()) {
                userDeviceTokens.put(userId, new ArrayList<>(tokens));
            }
        }
        if (tokens.isEmpty()) {
            log.warn("No registered device tokens for user {}. Skipping push notification.", userId);
            return false;
        }
        NotificationWebSocketEndpoint.sendNotification(userId, title, message, "PUSH");
        return sendPushNotificationToMultiple(tokens, title, message, Map.of("userId", userId.toString()));
    }

    /**
     * Send a message to an FCM topic.
     */
    public boolean sendToTopic(String topic, String message) {
        if (!fcmEnabled) {
            log.warn("FCM service is disabled. Skipping topic notification to: {}", topic);
            return false;
        }
        try {
            Message fcmMessage = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder().setTitle(topic).setBody(message).build())
                    .build();
            String responseId = FirebaseMessaging.getInstance(firebaseApp).send(fcmMessage);
            log.info("Topic notification sent to {} with response {}", topic, responseId);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send topic notification to {}: {}", topic, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send a push notification to FCM.
     */
    private boolean sendToFcm(String deviceToken, String title, String body, Map<String, Object> data) {
        try {
            Message.Builder builder = Message.builder()
                    .setToken(deviceToken);

            if ((title != null && !title.isEmpty()) || (body != null && !body.isEmpty())) {
                builder.setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());
            }

            if (data != null) {
                data.forEach((k, v) -> builder.putData(k, v != null ? String.valueOf(v) : ""));
            }

            String responseId = FirebaseMessaging.getInstance(firebaseApp).send(builder.build());
            log.info("Push notification sent successfully to device: {} (response id {})", deviceToken, responseId);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to device: {}", deviceToken, e);
            return false;
        }
    }

    /**
     * Send a multicast push notification to multiple devices via FCM.
     */
    private boolean sendMulticastToFcm(java.util.List<String> deviceTokens, String title, String body, Map<String, Object> data) {
        try {
            MulticastMessage.Builder builder = MulticastMessage.builder()
                    .addAllTokens(deviceTokens);

            if ((title != null && !title.isEmpty()) || (body != null && !body.isEmpty())) {
                builder.setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());
            }
            if (data != null) {
                data.forEach((k, v) -> builder.putData(k, v != null ? String.valueOf(v) : ""));
            }

            BatchResponse response = FirebaseMessaging.getInstance(firebaseApp).sendMulticast(builder.build());
            if (response.getFailureCount() > 0) {
                log.warn("Push notification partially failed: {} failures out of {}", response.getFailureCount(),
                        deviceTokens.size());
            } else {
                log.info("Push notification sent successfully to {} devices", deviceTokens.size());
            }
            return response.getFailureCount() == 0;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast push notification", e);
            return false;
        }
    }

    /**
     * Validate a device token.
     *
     * @param deviceToken The device token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateDeviceToken(String deviceToken) {
        if (deviceToken == null || deviceToken.isEmpty()) {
            return false;
        }

        // FCM tokens are typically 100-200 characters
        return deviceToken.length() >= 100 && deviceToken.length() <= 200;
    }

    /**
     * Send a silent push notification (no visible notification, just data).
     *
     * @param deviceToken The device token
     * @param data The data payload
     * @return true if notification was sent successfully, false otherwise
     */
    public boolean sendSilentNotification(String deviceToken, Map<String, Object> data) {
        if (!fcmEnabled) {
            log.warn("FCM service is disabled. Skipping silent notification to device: {}", deviceToken);
            return false;
        }

        log.info("Sending silent notification to device: {}", deviceToken);

        try {
            // Silent notification - no title/body, just data
            return sendToFcm(deviceToken, "", "", data);

        } catch (Exception e) {
            log.error("Failed to send silent notification to device: {}", deviceToken, e);
            return false;
        }
    }
}
