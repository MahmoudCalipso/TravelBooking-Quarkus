package com.travelplatform.domain.model.reel;

import com.travelplatform.domain.enums.ReportReason;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user report for inappropriate reel content.
 */
public class ReelReport {
    private final UUID id;
    private final UUID reelId;
    private final UUID reportedBy;
    private final ReportReason reason;
    private String description;
    private ReportStatus status;
    private UUID reviewedBy;
    private String adminNotes;
    private final LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    /**
     * Creates a new ReelReport.
     *
     * @param reelId      reel ID being reported
     * @param reportedBy  user ID who reported
     * @param reason      reason for report
     * @param description additional details
     * @throws IllegalArgumentException if required fields are null
     */
    public ReelReport(UUID reelId, UUID reportedBy, ReportReason reason, String description) {
        if (reelId == null) {
            throw new IllegalArgumentException("Reel ID cannot be null");
        }
        if (reportedBy == null) {
            throw new IllegalArgumentException("Reported by cannot be null");
        }
        if (reason == null) {
            throw new IllegalArgumentException("Reason cannot be null");
        }

        this.id = UUID.randomUUID();
        this.reelId = reelId;
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.description = description;
        this.status = ReportStatus.PENDING;
        this.reviewedBy = null;
        this.adminNotes = null;
        this.createdAt = LocalDateTime.now();
        this.reviewedAt = null;
    }

    /**
     * Reconstructs a ReelReport from persistence.
     */
    public ReelReport(UUID id, UUID reelId, UUID reportedBy, ReportReason reason, String description,
            ReportStatus status, UUID reviewedBy, String adminNotes,
            LocalDateTime createdAt, LocalDateTime reviewedAt) {
        this.id = id;
        this.reelId = reelId;
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.adminNotes = adminNotes;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReelId() {
        return reelId;
    }

    public UUID getReportedBy() {
        return reportedBy;
    }

    public ReportReason getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    /**
     * Marks the report as reviewed.
     *
     * @param reviewedBy admin user ID who reviewed
     * @param adminNotes internal notes from admin
     */
    public void markAsReviewed(UUID reviewedBy, String adminNotes) {
        if (reviewedBy == null) {
            throw new IllegalArgumentException("Reviewed by cannot be null");
        }
        this.reviewedBy = reviewedBy;
        this.adminNotes = adminNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Dismisses the report (no action taken).
     */
    public void dismiss() {
        this.status = ReportStatus.DISMISSED;
    }

    /**
     * Marks the report as reviewed with a specific status.
     *
     * @param reviewedBy admin user ID who reviewed
     * @param status     new status (REVIEWED, DISMISSED, ACTION_TAKEN)
     * @param adminNotes internal notes from admin
     */
    public void review(UUID reviewedBy, ReportStatus status, String adminNotes) {
        if (reviewedBy == null) {
            throw new IllegalArgumentException("Reviewed by cannot be null");
        }
        if (status == null || status == ReportStatus.PENDING) {
            throw new IllegalArgumentException("Invalid status for review completion");
        }
        this.reviewedBy = reviewedBy;
        this.status = status;
        this.adminNotes = adminNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * Marks the report as action taken (reel removed/flagged).
     */
    public void markActionTaken() {
        this.status = ReportStatus.ACTION_TAKEN;
    }

    /**
     * Checks if the report is pending review.
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }

    /**
     * Checks if the report has been reviewed.
     *
     * @return true if reviewed
     */
    public boolean isReviewed() {
        return this.reviewedAt != null;
    }

    /**
     * Checks if the report was dismissed.
     *
     * @return true if status is DISMISSED
     */
    public boolean isDismissed() {
        return this.status == ReportStatus.DISMISSED;
    }

    /**
     * Checks if action was taken on the report.
     *
     * @return true if status is ACTION_TAKEN
     */
    public boolean isActionTaken() {
        return this.status == ReportStatus.ACTION_TAKEN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReelReport that = (ReelReport) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ReelReport{id=%s, reelId=%s, reason=%s, status=%s}",
                id, reelId, reason, status);
    }

    /**
     * Enumeration of report statuses.
     */
    public enum ReportStatus {
        PENDING,
        REVIEWED,
        DISMISSED,
        ACTION_TAKEN
    }
}
