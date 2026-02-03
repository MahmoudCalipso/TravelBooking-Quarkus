package com.travelplatform.application.validator;

import com.travelplatform.application.dto.request.accommodation.CreateAccommodationRequest;
import com.travelplatform.application.dto.request.accommodation.SearchAccommodationRequest;
import com.travelplatform.domain.enums.AccommodationType;
import com.travelplatform.domain.service.ValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Validator for accommodation-related operations.
 * Provides additional validation beyond bean validation annotations.
 */
@ApplicationScoped
public class AccommodationValidator {

    private static final BigDecimal MIN_PRICE = BigDecimal.ZERO;
    private static final BigDecimal MAX_PRICE = new BigDecimal("100000");
    private static final int MIN_GUESTS = 1;
    private static final int MAX_GUESTS = 50;
    private static final int MIN_BEDROOMS = 0;
    private static final int MAX_BEDROOMS = 50;
    private static final int MIN_BEDS = 0;
    private static final int MAX_BEDS = 100;
    private static final int MIN_BATHROOMS = 0;
    private static final BigDecimal MAX_BATHROOMS = new BigDecimal("50");
    private static final int MIN_SQUARE_METERS = 0;
    private static final int MAX_SQUARE_METERS = 10000;
    private static final int MIN_NIGHTS = 1;
    private static final int MAX_NIGHTS = 365;

    @Inject
    ValidationService validationService;

