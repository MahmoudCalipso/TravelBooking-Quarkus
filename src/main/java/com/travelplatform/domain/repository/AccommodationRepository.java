package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.accommodation.AccommodationImage;
import com.travelplatform.domain.model.accommodation.AccommodationAmenity;
import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.valueobject.Location;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Accommodation aggregate.
 * Defines the contract for accommodation data access operations.
 */
public interface AccommodationRepository {

    /**
     * Saves a new accommodation.
     *
     * @param accommodation accommodation to save
     * @return saved accommodation
     */
    Accommodation save(Accommodation accommodation);

    /**
     * Updates an existing accommodation.
     *
     * @param accommodation accommodation to update
     * @return updated accommodation
     */
    Accommodation update(Accommodation accommodation);

    /**
     * Deletes an accommodation by ID.
     *
     * @param id accommodation ID
     */
    void deleteById(UUID id);

    /**
     * Finds an accommodation by ID.
     *
     * @param id accommodation ID
     * @return optional accommodation
     */
    Optional<Accommodation> findById(UUID id);

    /**
     * Finds all accommodations.
     *
     * @return list of all accommodations
     */
    List<Accommodation> findAll();

    /**
     * Finds accommodations by supplier ID.
     *
     * @param supplierId supplier user ID
     * @return list of accommodations by supplier
     */
    List<Accommodation> findBySupplierId(UUID supplierId);

    /**
     * Finds accommodations by type.
     *
     * @param type accommodation type
     * @return list of accommodations with the type
     */
    List<Accommodation> findByType(AccommodationType type);

    /**
     * Finds accommodations by status.
     *
     * @param status approval status
     * @return list of accommodations with the status
     */
    List<Accommodation> findByStatus(ApprovalStatus status);

    /**
     * Finds accommodations by city.
     *
     * @param city city name
     * @return list of accommodations in the city
     */
    List<Accommodation> findByCity(String city);

    /**
     * Finds accommodations by city and country.
     *
     * @param city    city name
     * @param country country name
     * @return list of accommodations in the city and country
     */
    List<Accommodation> findByCityAndCountry(String city, String country);

    /**
     * Finds accommodations near a location.
     *
     * @param location center location
     * @param radiusKm search radius in kilometers
     * @return list of accommodations within radius
     */
    List<Accommodation> findNearby(Location location, double radiusKm);

    /**
     * Finds accommodations near a location with pagination.
     *
     * @param location center location
     * @param radiusKm search radius in kilometers
     * @param page     page number
     * @param pageSize page size
     * @return list of accommodations
     */
    List<Accommodation> findNearbyPaginated(Location location, double radiusKm, int page, int pageSize);

    /**
     * Finds accommodations by status with pagination.
     *
     * @param status   approval status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of accommodations
     */
    List<Accommodation> findByStatusPaginated(ApprovalStatus status, int page, int pageSize);

    /**
     * Finds accommodations by supplier with pagination.
     *
     * @param supplierId supplier user ID
     * @param page       page number (0-indexed)
     * @param pageSize   page size
     * @return list of accommodations
     */
    List<Accommodation> findBySupplierIdPaginated(UUID supplierId, int page, int pageSize);

    /**
     * Counts accommodations by supplier.
     *
     * @param supplierId supplier user ID
     * @return count of accommodations by supplier
     */
    long countBySupplierId(UUID supplierId);

    /**
     * Counts accommodations by status.
     *
     * @param status approval status
     * @return count of accommodations with the status
     */
    long countByStatus(ApprovalStatus status);

    /**
     * Counts all accommodations.
     *
     * @return total count of accommodations
     */
    long countAll();

    /**
     * Finds accommodations available for a date range.
     *
     * @param checkInDate  check-in date
     * @param checkOutDate check-out date
     * @return list of available accommodations
     */
    List<Accommodation> findAvailableForDates(LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Finds accommodations available for a date range with guest count.
     *
     * @param checkInDate  check-in date
     * @param checkOutDate check-out date
     * @param guests       number of guests
     * @return list of available accommodations
     */
    List<Accommodation> findAvailableForDatesAndGuests(LocalDate checkInDate, LocalDate checkOutDate, int guests);

    /**
     * Finds accommodations by price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of accommodations within price range
     */
    List<Accommodation> findByPriceRange(double minPrice, double maxPrice);

    /**
     * Finds premium accommodations.
     *
     * @return list of premium accommodations
     */
    List<Accommodation> findPremium();

    /**
     * Finds accommodations with specific amenities.
     *
     * @param amenityNames list of amenity names
     * @return list of accommodations with the amenities
     */
    List<Accommodation> findByAmenities(List<String> amenityNames);

    /**
     * Searches accommodations with multiple criteria.
     */
    List<Accommodation> search(String city, String country, String type, java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice, Integer guests, Integer bedrooms, List<String> amenities,
            Boolean isInstantBook, com.travelplatform.domain.enums.ApprovalStatus status, int page, int pageSize,
            String sortBy, String sortOrder);

    /**
     * Searches accommodations by keyword.
     *
     * @param keyword search term
     * @return list of matching accommodations
     */
    List<Accommodation> searchByKeyword(String keyword);

    /**
     * Finds accommodation images by accommodation ID.
     *
     * @param accommodationId accommodation ID
     * @return list of images
     */
    List<AccommodationImage> findImagesByAccommodationId(UUID accommodationId);

    /**
     * Finds accommodation amenities by accommodation ID.
     *
     * @param accommodationId accommodation ID
     * @return list of amenities
     */
    List<AccommodationAmenity> findAmenitiesByAccommodationId(UUID accommodationId);

    /**
     * Counts total accommodations.
     *
     * @return count of accommodations
     */
    long count();

    /**
     * Finds primary image for an accommodation.
     *
     * @param accommodationId accommodation ID
     * @return optional primary image
     */
    Optional<AccommodationImage> findPrimaryImageByAccommodationId(UUID accommodationId);

    /**
     * Finds accommodations with average rating above threshold.
     *
     * @param minRating minimum average rating
     * @return list of accommodations with rating >= threshold
     */
    List<Accommodation> findByMinAverageRating(double minRating);

    /**
     * Finds accommodations sorted by rating.
     *
     * @param limit maximum number of results
     * @return list of top-rated accommodations
     */
    List<Accommodation> findTopRated(int limit);

    /**
     * Finds accommodations sorted by booking count.
     *
     * @param limit maximum number of results
     * @return list of most-booked accommodations
     */
    List<Accommodation> findMostBooked(int limit);

    /**
     * Finds accommodations sorted by view count.
     *
     * @param limit maximum number of results
     * @return list of most-viewed accommodations
     */
    List<Accommodation> findMostViewed(int limit);

    /**
     * Counts premium accommodations.
     * 
     * @param isPremium is premium
     * @return count
     */
    long countByIsPremium(boolean isPremium);
}
