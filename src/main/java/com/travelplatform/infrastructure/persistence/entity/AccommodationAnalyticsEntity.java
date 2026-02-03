package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity for accommodation_analytics table.
 * This is persistence model for daily metrics for accommodations.
 */
@Entity
@Table(name = "accommodation_analytics", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_accommodation_analytics_accommodation_date", columnNames = {"accommodation_id", "date"})
    },
    indexes = {
        @Index(name = "idx_accommodation_analytics_accommodation_id", columnList = "accommodation_id"),
        @Index(name = "idx_accommodation_analytics_date", columnList = "date")
    })
public class AccommodationAnalyticsEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "accommodation_id", nullable = false)
    private UUID accommodationId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "detail_view_count", nullable = false)
    private Integer detailViewCount = 0;

    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount = 0;

    @Column(name = "booking_inquiries", nullable = false)
    private Integer bookingInquiries = 0;

    @Column(name = "booking_conversions", nullable = false)
    private Integer bookingConversions = 0;

    @Column(name = "revenue", precision = 10, scale = 2)
    private BigDecimal revenue = BigDecimal.ZERO;

    // Default constructor for JPA
    public AccommodationAnalyticsEntity() {
    }

    // Constructor for creating new entity
    public AccommodationAnalyticsEntity(UUID id, UUID accommodationId, LocalDate date) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.date = date;
        this.viewCount = 0;
        this.detailViewCount = 0;
        this.favoriteCount = 0;
        this.bookingInquiries = 0;
        this.bookingConversions = 0;
        this.revenue = BigDecimal.ZERO;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getDetailViewCount() {
        return detailViewCount;
    }

    public void setDetailViewCount(Integer detailViewCount) {
        this.detailViewCount = detailViewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getBookingInquiries() {
        return bookingInquiries;
    }

    public void setBookingInquiries(Integer bookingInquiries) {
        this.bookingInquiries = bookingInquiries;
    }

    public Integer getBookingConversions() {
        return bookingConversions;
    }

    public void setBookingConversions(Integer bookingConversions) {
        this.bookingConversions = bookingConversions;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
