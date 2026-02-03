package com.travelplatform.application.validator;

import com.travelplatform.application.dto.request.booking.CreateBookingRequest;
import com.travelplatform.domain.service.ValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Validator for booking-related operations.
 * Provides additional validation beyond bean validation annotations.
 */
@ApplicationScoped
public class BookingValidator {

    private static final int MIN_GUESTS = 1;
    private static final int MAX_GUESTS = 50;
    private static final int MIN_NIGHTS = 1;
    private static final int MAX_NIGHTS = 365;
    private static final int MIN_ADULTS = 1;
    private static final int MAX_ADULTS = 50;
    private static final int MAX_CHILDREN = 20;
    private static final int MAX_INFANTS = 10;
    private static final int MAX_SPECIAL_REQUESTS_LENGTH = 500;
    private static final int MAX_GUEST_MESSAGE_LENGTH = 1000;

    @Inject
    ValidationService validationService;

    /**
     * Validates booking creation request.
     */
    public void validateBookingCreation(CreateBookingRequest request) {
        // Accommodation ID validation
        if (request.getAccommodationId() == null) {
            throw new IllegalArgumentException("Accommodation ID is required");
        }

        // Date validation
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }

        LocalDate today = LocalDate.now();

        if (request.getCheckInDate().isBefore(today)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights < MIN_NIGHTS) {
            throw new IllegalArgumentException("Minimum stay duration is " + MIN_NIGHTS + " night");
        }
        if (nights > MAX_NIGHTS) {
            throw new IllegalArgumentException("Maximum stay duration is " + MAX_NIGHTS + " nights");
        }

        // Guest count validation
        if (request.getNumberOfGuests() != null) {
            if (request.getNumberOfGuests() < MIN_GUESTS) {
                throw new IllegalArgumentException("At least " + MIN_GUESTS + " guest is required");
            }
            if (request.getNumberOfGuests() > MAX_GUESTS) {
                throw new IllegalArgumentException("Maximum " + MAX_GUESTS + " guests allowed");
            }
        }

        // Adults validation
        if (request.getNumberOfAdults() != null) {
            if (request.getNumberOfAdults() < MIN_ADULTS) {
                throw new IllegalArgumentException("At least " + MIN_ADULTS + " adult is required");
            }
            if (request.getNumberOfAdults() > MAX_ADULTS) {
                throw new IllegalArgumentException("Maximum " + MAX_ADULTS + " adults allowed");
            }
        }

        // Children validation
        if (request.getNumberOfChildren() != null) {
            if (request.getNumberOfChildren() < 0) {
                throw new IllegalArgumentException("Number of children cannot be negative");
            }
            if (request.getNumberOfChildren() > MAX_CHILDREN) {
                throw new IllegalArgumentException("Maximum " + MAX_CHILDREN + " children allowed");
            }
        }

        // Infants validation
        if (request.getNumberOfInfants() != null) {
            if (request.getNumberOfInfants() < 0) {
                throw new IllegalArgumentException("Number of infants cannot be negative");
            }
            if (request.getNumberOfInfants() > MAX_INFANTS) {
                throw new IllegalArgumentException("Maximum " + MAX_INFANTS + " infants allowed");
            }
        }

        // Validate total guests match
        int totalGuests = (request.getNumberOfAdults() != null ? request.getNumberOfAdults() : 0) +
                           (request.getNumberOfChildren() != null ? request.getNumberOfChildren() : 0) +
                           (request.getNumberOfInfants() != null ? request.getNumberOfInfants() : 0);

        if (request.getNumberOfGuests() != null && request.getNumberOfGuests() != totalGuests) {
            throw new IllegalArgumentException("Number of guests must match the sum of adults, children, and infants");
        }

        // Special requests validation
        if (request.getSpecialRequests() != null && request.getSpecialRequests().length() > MAX_SPECIAL_REQUESTS_LENGTH) {
            throw new IllegalArgumentException("Special requests cannot exceed " + MAX_SPECIAL_REQUESTS_LENGTH + " characters");
        }

        // Guest message validation
        if (request.getGuestMessageToHost() != null && request.getGuestMessageToHost().length() > MAX_GUEST_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Guest message cannot exceed " + MAX_GUEST_MESSAGE_LENGTH + " characters");
        }

        // Use domain validation service
        validationService.validateBooking(
            request.getAccommodationId(),
            request.getCheckInDate(),
            request.getCheckOutDate(),
            request.getNumberOfGuests()
        );
    }

    /**
     * Validates booking cancellation.
     */
    public void validateBookingCancellation(LocalDate checkInDate, LocalDate checkOutDate, String cancellationReason) {
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            // Past booking - cannot cancel
            throw new IllegalArgumentException("Cannot cancel a booking that has already started");
        }

        if (cancellationReason != null && cancellationReason.length() > 500) {
            throw new IllegalArgumentException("Cancellation reason cannot exceed 500 characters");
        }
    }

    /**
     * Validates booking modification.
     */
    public void validateBookingModification(LocalDate checkInDate, LocalDate checkOutDate, 
                                       LocalDate newCheckInDate, LocalDate newCheckOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            throw new IllegalArgumentException("Cannot modify a booking that has already started");
        }

        if (newCheckInDate != null && newCheckInDate.isBefore(today)) {
            throw new IllegalArgumentException("New check-in date cannot be in the past");
        }

        if (newCheckInDate != null && newCheckOutDate != null) {
            if (newCheckOutDate.isBefore(newCheckInDate)) {
                throw new IllegalArgumentException("New check-out date must be after check-in date");
            }

            long nights = ChronoUnit.DAYS.between(newCheckInDate, newCheckOutDate);
            if (nights < MIN_NIGHTS) {
                throw new IllegalArgumentException("Minimum stay duration is " + MIN_NIGHTS + " night");
            }
            if (nights > MAX_NIGHTS) {
                throw new IllegalArgumentException("Maximum stay duration is " + MAX_NIGHTS + " nights");
            }
        }
    }
}
