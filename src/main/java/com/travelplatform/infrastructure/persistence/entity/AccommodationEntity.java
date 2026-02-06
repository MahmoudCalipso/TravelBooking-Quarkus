package com.travelplatform.infrastructure.persistence.entity;

import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * JPA Entity for Accommodation table.
 * This is the persistence model for Accommodation domain entity.
 */
@Entity
@Table(name = "accommodations", indexes = {
        @Index(name = "idx_accommodations_supplier_id", columnList = "supplier_id"),
        @Index(name = "idx_accommodations_status", columnList = "status"),
        @Index(name = "idx_accommodations_city_country", columnList = "city, country"),
        @Index(name = "idx_accommodations_status_premium_rating", columnList = "status, is_premium, average_rating"),
        @Index(name = "idx_accommodations_visibility_dates", columnList = "visibility_start, visibility_end")
})
public class AccommodationEntity extends BaseEntity {

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private AccommodationType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "beds")
    private Integer beds;

    @Column(name = "bathrooms", precision = 3, scale = 1)
    private BigDecimal bathrooms;

    @Column(name = "square_meters")
    private Integer squareMeters;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApprovalStatus status;

    @Column(name = "visibility_start")
    private LocalDateTime visibilityStart;

    @Column(name = "visibility_end")
    private LocalDateTime visibilityEnd;

    @Column(name = "is_premium", nullable = false)
    private boolean isPremium = false;

    @Column(name = "is_instant_book", nullable = false)
    private boolean isInstantBook = false;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "minimum_nights")
    private Integer minimumNights = 1;

    @Column(name = "maximum_nights")
    private Integer maximumNights;

    @Column(name = "cancellation_policy", length = 50)
    private String cancellationPolicy;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "booking_count")
    private Integer bookingCount = 0;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    // Default constructor for JPA
    public AccommodationEntity() {
    }

    // Constructor for creating new entity
    public AccommodationEntity(UUID id, UUID supplierId, AccommodationType type, String title,
            String address, String city, String country, BigDecimal basePrice, Integer maxGuests) {
        this.id = id;
        this.supplierId = supplierId;
        this.type = type;
        this.title = title;
        this.address = address;
        this.city = city;
        this.country = country;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
        this.status = ApprovalStatus.PENDING;
    }

    // Getters and Setters
    public UUID getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }

    public AccommodationType getType() {
        return type;
    }

    public void setType(AccommodationType type) {
        this.type = type;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public BigDecimal getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(BigDecimal bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(Integer squareMeters) {
        this.squareMeters = squareMeters;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getVisibilityStart() {
        return visibilityStart;
    }

    public void setVisibilityStart(LocalDateTime visibilityStart) {
        this.visibilityStart = visibilityStart;
    }

    public LocalDateTime getVisibilityEnd() {
        return visibilityEnd;
    }

    public void setVisibilityEnd(LocalDateTime visibilityEnd) {
        this.visibilityEnd = visibilityEnd;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean isInstantBook() {
        return isInstantBook;
    }

    public void setInstantBook(boolean instantBook) {
        isInstantBook = instantBook;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Integer getMinimumNights() {
        return minimumNights;
    }

    public void setMinimumNights(Integer minimumNights) {
        this.minimumNights = minimumNights;
    }

    public Integer getMaximumNights() {
        return maximumNights;
    }

    public void setMaximumNights(Integer maximumNights) {
        this.maximumNights = maximumNights;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Integer bookingCount) {
        this.bookingCount = bookingCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
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
