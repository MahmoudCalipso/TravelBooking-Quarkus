package com.travelplatform.application.service.accommodation;

import com.travelplatform.application.dto.request.accommodation.CreateAccommodationRequest;
import com.travelplatform.application.dto.request.accommodation.SearchAccommodationRequest;
import com.travelplatform.application.dto.response.accommodation.AccommodationResponse;
import com.travelplatform.application.mapper.AccommodationMapper;
import com.travelplatform.application.validator.AccommodationValidator;
import com.travelplatform.domain.enums.ApprovalStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.accommodation.AccommodationAmenity;
import com.travelplatform.domain.model.accommodation.AccommodationImage;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.domain.service.AvailabilityService;
import com.travelplatform.domain.valueobject.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Accommodation operations.
 * Orchestrates accommodation-related business workflows.
 */
@ApplicationScoped
public class AccommodationService {

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    AccommodationMapper accommodationMapper;

    @Inject
    AccommodationValidator accommodationValidator;

    @Inject
    AvailabilityService availabilityService;

    /**
     * Create a new accommodation.
     */
    @Transactional
    public AccommodationResponse createAccommodation(UUID supplierId, CreateAccommodationRequest request) {
        // Validate request
        accommodationValidator.validateAccommodationCreation(request);

        // Verify supplier exists and has correct role
        User supplier = userRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        if (supplier.getRole() != UserRole.SUPPLIER_SUBSCRIBER) {
            throw new IllegalArgumentException("Only suppliers can create accommodations");
        }

        // Create Value Objects
        com.travelplatform.domain.valueobject.Address address = new com.travelplatform.domain.valueobject.Address(
                request.getAddress(),
                request.getCity(),
                request.getStateProvince(),
                request.getCountry(),
                request.getPostalCode());

        Location location = new Location(
                request.getLatitude() != null ? request.getLatitude().doubleValue() : 0.0,
                request.getLongitude() != null ? request.getLongitude().doubleValue() : 0.0);

        com.travelplatform.domain.valueobject.Money basePrice = new com.travelplatform.domain.valueobject.Money(
                request.getBasePrice(),
                request.getCurrency());

        // Create accommodation
        Accommodation accommodation = new Accommodation(
                supplierId,
                com.travelplatform.domain.enums.AccommodationType.valueOf(request.getType()),
                request.getTitle(),
                request.getDescription(),
                address,
                location,
                basePrice,
                request.getMaxGuests());

        // Set optional fields
        if (request.getBedrooms() != null) {
            accommodation.setBedrooms(request.getBedrooms());
        }
        if (request.getBeds() != null) {
            accommodation.setBeds(request.getBeds());
        }
        if (request.getBathrooms() != null) {
            accommodation.setBathrooms(request.getBathrooms());
        }
        if (request.getSquareMeters() != null) {
            accommodation.setSquareMeters(request.getSquareMeters());
        }
        if (request.getCheckInTime() != null) {
            accommodation.setCheckInTime(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            accommodation.setCheckOutTime(request.getCheckOutTime());
        }
        if (request.getMinimumNights() != null) {
            accommodation.setMinimumNights(request.getMinimumNights());
        }
        if (request.getMaximumNights() != null) {
            accommodation.setMaximumNights(request.getMaximumNights());
        }
        if (request.getCancellationPolicy() != null) {
            accommodation.setCancellationPolicy(request.getCancellationPolicy());
        }
        if (request.getDescription() != null) {
            accommodation.setDescription(request.getDescription());
        }

        // Add images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<AccommodationImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImages().size(); i++) {
                AccommodationImage image = new AccommodationImage(
                        accommodation.getId(),
                        request.getImages().get(i),
                        i,
                        i == 0, // First image is primary
                        request.getTitle() // Use title as caption
                );
                images.add(image);
            }
            accommodation.setImages(images);
        }

