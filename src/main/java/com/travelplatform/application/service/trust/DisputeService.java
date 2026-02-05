package com.travelplatform.application.service.trust;

import com.travelplatform.infrastructure.persistence.entity.DisputeEntity;
import com.travelplatform.infrastructure.persistence.repository.JpaDisputeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Simple dispute handling service with in-memory storage.
 */
@ApplicationScoped
public class DisputeService {

    @Inject
    JpaDisputeRepository disputeRepository;

    public Dispute createDispute(UUID bookingId, UUID initiatorId, String reason) {
        DisputeEntity entity = new DisputeEntity(UUID.randomUUID(), bookingId, initiatorId, reason,
                "OPEN", null, LocalDateTime.now());
        disputeRepository.persist(entity);
        return toDto(entity);
    }

    public Dispute mediateDispute(UUID disputeId, UUID adminId, String resolution) {
        DisputeEntity entity = disputeRepository.findById(disputeId);
        if (entity == null) {
            throw new IllegalArgumentException("Dispute not found");
        }
        entity.setStatus("RESOLVED");
        entity.setResolution(resolution);
        entity.setUpdatedAt(LocalDateTime.now());
        disputeRepository.persist(entity);
        return toDto(entity);
    }

    public record Dispute(UUID id, UUID bookingId, UUID initiatorId, String reason,
                          String status, String resolution, LocalDateTime updatedAt) {
    }

    private Dispute toDto(DisputeEntity entity) {
        return new Dispute(entity.getId(), entity.getBookingId(), entity.getInitiatorId(),
                entity.getReason(), entity.getStatus(), entity.getResolution(), entity.getUpdatedAt());
    }
}
