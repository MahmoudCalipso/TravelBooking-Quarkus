package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.enums.ApprovalStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for travel_programs table.
 * This is persistence model for TravelProgram domain entity.
 */
@Entity
@Table(name = "travel_programs", indexes = {
    @Index(name = "idx_travel_programs_organizer_id", columnList = "organizer_id"),
    @Index(name = "idx_travel_programs_status", columnList = "status"),
    @Index(name = "idx_travel_programs_destination", columnList = "destination"),
    @Index(name = "idx_travel_programs_start_date", columnList = "start_date")
})
public class TravelProgramEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "destination", nullable = false, length = 255)
    private String destination;

    @Column(name = "itinerary", columnDefinition = "JSONB")
    private String itinerary;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "price_per_person", precision = 10, scale = 2)
    private BigDecimal pricePerPerson;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @ElementCollection
    @CollectionTable(name = "travel_program_included_items", joinColumns = @JoinColumn(name = "program_id"))
    @Column(name = "item")
    private List<String> includedItems;

    @ElementCollection
    @CollectionTable(name = "travel_program_excluded_items", joinColumns = @JoinColumn(name = "program_id"))
    @Column(name = "item")
    private List<String> excludedItems;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "min_participants")
    private Integer minParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApprovalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    // Default constructor for JPA
    public TravelProgramEntity() {
    }

    // Constructor for creating new entity
    public TravelProgramEntity(UUID id, UUID organizerId, String title, String destination, 
                           LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.organizerId = organizerId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ApprovalStatus.PENDING;
        this.currentParticipants = 0;
        this.currency = "USD";
        this.createdAt = LocalDateTime.now();
    }

    // Lifecycle callback for updating timestamp
    @PreUpdate
    protected void onUpdate() {
        // No updatedAt field in this entity based on spec
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(UUID organizerId) {
        this.organizerId = organizerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getItinerary() {
        return itinerary;
    }

    public void setItinerary(String itinerary) {
        this.itinerary = itinerary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(BigDecimal pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getIncludedItems() {
        return includedItems;
    }

    public void setIncludedItems(List<String> includedItems) {
        this.includedItems = includedItems;
    }

    public List<String> getExcludedItems() {
        return excludedItems;
    }

    public void setExcludedItems(List<String> excludedItems) {
        this.excludedItems = excludedItems;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(Integer minParticipants) {
        this.minParticipants = minParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
}