        // Add amenities
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            List<AccommodationAmenity> amenities = new ArrayList<>();
            for (String amenityName : request.getAmenities()) {
                AccommodationAmenity amenity = new AccommodationAmenity(
                        UUID.randomUUID(),
                        accommodation.getId(),
                        amenityName,
                        "BASIC" // Default category
                );
                amenities.add(amenity);
            }
            accommodation.setAmenities(amenities);
        }

        // Save accommodation
        accommodationRepository.save(accommodation);

        return accommodationMapper.toAccommodationResponse(accommodation);
    }

    /**
     * Get accommodation by ID.
     */
    @Transactional
    public AccommodationResponse getAccommodationById(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        return accommodationMapper.toAccommodationResponse(accommodation);
    }

    /**
     * Search accommodations.
     */
    @Transactional
    public List<AccommodationResponse> searchAccommodations(SearchAccommodationRequest request) {
        // Validate search request
        accommodationValidator.validateSearchRequest(request);

        // Build search criteria
        String city = request.getCity();
        String country = request.getCountry();
        String type = request.getType();
        BigDecimal minPrice = request.getMinPrice();
        BigDecimal maxPrice = request.getMaxPrice();
        Integer maxGuests = request.getMaxGuests();
        Integer bedrooms = request.getBedrooms();
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();
        List<String> amenities = request.getAmenities();
        Boolean isPremium = request.getIsPremium();
        ApprovalStatus status = ApprovalStatus.APPROVED; // Only show approved accommodations
        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "DESC";

        // Search accommodations
        List<Accommodation> accommodations = accommodationRepository.search(
                city, country, type, minPrice, maxPrice, maxGuests, bedrooms,
                amenities, isPremium, status, page, pageSize, sortBy, sortOrder);

        return accommodationMapper.toAccommodationResponseList(accommodations);
    }

    /**
     * Get accommodations by supplier.
     */
    @Transactional
    public List<AccommodationResponse> getAccommodationsBySupplier(UUID supplierId, int page, int pageSize) {
        List<Accommodation> accommodations = accommodationRepository.findBySupplierIdPaginated(supplierId, page,
                pageSize);
        return accommodationMapper.toAccommodationResponseList(accommodations);
    }

    /**
     * Get accommodations by location (nearby).
     */
    @Transactional
    public List<AccommodationResponse> getNearbyAccommodations(double latitude, double longitude, double radiusKm,
            int page, int pageSize) {
        Location location = new Location(latitude, longitude);
        List<Accommodation> accommodations = accommodationRepository.findNearbyPaginated(location, radiusKm, page,
                pageSize);
        return accommodationMapper.toAccommodationResponseList(accommodations);
    }

    /**
     * Check availability for accommodation.
     */
    @Transactional
    public boolean checkAvailability(UUID accommodationId, LocalDate checkIn, LocalDate checkOut, int numberOfGuests) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Check if accommodation is approved
        if (accommodation.getStatus() != ApprovalStatus.APPROVED) {
            return false;
        }

        // Check guest capacity
        if (numberOfGuests > accommodation.getMaxGuests()) {
            return false;
        }

        // Check availability using domain service
        List<com.travelplatform.domain.model.booking.Booking> bookings = bookingRepository
                .findByAccommodationId(accommodationId);
        return availabilityService.isAvailableWithGuests(accommodation, checkIn, checkOut, numberOfGuests, bookings);
    }

    /**
     * Update accommodation.
     */
    @Transactional
    public AccommodationResponse updateAccommodation(UUID supplierId, UUID accommodationId,
            CreateAccommodationRequest request) {
        // Validate request
        accommodationValidator.validateAccommodationCreation(request);

        // Get accommodation
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Verify ownership
        if (!accommodation.getSupplierId().equals(supplierId)) {
            throw new IllegalArgumentException("You can only update your own accommodations");
        }

        // Update fields
        if (request.getTitle() != null) {
            accommodation.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            accommodation.setDescription(request.getDescription());
        }
        if (request.getAddress() != null) {
            accommodation.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            accommodation.setCity(request.getCity());
        }
        if (request.getStateProvince() != null) {
            accommodation.setStateProvince(request.getStateProvince());
        }
        if (request.getCountry() != null) {
            accommodation.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            accommodation.setPostalCode(request.getPostalCode());
        }
        if (request.getLatitude() != null) {
            accommodation.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            accommodation.setLongitude(request.getLongitude());
        }
        if (request.getBasePrice() != null) {
            accommodation.setBasePrice(request.getBasePrice());
        }
        if (request.getCurrency() != null) {
            accommodation.setCurrency(request.getCurrency());
        }
        if (request.getMaxGuests() != null) {
            accommodation.setMaxGuests(request.getMaxGuests());
        }
        if (request.getBedrooms() != null) {
            accommodation.setBedrooms(request.getBedrooms());
        }
        if (request.getBeds() != null) {
            accommodation.setBeds(request.getBeds());
        }
        if (request.getBathrooms() != null) {
            accommodation.setBathrooms(request.getBathrooms());
        }
        if (request.getSquareMeters() != null) {
            accommodation.setSquareMeters(request.getSquareMeters());
        }
        if (request.getCheckInTime() != null) {
            accommodation.setCheckInTime(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            accommodation.setCheckOutTime(request.getCheckOutTime());
        }
        if (request.getMinimumNights() != null) {
            accommodation.setMinimumNights(request.getMinimumNights());
        }
        if (request.getMaximumNights() != null) {
            accommodation.setMaximumNights(request.getMaximumNights());
        }
        if (request.getCancellationPolicy() != null) {
            accommodation.setCancellationPolicy(request.getCancellationPolicy());
        }

        // Save updated accommodation
        accommodationRepository.save(accommodation);

        return accommodationMapper.toAccommodationResponse(accommodation);
    }

    /**
     * Delete accommodation.
     */
    @Transactional
    public void deleteAccommodation(UUID supplierId, UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Verify ownership
        if (!accommodation.getSupplierId().equals(supplierId)) {
            throw new IllegalArgumentException("You can only delete your own accommodations");
        }

        // Delete accommodation
        accommodationRepository.deleteById(accommodationId);
    }

    /**
     * Increment view count.
     */
    @Transactional
    public void incrementViewCount(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        accommodation.incrementViewCount();
        accommodationRepository.save(accommodation);
    }

    /**
     * Update average rating.
     */
    @Transactional
    public void updateAverageRating(UUID accommodationId, double newRating) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        accommodation.updateAverageRating(newRating);
        accommodationRepository.save(accommodation);
    }

    /**
     * Increment booking count.
     */
    @Transactional
    public void incrementBookingCount(UUID accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        accommodation.incrementBookingCount();
        accommodationRepository.save(accommodation);
    }
}
