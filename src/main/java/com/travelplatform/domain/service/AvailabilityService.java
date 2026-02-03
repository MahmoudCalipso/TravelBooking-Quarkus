package com.travelplatform.domain.service;

import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.valueobject.DateRange;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for availability checking.
 * Handles all availability-related business logic for accommodations.
 */
public class AvailabilityService {

    /**
     * Checks if an accommodation is available for a date range.
     *
     * @param accommodation    accommodation to check
     * @param checkInDate      check-in date
     * @param checkOutDate     check-out date
     * @param existingBookings existing bookings for the accommodation
     * @return true if available, false otherwise
     */
    public boolean isAvailable(Accommodation accommodation, LocalDate checkInDate,
            LocalDate checkOutDate, List<Booking> existingBookings) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
        }
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        if (existingBookings == null) {
            throw new IllegalArgumentException("Existing bookings cannot be null");
        }

        // Check minimum and maximum nights
        int nights = DateRange.calculateNights(checkInDate, checkOutDate);
        if (accommodation.getMinimumNights() > 0 && nights < accommodation.getMinimumNights()) {
            return false;
        }
        if (accommodation.getMaximumNights() > 0 && nights > accommodation.getMaximumNights()) {
            return false;
        }

        // Check for overlapping bookings
        DateRange requestedRange = new DateRange(checkInDate, checkOutDate);

        for (Booking booking : existingBookings) {
            // Skip cancelled or no-show bookings
            if (booking.getStatus().name().equals("CANCELLED") ||
                    booking.getStatus().name().equals("NO_SHOW")) {
                continue;
            }

            DateRange bookingRange = new DateRange(booking.getCheckInDate(), booking.getCheckOutDate());

            if (requestedRange.overlapsWith(bookingRange)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if an accommodation is available for a date range with guest count.
     *
     * @param accommodation    accommodation to check
     * @param checkInDate      check-in date
     * @param checkOutDate     check-out date
     * @param guests           number of guests
     * @param existingBookings existing bookings for the accommodation
     * @return true if available, false otherwise
     */
    public boolean isAvailableWithGuests(Accommodation accommodation, LocalDate checkInDate,
            LocalDate checkOutDate, int guests,
            List<Booking> existingBookings) {
        if (!isAvailable(accommodation, checkInDate, checkOutDate, existingBookings)) {
            return false;
        }

        // Check guest capacity
        if (guests > accommodation.getMaxGuests()) {
            return false;
        }

        return true;
    }

    /**
     * Finds available dates for an accommodation within a date range.
     *
     * @param accommodation    accommodation to check
     * @param startDate        start of search range
     * @param endDate          end of search range
     * @param existingBookings existing bookings for the accommodation
     * @return list of available date ranges
     */
    public List<DateRange> findAvailableDates(Accommodation accommodation, LocalDate startDate,
            LocalDate endDate, List<Booking> existingBookings) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (existingBookings == null) {
            throw new IllegalArgumentException("Existing bookings cannot be null");
        }

        List<DateRange> availableRanges = new java.util.ArrayList<>();
        DateRange searchRange = new DateRange(startDate, endDate);

        // Collect all booked date ranges
        List<DateRange> bookedRanges = new java.util.ArrayList<>();
        for (Booking booking : existingBookings) {
            if (booking.getStatus().name().equals("CANCELLED") ||
                    booking.getStatus().name().equals("NO_SHOW")) {
                continue;
            }

            DateRange bookingRange = new DateRange(booking.getCheckInDate(), booking.getCheckOutDate());
            if (searchRange.overlapsWith(bookingRange)) {
                bookedRanges.add(bookingRange);
            }
        }

        // Find gaps between booked ranges
        if (bookedRanges.isEmpty()) {
            // No bookings, entire range is available
            availableRanges.add(searchRange);
        } else {
            // Sort booked ranges by start date
            bookedRanges.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

            // Find gaps
            LocalDate currentStart = startDate;
            for (DateRange bookedRange : bookedRanges) {
                if (bookedRange.getStartDate().isAfter(currentStart)) {
                    // Gap found
                    availableRanges.add(new DateRange(currentStart, bookedRange.getStartDate()));
                }
                currentStart = bookedRange.getEndDate().isAfter(currentStart) ? bookedRange.getEndDate() : currentStart;
            }

            // Check if there's availability after the last booking
            if (currentStart.isBefore(endDate)) {
                availableRanges.add(new DateRange(currentStart, endDate));
            }
        }

        return availableRanges;
    }

    /**
     * Calculates the maximum number of guests that can be accommodated.
     *
     * @param accommodation accommodation
     * @return maximum guests
     */
    public int getMaxGuests(Accommodation accommodation) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }

        return accommodation.getMaxGuests();
    }

    /**
     * Checks if a booking can be modified (dates changed).
     *
     * @param booking          booking to modify
     * @param newCheckInDate   new check-in date
     * @param newCheckOutDate  new check-out date
     * @param existingBookings existing bookings for the accommodation
     * @return true if modification is possible, false otherwise
     */
    public boolean canModifyBooking(Booking booking, LocalDate newCheckInDate,
            LocalDate newCheckOutDate, List<Booking> existingBookings) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        if (newCheckInDate == null || newCheckOutDate == null) {
            throw new IllegalArgumentException("New check-in and check-out dates cannot be null");
        }
        if (newCheckOutDate.isBefore(newCheckInDate) || newCheckOutDate.isEqual(newCheckInDate)) {
            throw new IllegalArgumentException("New check-out date must be after check-in date");
        }
        if (existingBookings == null) {
            throw new IllegalArgumentException("Existing bookings cannot be null");
        }

        // Check if the booking can be modified based on its status
        if (!canModifyBookingStatus(booking)) {
            return false;
        }

        // Check availability excluding the current booking
        DateRange newRange = new DateRange(newCheckInDate, newCheckOutDate);

        for (Booking existingBooking : existingBookings) {
            // Skip the current booking
            if (existingBooking.getId().equals(booking.getId())) {
                continue;
            }

            // Skip cancelled or no-show bookings
            if (existingBooking.getStatus().name().equals("CANCELLED") ||
                    existingBooking.getStatus().name().equals("NO_SHOW")) {
                continue;
            }

            DateRange existingRange = new DateRange(
                    existingBooking.getCheckInDate(),
                    existingBooking.getCheckOutDate());

            if (newRange.overlapsWith(existingRange)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a booking can be cancelled.
     *
     * @param booking booking to cancel
     * @return true if cancellation is possible, false otherwise
     */
    public boolean canCancelBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        // Check if the booking can be cancelled based on its status
        String status = booking.getStatus().name();
        return status.equals("PENDING") || status.equals("CONFIRMED");
    }

    /**
     * Checks if a booking can be modified based on its status.
     *
     * @param booking booking to check
     * @return true if modification is possible, false otherwise
     */
    private boolean canModifyBookingStatus(Booking booking) {
        String status = booking.getStatus().name();
        return status.equals("PENDING") || status.equals("CONFIRMED");
    }

    /**
     * Calculates the number of available nights for a date range.
     *
     * @param accommodation    accommodation to check
     * @param startDate        start of search range
     * @param endDate          end of search range
     * @param existingBookings existing bookings for the accommodation
     * @return number of available nights
     */
    public int calculateAvailableNights(Accommodation accommodation, LocalDate startDate,
            LocalDate endDate, List<Booking> existingBookings) {
        List<DateRange> availableRanges = findAvailableDates(
                accommodation, startDate, endDate, existingBookings);

        int totalNights = 0;
        for (DateRange range : availableRanges) {
            totalNights += range.getNights();
        }

        return totalNights;
    }

    /**
     * Finds the next available date for an accommodation.
     *
     * @param accommodation    accommodation to check
     * @param searchFromDate   date to start searching from
     * @param existingBookings existing bookings for the accommodation
     * @param minNights        minimum nights required
     * @return next available date, or null if none found
     */
    public LocalDate findNextAvailableDate(Accommodation accommodation, LocalDate searchFromDate,
            List<Booking> existingBookings, int minNights) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }
        if (searchFromDate == null) {
            throw new IllegalArgumentException("Search from date cannot be null");
        }
        if (existingBookings == null) {
            throw new IllegalArgumentException("Existing bookings cannot be null");
        }
        if (minNights <= 0) {
            throw new IllegalArgumentException("Minimum nights must be positive");
        }

        // Search for availability starting from the given date
        LocalDate currentDate = searchFromDate;
        LocalDate maxSearchDate = searchFromDate.plusYears(1); // Search up to 1 year ahead

        while (currentDate.isBefore(maxSearchDate)) {
            LocalDate checkOutDate = currentDate.plusDays(minNights);

            if (isAvailable(accommodation, currentDate, checkOutDate, existingBookings)) {
                return currentDate;
            }

            currentDate = currentDate.plusDays(1);
        }

        return null; // No availability found within search range
    }

    /**
     * Checks if an accommodation has instant book availability.
     *
     * @param accommodation accommodation to check
     * @return true if instant book is enabled, false otherwise
     */
    public boolean isInstantBook(Accommodation accommodation) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }

        return accommodation.isInstantBook();
    }

    /**
     * Validates check-in and check-out dates.
     *
     * @param checkInDate  check-in date
     * @param checkOutDate check-out date
     * @return true if dates are valid, false otherwise
     */
    public boolean areDatesValid(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return false;
        }

        // Check-out must be after check-in
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            return false;
        }

        // Check-in cannot be in the past
        if (checkInDate.isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }

    /**
     * Calculates the minimum check-in date (today).
     *
     * @return minimum check-in date
     */
    public LocalDate getMinCheckInDate() {
        return LocalDate.now();
    }

    /**
     * Calculates the maximum check-in date (1 year from today).
     *
     * @return maximum check-in date
     */
    public LocalDate getMaxCheckInDate() {
        return LocalDate.now().plusYears(1);
    }
}
