package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disputes")
public class DisputeEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID initiatorId;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public DisputeEntity() {
    }

    public DisputeEntity(UUID id, UUID bookingId, UUID initiatorId, String reason, String status, String resolution, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.initiatorId = initiatorId;
        this.reason = reason;
        this.status = status;
        this.resolution = resolution;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(UUID initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
