package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.infrastructure.persistence.entity.AuditLogEntity;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Admin controller for system auditing and compliance.
 */
@Path("/api/v1/admin/audit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Audit", description = "SUPER_ADMIN endpoints for system audit logs")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminAuditController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuditController.class);

    @Inject
    EntityManager entityManager;

    /**
     * List all audit logs with filters.
     */
    @GET
    @Operation(summary = "List audit logs", description = "Query system audit logs with filters")
    public PaginatedResponse<AuditLogEntity> listAuditLogs(
            @QueryParam("adminId") UUID adminId,
            @QueryParam("action") String action,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        logger.info("Admin listing audit logs: adminId={}, action={}", adminId, action);

        StringBuilder jpql = new StringBuilder("SELECT a FROM AuditLogEntity a WHERE 1=1");
        if (adminId != null)
            jpql.append(" AND a.adminId = :adminId");
        if (action != null)
            jpql.append(" AND a.action = :action");
        jpql.append(" ORDER BY a.createdAt DESC");

        TypedQuery<AuditLogEntity> query = entityManager.createQuery(jpql.toString(), AuditLogEntity.class);
        if (adminId != null)
            query.setParameter("adminId", adminId);
        if (action != null)
            query.setParameter("action", action);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<AuditLogEntity> logs = query.getResultList();

        // Count for pagination
        String countJpql = jpql.toString().replace("SELECT a", "SELECT COUNT(a)");
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        if (adminId != null)
            countQuery.setParameter("adminId", adminId);
        if (action != null)
            countQuery.setParameter("action", action);
        long totalCount = countQuery.getSingleResult();

        return PaginatedResponse.of(logs, totalCount, page, size);
    }

    /**
     * View detailed audit entry.
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "View audit entry", description = "Get details of a specific audit log entry")
    public BaseResponse<AuditLogEntity> getAuditEntry(@PathParam("id") UUID id) {
        logger.info("Admin viewing audit entry: id={}", id);

        AuditLogEntity entry = entityManager.find(AuditLogEntity.class, id);
        if (entry == null) {
            throw new NotFoundException("Audit entry not found");
        }

        return BaseResponse.success(entry);
    }

    /**
     * Get security alert statistics.
     */
    @GET
    @Path("/alerts")
    @Operation(summary = "View security alerts", description = "Summarize recent sensitive administrative actions")
    public BaseResponse<List<Map<String, Object>>> getSecurityAlerts() {
        logger.info("Admin viewing security alerts");

        // Query for potentially sensitive actions like login, role change, ban
        String jpql = "SELECT a.action, COUNT(a) FROM AuditLogEntity a " +
                "WHERE a.createdAt > :since AND a.action IN ('LOGIN', 'BAN_USER', 'ROLE_CHANGE') " +
                "GROUP BY a.action";

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
                .setParameter("since", LocalDateTime.now().minusDays(7))
                .getResultList();

        List<Map<String, Object>> alerts = results.stream()
                .map(r -> Map.of("action", r[0], "count", r[1]))
                .toList();

        return BaseResponse.success(alerts);
    }
}
