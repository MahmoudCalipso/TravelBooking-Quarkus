package com.travelplatform.domain.model.accommodation;

import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.valueobject.Address;
import com.travelplatform.domain.valueobject.Location;
import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.math.BigDecimal;

/**
 * Domain entity representing an accommodation listing.
 * This is the aggregate root for the accommodation aggregate.
 */
public class Accommodation {
    private final UUID id;
    private final UUID supplierId;
    private final AccommodationType type;
    private String title;
    private String description;
    private Address address;
    private Location location;
    private Money basePrice;
    private int maxGuests;
    private Integer bedrooms;
    private Integer beds;
    private Double bathrooms;
    private Integer squareMeters;
    private ApprovalStatus status;
    private LocalDateTime visibilityStart;
    private LocalDateTime visibilityEnd;
    private boolean isPremium;
    private boolean isInstantBook;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private int minimumNights;
    private Integer maximumNights;
    private String cancellationPolicy;
    private long viewCount;
    private int bookingCount;
    private Double averageRating;
    private int reviewCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private UUID approvedBy;

    // Associated entities (part of the aggregate)
    private List<AccommodationImage> images;
    private List<AccommodationAmenity> amenities;

    /**
     * Creates a new Accommodation with the specified details.
     *
     * @param supplierId  supplier user ID
     * @param type        accommodation type
     * @param title       listing title
     * @param description detailed description
     * @param address     full address
     * @param location    GPS location
     * @param basePrice   nightly rate
     * @param maxGuests   maximum occupancy
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Accommodation(UUID supplierId, AccommodationType type, String title, String description,
            Address address, Location location, Money basePrice, int maxGuests) {
        if (supplierId == null) {
            throw new IllegalArgumentException("Supplier ID cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Accommodation type cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }
        if (maxGuests <= 0) {
            throw new IllegalArgumentException("Max guests must be positive");
        }

        this.id = UUID.randomUUID();
        this.supplierId = supplierId;
        this.type = type;
        this.title = title.trim();
        this.description = description;
        this.address = address;
        this.location = location;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
        this.bedrooms = null;
        this.beds = null;
        this.bathrooms = null;
        this.squareMeters = null;
        this.status = ApprovalStatus.PENDING;
        this.visibilityStart = null;
        this.visibilityEnd = null;
        this.isPremium = false;
        this.isInstantBook = false;
        this.checkInTime = LocalTime.of(15, 0); // Default 3:00 PM
        this.checkOutTime = LocalTime.of(11, 0); // Default 11:00 AM
        this.minimumNights = 1;
        this.maximumNights = null;
        this.cancellationPolicy = "MODERATE";
        this.viewCount = 0;
        this.bookingCount = 0;
        this.averageRating = null;
        this.reviewCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.approvedAt = null;
        this.approvedBy = null;
        this.images = new ArrayList<>();
        this.amenities = new ArrayList<>();
    }

    /**
     * Reconstructs an Accommodation from persistence.
     */
    public Accommodation(UUID id, UUID supplierId, AccommodationType type, String title, String description,
            Address address, Location location, Money basePrice, int maxGuests, Integer bedrooms,
            Integer beds, Double bathrooms, Integer squareMeters, ApprovalStatus status,
            LocalDateTime visibilityStart, LocalDateTime visibilityEnd, boolean isPremium,
            boolean isInstantBook, LocalTime checkInTime, LocalTime checkOutTime,
            int minimumNights, Integer maximumNights, String cancellationPolicy,
            long viewCount, int bookingCount, Double averageRating, int reviewCount,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime approvedAt, UUID approvedBy) {
        this.id = id;
        this.supplierId = supplierId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.address = address;
        this.location = location;
        this.basePrice = basePrice;
        this.maxGuests = maxGuests;
        this.bedrooms = bedrooms;
        this.beds = beds;
        this.bathrooms = bathrooms;
        this.squareMeters = squareMeters;
        this.status = status;
        this.visibilityStart = visibilityStart;
        this.visibilityEnd = visibilityEnd;
        this.isPremium = isPremium;
        this.isInstantBook = isInstantBook;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.minimumNights = minimumNights;
        this.maximumNights = maximumNights;
        this.cancellationPolicy = cancellationPolicy;
        this.viewCount = viewCount;
        this.bookingCount = bookingCount;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.images = new ArrayList<>();
        this.amenities = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public AccommodationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }

