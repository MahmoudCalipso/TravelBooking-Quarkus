package com.travelplatform.domain.model.audit;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity for audit logging.
 * Tracks all sensitive operations performed in the system.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
public class AuditLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * ID of the user who performed the action.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Action performed (e.g., "USER_BANNED", "PAYMENT_REFUNDED").
     */
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /**
     * Type of entity affected (e.g., "User", "Payment", "Accommodation").
     */
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    /**
     * ID of the entity affected.
     */
    @Column(name = "entity_id")
    private UUID entityId;

    /**
     * Additional details about the action in JSON format.
     */
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    /**
     * IP address of the user who performed the action.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent of the client.
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Timestamp when the action was performed.
     */
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    /**
     * Default constructor.
     */
    public AuditLog() {
        this.timestamp = Instant.now();
    }

    /**
     * Constructor with required fields.
     */
    public AuditLog(UUID userId, String action, String entityType, UUID entityId, String details) {
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.timestamp = Instant.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
