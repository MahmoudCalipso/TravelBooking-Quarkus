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

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket endpoint for live availability updates per accommodation.
 */
@ServerEndpoint("/ws/availability/{accommodationId}")
@ApplicationScoped
public class AvailabilityWebSocketEndpoint {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityWebSocketEndpoint.class);
    private static AvailabilityWebSocketEndpoint instance;

    private final Map<UUID, Set<Session>> availabilitySessions = new ConcurrentHashMap<>();

    @Inject
    ObjectMapper objectMapper;

    public AvailabilityWebSocketEndpoint() {
        instance = this;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("accommodationId") String accommodationId) {
        UUID id = UUID.fromString(accommodationId);
        availabilitySessions.computeIfAbsent(id, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("Availability WebSocket connected: accommodation={}, session={}", accommodationId, session.getId());
    }

    @OnClose
    public void onClose(Session session, @PathParam("accommodationId") String accommodationId) {
        UUID id = UUID.fromString(accommodationId);
        availabilitySessions.computeIfPresent(id, (k, sessions) -> {
            sessions.remove(session);
            return sessions.isEmpty() ? null : sessions;
        });
        log.info("Availability WebSocket disconnected: accommodation={}, session={}", accommodationId, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Availability WebSocket error for session {}", session.getId(), throwable);
    }

    /**
     * Broadcast availability change to all subscribers of an accommodation.
     */
    public static void broadcastAvailabilityChange(UUID accommodationId, LocalDate date, boolean available) {
        if (instance == null) {
            return;
        }
        instance.doBroadcast(accommodationId, date, available);
    }

    private void doBroadcast(UUID accommodationId, LocalDate date, boolean available) {
        try {
            AvailabilityUpdate payload = new AvailabilityUpdate(accommodationId, date, available);
            String message = objectMapper.writeValueAsString(payload);
            Set<Session> sessions = availabilitySessions.getOrDefault(accommodationId, Set.of());
            for (Session s : sessions) {
                if (s.isOpen()) {
                    s.getAsyncRemote().sendText(message);
                }
            }
        } catch (Exception e) {
            log.error("Failed to broadcast availability for {}", accommodationId, e);
        }
    }

    /**
     * Payload sent to clients.
     */
    public record AvailabilityUpdate(UUID accommodationId, LocalDate date, boolean available) {
    }
}
