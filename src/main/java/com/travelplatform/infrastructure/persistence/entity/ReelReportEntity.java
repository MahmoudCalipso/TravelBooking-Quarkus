package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.ReportReason;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for reel_reports table.
 * This is persistence model for ReelReport domain entity.
 */
@Entity
@Table(name = "reel_reports", indexes = {
    @Index(name = "idx_reel_reports_reel_id", columnList = "reel_id"),
    @Index(name = "idx_reel_reports_reported_by", columnList = "reported_by"),
    @Index(name = "idx_reel_reports_status", columnList = "status")
})
public class ReelReportEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reel_id", nullable = false)
    private UUID reelId;

    @Column(name = "reported_by", nullable = false)
    private UUID reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // Default constructor for JPA
    public ReelReportEntity() {
    }

    // Constructor for creating new entity
    public ReelReportEntity(UUID id, UUID reelId, UUID reportedBy, ReportReason reason, String description) {
        this.id = id;
        this.reelId = reelId;
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.description = description;
        this.status = ApprovalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReelId() {
        return reelId;
    }

    public void setReelId(UUID reelId) {
        this.reelId = reelId;
    }

    public UUID getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(UUID reportedBy) {
        this.reportedBy = reportedBy;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UUID reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