    public String getCity() {
        return address != null ? address.getCity() : null;
    }

    public String getCountry() {
        return address != null ? address.getCountry() : null;
    }

    public String getStateProvince() {
        return address != null ? address.getStateProvince() : null;
    }

    public String getPostalCode() {
        return address != null ? address.getPostalCode() : null;
    }

    public Location getLocation() {
        return location;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public Integer getBeds() {
        return beds;
    }

    public Double getBathrooms() {
        return bathrooms;
    }

    public Integer getSquareMeters() {
        return squareMeters;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public LocalDateTime getVisibilityStart() {
        return visibilityStart;
    }

    public LocalDateTime getVisibilityEnd() {
        return visibilityEnd;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean isInstantBook() {
        return isInstantBook;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public int getMinimumNights() {
        return minimumNights;
    }

    public Integer getMaximumNights() {
        return maximumNights;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public long getViewCount() {
        return viewCount;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public List<AccommodationImage> getImages() {
        return new ArrayList<>(images);
    }

    public List<AccommodationAmenity> getAmenities() {
        return new ArrayList<>(amenities);
    }

    /**
     * Updates the accommodation details.
     */
    public void updateDetails(String title, String description, Integer bedrooms, Integer beds,
            Double bathrooms, Integer squareMeters, int minimumNights,
            Integer maximumNights, String cancellationPolicy,
            LocalTime checkInTime, LocalTime checkOutTime, boolean isInstantBook) {
        this.title = title != null ? title.trim() : this.title;
        this.description = description;
        this.bedrooms = bedrooms;
        this.beds = beds;
        this.bathrooms = bathrooms;
        this.squareMeters = squareMeters;
        this.minimumNights = minimumNights > 0 ? minimumNights : this.minimumNights;
        this.maximumNights = maximumNights;
        this.cancellationPolicy = cancellationPolicy;
        this.checkInTime = checkInTime != null ? checkInTime : this.checkInTime;
        this.checkOutTime = checkOutTime != null ? checkOutTime : this.checkOutTime;
        this.isInstantBook = isInstantBook;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approves the accommodation.
     *
     * @param approvedBy admin user ID who approved
     */
    public void approve(UUID approvedBy) {
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approved by cannot be null");
        }
        this.status = ApprovalStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rejects the accommodation.
     */
    public void reject() {
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Flags the accommodation for review.
     */
    public void flag() {
        this.status = ApprovalStatus.FLAGGED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Sets premium visibility period.
     *
     * @param startDate start of premium period
     * @param endDate   end of premium period
     */
    public void setPremiumVisibility(LocalDateTime startDate, LocalDateTime endDate) {
        this.visibilityStart = startDate;
        this.visibilityEnd = endDate;
        this.isPremium = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void setIsInstantBook(boolean isInstantBook) {
        this.isInstantBook = isInstantBook;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Removes premium visibility.
     */
    public void removePremiumVisibility() {
        this.visibilityStart = null;
        this.visibilityEnd = null;
        this.isPremium = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the view count.
     */
    public void incrementViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the booking count.
     */
    public void incrementBookingCount() {
        this.bookingCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the average rating.
     *
     * @param newAverage new average rating
     */
    public void updateAverageRating(Double newAverage) {
        this.averageRating = newAverage;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Increments the review count.
     */
    public void incrementReviewCount() {
        this.reviewCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an image to the accommodation.
     *
     * @param image accommodation image
     */
    public void addImage(AccommodationImage image) {
        if (image != null) {
            this.images.add(image);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Removes an image from the accommodation.
     *
     * @param imageId image ID to remove
     */
    public void removeImage(UUID imageId) {
        this.images.removeIf(img -> img.getId().equals(imageId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an amenity to the accommodation.
     *
     * @param amenity accommodation amenity
     */
    public void addAmenity(AccommodationAmenity amenity) {
        if (amenity != null) {
            this.amenities.add(amenity);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Removes an amenity from the accommodation.
     *
     * @param amenityId amenity ID to remove
     */
    public void removeAmenity(UUID amenityId) {
        this.amenities.removeIf(amenity -> amenity.getId().equals(amenityId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the accommodation is approved.
     *
     * @return true if status is APPROVED
     */
    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    /**
     * Checks if the accommodation is pending approval.
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    /**
     * Checks if the accommodation is currently visible (approved and within
     * visibility period).
     *
     * @return true if accommodation is visible
     */
    public boolean isVisible() {
        if (!isApproved()) {
            return false;
        }
        if (isPremium && visibilityEnd != null && LocalDateTime.now().isAfter(visibilityEnd)) {
            return false;
        }
        return true;
    }

    /**
     * Setters for mutable fields, supporting Service layer updates.
     */

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBathrooms(Double bathrooms) {
        this.bathrooms = bathrooms;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBathrooms(BigDecimal bathrooms) {
        this.bathrooms = bathrooms != null ? bathrooms.doubleValue() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void setSquareMeters(Integer squareMeters) {
        this.squareMeters = squareMeters;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void setMinimumNights(int minimumNights) {
        this.minimumNights = minimumNights;
        this.updatedAt = LocalDateTime.now();
    }

    public void setMaximumNights(Integer maximumNights) {
        this.maximumNights = maximumNights;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
        this.updatedAt = LocalDateTime.now();
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
        this.updatedAt = LocalDateTime.now();
    }

    // Smart Setters for Address
    public void setAddress(String streetAddress) {
        if (this.address != null) {
            this.address = new Address(streetAddress, this.address.getCity(), this.address.getStateProvince(),
                    this.address.getCountry(), this.address.getPostalCode());
        } else {
            // Fallback if address was null (unlikely due to constructor)
            // We can't really create a valid address without other fields.
            // Assuming sequence of calls handles this or checks null.
            // For now, simple update.
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void setCity(String city) {
        if (this.address != null) {
            this.address = new Address(this.address.getStreetAddress(), city, this.address.getStateProvince(),
                    this.address.getCountry(), this.address.getPostalCode());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setStateProvince(String stateProvince) {
        if (this.address != null) {
            this.address = new Address(this.address.getStreetAddress(), this.address.getCity(), stateProvince,
                    this.address.getCountry(), this.address.getPostalCode());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setCountry(String country) {
        if (this.address != null) {
            this.address = new Address(this.address.getStreetAddress(), this.address.getCity(),
                    this.address.getStateProvince(), country, this.address.getPostalCode());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setPostalCode(String postalCode) {
        if (this.address != null) {
            this.address = new Address(this.address.getStreetAddress(), this.address.getCity(),
                    this.address.getStateProvince(), this.address.getCountry(), postalCode);
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Smart Setters for Location
    public void setLatitude(BigDecimal latitude) {
        if (this.location != null && latitude != null) {
            this.location = new Location(latitude.doubleValue(), this.location.getLongitude());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setLongitude(BigDecimal longitude) {
        if (this.location != null && longitude != null) {
            this.location = new Location(this.location.getLatitude(), longitude.doubleValue());
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Smart Setters for Money
    public void setBasePrice(BigDecimal amount) {
        if (this.basePrice != null && amount != null) {
            this.basePrice = new Money(amount, this.basePrice.getCurrency());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setCurrency(String currencyCode) {
        if (this.basePrice != null && currencyCode != null && !currencyCode.isEmpty()) {
            this.basePrice = new Money(this.basePrice.getAmount(), currencyCode);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public String getCurrency() {
        return basePrice != null ? basePrice.getCurrencyCode() : null;
    }

    public void setImages(List<AccommodationImage> images) {
        this.images.clear();
        if (images != null) {
            this.images.addAll(images);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void setAmenities(List<AccommodationAmenity> amenities) {
        this.amenities.clear();
        if (amenities != null) {
            this.amenities.addAll(amenities);
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Accommodation that = (Accommodation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Accommodation{id=%s, title='%s', type=%s, status=%s}",
                id, title, type, status);
    }
}