    /**
     * Validates accommodation creation request.
     */
    public void validateAccommodationCreation(CreateAccommodationRequest request) {
        // Type validation
        if (request.getType() != null) {
            try {
                AccommodationType.valueOf(request.getType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid accommodation type");
            }
        }

        // Price validation
        if (request.getBasePrice() != null) {
            if (request.getBasePrice().compareTo(MIN_PRICE) < 0) {
                throw new IllegalArgumentException("Base price cannot be negative");
            }
            if (request.getBasePrice().compareTo(MAX_PRICE) > 0) {
                throw new IllegalArgumentException("Base price exceeds maximum allowed");
            }
        }

        // Guest capacity validation
        if (request.getMaxGuests() != null) {
            if (request.getMaxGuests() < MIN_GUESTS) {
                throw new IllegalArgumentException("Maximum guests must be at least " + MIN_GUESTS);
            }
            if (request.getMaxGuests() > MAX_GUESTS) {
                throw new IllegalArgumentException("Maximum guests exceeds maximum allowed");
            }
        }

        // Bedrooms validation
        if (request.getBedrooms() != null) {
            if (request.getBedrooms() < MIN_BEDROOMS) {
                throw new IllegalArgumentException("Bedrooms cannot be negative");
            }
            if (request.getBedrooms() > MAX_BEDROOMS) {
                throw new IllegalArgumentException("Bedrooms exceeds maximum allowed");
            }
        }

        // Beds validation
        if (request.getBeds() != null) {
            if (request.getBeds() < MIN_BEDS) {
                throw new IllegalArgumentException("Beds cannot be negative");
            }
            if (request.getBeds() > MAX_BEDS) {
                throw new IllegalArgumentException("Beds exceeds maximum allowed");
            }
        }

        // Bathrooms validation
        if (request.getBathrooms() != null) {
            if (request.getBathrooms().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Bathrooms cannot be negative");
            }
            if (request.getBathrooms().compareTo(MAX_BATHROOMS) > 0) {
                throw new IllegalArgumentException("Bathrooms exceeds maximum allowed");
            }
        }

        // Square meters validation
        if (request.getSquareMeters() != null) {
            if (request.getSquareMeters() < MIN_SQUARE_METERS) {
                throw new IllegalArgumentException("Square meters cannot be negative");
            }
            if (request.getSquareMeters() > MAX_SQUARE_METERS) {
                throw new IllegalArgumentException("Square meters exceeds maximum allowed");
            }
        }

        // Minimum nights validation
        if (request.getMinimumNights() != null) {
            if (request.getMinimumNights() < MIN_NIGHTS) {
                throw new IllegalArgumentException("Minimum nights must be at least " + MIN_NIGHTS);
            }
            if (request.getMinimumNights() > MAX_NIGHTS) {
                throw new IllegalArgumentException("Minimum nights exceeds maximum allowed");
            }
        }

        // Maximum nights validation
        if (request.getMaximumNights() != null) {
            if (request.getMaximumNights() < MIN_NIGHTS) {
                throw new IllegalArgumentException("Maximum nights must be at least " + MIN_NIGHTS);
            }
            if (request.getMaximumNights() > MAX_NIGHTS) {
                throw new IllegalArgumentException("Maximum nights exceeds maximum allowed");
            }
        }

        // Check-in/Check-out time validation
        if (request.getCheckInTime() != null && request.getCheckOutTime() != null) {
            if (request.getCheckInTime().isAfter(request.getCheckOutTime())) {
                throw new IllegalArgumentException("Check-in time must be before check-out time");
            }
        }

        // Use domain validation service
        validationService.validateAccommodationDetails(
                request.getTitle(),
                request.getDescription(),
                request.getAddress(),
                request.getCity(),
                request.getCountry(),
                new com.travelplatform.domain.valueobject.Money(request.getBasePrice(),
                        request.getCurrency() != null ? request.getCurrency() : "USD"),
                request.getMaxGuests(),
                request.getMinimumNights(),
                request.getMaximumNights(),
                request.getLatitude() != null
                        ? new com.travelplatform.domain.valueobject.Location(request.getLatitude().doubleValue(),
                                request.getLongitude().doubleValue())
                        : null);
    }

    /**
     * Validates accommodation search request.
     */
    public void validateSearchRequest(SearchAccommodationRequest request) {
        // Date range validation
        if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
            if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
                throw new IllegalArgumentException("Check-in date must be before check-out date");
            }

            if (request.getCheckInDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Check-in date cannot be in the past");
            }

            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    request.getCheckInDate(),
                    request.getCheckOutDate());
            if (nights > MAX_NIGHTS) {
                throw new IllegalArgumentException("Stay duration exceeds maximum allowed");
            }
        }

        // Price range validation
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            if (request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
                throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
            }
        }

        // Guest count validation
        if (request.getNumberOfGuests() != null) {
            if (request.getNumberOfGuests() < MIN_GUESTS) {
                throw new IllegalArgumentException("Number of guests must be at least " + MIN_GUESTS);
            }
            if (request.getNumberOfGuests() > MAX_GUESTS) {
                throw new IllegalArgumentException("Number of guests exceeds maximum allowed");
            }
        }

        // Pagination validation
        if (request.getPage() != null && request.getPage() < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (request.getPageSize() != null) {
            if (request.getPageSize() < 1) {
                throw new IllegalArgumentException("Page size must be at least 1");
            }
            if (request.getPageSize() > 100) {
                throw new IllegalArgumentException("Page size cannot exceed 100");
            }
        }
    }

    /**
     * Validates accommodation images.
     */
    public void validateAccommodationImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new IllegalArgumentException("At least one image is required");
        }

        if (imageUrls.size() > 50) {
            throw new IllegalArgumentException("Maximum 50 images allowed");
        }

        // Check for primary image
        long primaryCount = imageUrls.stream()
                .filter(url -> url != null && url.startsWith("primary:"))
                .count();

        if (primaryCount > 1) {
            throw new IllegalArgumentException("Only one primary image is allowed");
        }
    }

    /**
     * Validates accommodation amenities.
     */
    public void validateAccommodationAmenities(List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            throw new IllegalArgumentException("At least one amenity is required");
        }

        if (amenities.size() > 100) {
            throw new IllegalArgumentException("Maximum 100 amenities allowed");
        }

        // Check for duplicates
        long uniqueCount = amenities.stream().distinct().count();
        if (uniqueCount != amenities.size()) {
            throw new IllegalArgumentException("Duplicate amenities are not allowed");
        }
    }
}
