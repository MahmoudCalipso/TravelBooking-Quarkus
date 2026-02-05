package com.travelplatform.interfaces.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket endpoint for live user notifications.
 */
@ServerEndpoint("/ws/notifications/{userId}")
@ApplicationScoped
public class NotificationWebSocketEndpoint {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketEndpoint.class);
    private static NotificationWebSocketEndpoint instance;

    private final Map<UUID, Set<Session>> userSessions = new ConcurrentHashMap<>();

    @Inject
    ObjectMapper objectMapper;

    public NotificationWebSocketEndpoint() {
        instance = this;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        UUID id = UUID.fromString(userId);
        userSessions.computeIfAbsent(id, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("Notification WebSocket connected: user={}, session={}", userId, session.getId());
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        UUID id = UUID.fromString(userId);
        userSessions.computeIfPresent(id, (k, sessions) -> {
            sessions.remove(session);
            return sessions.isEmpty() ? null : sessions;
        });
        log.info("Notification WebSocket disconnected: user={}, session={}", userId, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Notification WebSocket error for session {}", session.getId(), throwable);
    }

    /**
     * Send a notification to all sessions of a user.
     */
    public static void sendNotification(UUID userId, String title, String message, String type) {
        if (instance == null) {
            return;
        }
        instance.doSend(userId, title, message, type);
    }

    private void doSend(UUID userId, String title, String message, String type) {
        try {
            NotificationMessage payload = new NotificationMessage(type, title, message, Instant.now());
            String json = objectMapper.writeValueAsString(payload);
            Set<Session> sessions = userSessions.getOrDefault(userId, Set.of());
            for (Session s : sessions) {
                if (s.isOpen()) {
                    s.getAsyncRemote().sendText(json);
                }
            }
        } catch (Exception e) {
            log.error("Failed to send notification to user {}", userId, e);
        }
    }

    /**
     * Payload sent to client.
     */
    public record NotificationMessage(String type, String title, String message, Instant timestamp) {
    }
}
