package com.travelplatform.interfaces.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplatform.application.dto.response.chat.MessageResponse;
import com.travelplatform.application.service.chat.ChatService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket endpoint for real-time chat messages.
 * Provides group messaging per conversationId.
 */
@ServerEndpoint("/ws/chat/{conversationId}")
@ApplicationScoped
public class ChatWebSocketEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketEndpoint.class);

    private final Map<UUID, Set<Session>> conversationSessions = new ConcurrentHashMap<>();

    @Inject
    ChatService chatService;

    @Inject
    ObjectMapper objectMapper;

    @OnOpen
    public void onOpen(Session session, @PathParam("conversationId") String conversationId) {
        UUID convId = UUID.fromString(conversationId);
        conversationSessions.computeIfAbsent(convId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("WebSocket connected: conversation={}, session={}", conversationId, session.getId());
    }

    @OnMessage
    public void onMessage(String payload, Session session, @PathParam("conversationId") String conversationId) {
        try {
            ChatSocketMessage incoming = objectMapper.readValue(payload, ChatSocketMessage.class);
            UUID convId = UUID.fromString(conversationId);
            MessageResponse saved = chatService.sendDirectMessage(incoming.senderId(), convId, incoming.content());
            broadcast(convId, saved);
        } catch (Exception e) {
            log.error("Failed to process chat message for conversation {}", conversationId, e);
            sendError(session, "Unable to send message");
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("conversationId") String conversationId) {
        UUID convId = UUID.fromString(conversationId);
        conversationSessions.computeIfPresent(convId, (id, sessions) -> {
            sessions.remove(session);
            return sessions.isEmpty() ? null : sessions;
        });
        log.info("WebSocket disconnected: conversation={}, session={}", conversationId, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket error for session {}", session.getId(), throwable);
    }

    private void broadcast(UUID conversationId, MessageResponse message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        Set<Session> sessions = conversationSessions.getOrDefault(conversationId, Set.of());
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(json);
            }
        }
    }

    private void sendError(Session session, String message) {
        if (session.isOpen()) {
            session.getAsyncRemote().sendText("{\"error\":\"" + message + "\"}");
        }
    }

    /**
     * Simple DTO for inbound socket messages.
     */
    public record ChatSocketMessage(UUID senderId, String content) {
    }
}
