package com.travelplatform.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplatform.domain.model.audit.AuditLog;
import com.travelplatform.domain.repository.AuditLogRepository;
import com.travelplatform.infrastructure.security.CurrentUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for audit logging.
 * Provides methods to log sensitive operations and retrieve audit logs.
 */
@ApplicationScoped
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    @Inject
    AuditLogRepository auditLogRepository;

    @Inject
    CurrentUser currentUser;

    @Inject
    ObjectMapper objectMapper;

    @Context
    HttpHeaders httpHeaders;

    /**
     * Log an action performed by a user.
     * 
     * @param action     Action performed
     * @param entityType Type of entity affected
     * @param entityId   ID of entity affected
     * @param details    Additional details (will be converted to JSON)
     */
    @Transactional
    public void logAction(String action, String entityType, UUID entityId, Map<String, Object> details) {
        try {
            UUID userId = currentUser.getId();
            if (userId == null) {
                logger.warn("Attempted to log action without authenticated user: {}", action);
                return;
            }

            String detailsJson = null;
            if (details != null && !details.isEmpty()) {
                try {
                    detailsJson = objectMapper.writeValueAsString(details);
                } catch (JsonProcessingException e) {
                    logger.error("Failed to serialize audit log details", e);
                    detailsJson = details.toString();
                }
            }

            AuditLog auditLog = new AuditLog(userId, action, entityType, entityId, detailsJson);

            // Extract IP address and user agent if available
            if (httpHeaders != null) {
                String ipAddress = extractIpAddress();
                auditLog.setIpAddress(ipAddress);

                List<String> userAgentHeaders = httpHeaders.getRequestHeader(HttpHeaders.USER_AGENT);
                if (userAgentHeaders != null && !userAgentHeaders.isEmpty()) {
                    auditLog.setUserAgent(userAgentHeaders.get(0));
                }
            }

            auditLogRepository.persist(auditLog);
            logger.info("Audit log created: action={}, entityType={}, entityId={}, userId={}",
                    action, entityType, entityId, userId);

        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
            // Don't throw exception to avoid breaking the main operation
        }
    }

    /**
     * Log an action with a simple string detail.
     * 
     * @param action     Action performed
     * @param entityType Type of entity affected
     * @param entityId   ID of entity affected
     * @param detail     Simple detail string
     */
    @Transactional
    public void logAction(String action, String entityType, UUID entityId, String detail) {
        logAction(action, entityType, entityId, detail != null ? Map.of("detail", detail) : null);
    }

    /**
     * Log an action without details.
     * 
     * @param action     Action performed
     * @param entityType Type of entity affected
     * @param entityId   ID of entity affected
     */
    @Transactional
    public void logAction(String action, String entityType, UUID entityId) {
        logAction(action, entityType, entityId, (Map<String, Object>) null);
    }

    /**
     * Get audit logs for a specific entity.
     * 
     * @param entityType Type of entity
     * @param entityId   ID of entity
     * @return List of audit logs
     */
    public List<AuditLog> getAuditLogs(String entityType, UUID entityId) {
        return auditLogRepository.findByEntity(entityType, entityId);
    }

    /**
     * Get audit logs by user.
     * 
     * @param userId User ID
     * @return List of audit logs
     */
    public List<AuditLog> getAuditLogsByUser(UUID userId) {
        return auditLogRepository.findByUserId(userId);
    }

    /**
     * Get recent audit logs.
     * 
     * @param limit Maximum number of logs
     * @return List of audit logs
     */
    public List<AuditLog> getRecentAuditLogs(int limit) {
        return auditLogRepository.findRecent(limit);
    }

    /**
     * Extract IP address from request headers.
     * Checks X-Forwarded-For header first (for proxied requests).
     * 
     * @return IP address or null
     */
    private String extractIpAddress() {
        if (httpHeaders == null) {
            return null;
        }

        // Check X-Forwarded-For header (for proxied requests)
        List<String> forwardedFor = httpHeaders.getRequestHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            String ip = forwardedFor.get(0);
            // X-Forwarded-For can contain multiple IPs, take the first one
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }

        // Check X-Real-IP header
        List<String> realIp = httpHeaders.getRequestHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp.get(0);
        }

        return null;
    }
}
