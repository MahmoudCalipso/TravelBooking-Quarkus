package com.travelplatform.infrastructure.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.valueobject.Money;
import com.travelplatform.infrastructure.persistence.entity.AuditLogEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for persisting audit trail records for sensitive operations.
 */
@ApplicationScoped
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    @Inject
    EntityManager entityManager;

    @Inject
    ObjectMapper objectMapper;

    @Transactional
    public void logAccommodationApproval(UUID adminId, UUID accommodationId) {
        Map<String, Object> changes = Map.of("status", "APPROVED");
        saveAuditLog(adminId, "ACCOMMODATION_APPROVAL", "Accommodation", accommodationId, null, null, changes);
    }

    @Transactional
    public void logUserSuspension(UUID adminId, UUID userId, String reason) {
        Map<String, Object> changes = Map.of("action", "SUSPEND", "reason", reason);
        saveAuditLog(adminId, "USER_SUSPENSION", "User", userId, null, null, changes);
    }

    @Transactional
    public void logPaymentTransaction(UUID bookingId, Money amount, PaymentStatus status) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("amount", amount != null ? amount.toString() : null);
        changes.put("status", status != null ? status.name() : null);
        saveAuditLog(null, "PAYMENT_EVENT", "Booking", bookingId, null, null, changes);
    }

    private void saveAuditLog(UUID userId, String action, String entityType, UUID entityId,
                              String ipAddress, String userAgent, Map<String, Object> changes) {
        try {
            AuditLogEntity entity = new AuditLogEntity(UUID.randomUUID(), action);
            entity.setUserId(userId);
            entity.setEntityType(entityType);
            entity.setEntityId(entityId);
            entity.setIpAddress(ipAddress);
            entity.setUserAgent(userAgent);
            entity.setChanges(changes != null ? objectMapper.writeValueAsString(changes) : null);
            entity.setCreatedAt(LocalDateTime.now());

            entityManager.persist(entity);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log changes for action {}", action, e);
        }
    }
}
