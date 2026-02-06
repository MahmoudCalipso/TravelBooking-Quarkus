package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.audit.AuditLog;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

/**
 * Repository for AuditLog entity.
 */
@ApplicationScoped
public class AuditLogRepository implements PanacheRepositoryBase<AuditLog, UUID> {

    /**
     * Find audit logs by entity type and ID.
     * 
     * @param entityType Type of entity
     * @param entityId   ID of entity
     * @return List of audit logs
     */
    public List<AuditLog> findByEntity(String entityType, UUID entityId) {
        return list("entityType = ?1 and entityId = ?2 order by timestamp desc", entityType, entityId);
    }

    /**
     * Find audit logs by user ID.
     * 
     * @param userId User ID
     * @return List of audit logs
     */
    public List<AuditLog> findByUserId(UUID userId) {
        return list("userId = ?1 order by timestamp desc", userId);
    }

    /**
     * Find audit logs by action.
     * 
     * @param action Action type
     * @return List of audit logs
     */
    public List<AuditLog> findByAction(String action) {
        return list("action = ?1 order by timestamp desc", action);
    }

    /**
     * Find recent audit logs.
     * 
     * @param limit Maximum number of logs to return
     * @return List of audit logs
     */
    public List<AuditLog> findRecent(int limit) {
        return find("order by timestamp desc").page(0, limit).list();
    }
}
