package com.travelplatform.application.dto.request.booking;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating booking request.
 */
public class CreateBookingRequest {

    @NotNull(message = "Accommodation ID is required")
    private UUID accommodationId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer numberOfGuests;

    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer numberOfAdults = 1;

    @Min(value = 0, message = "Number of children cannot be negative")
    private Integer numberOfChildren = 0;

    @Min(value = 0, message = "Number of infants cannot be negative")
    private Integer numberOfInfants = 0;

    @Size(max = 1000, message = "Special requests must be less than 1000 characters")
    private String specialRequests;

    @Size(max = 1000, message = "Guest message must be less than 1000 characters")
    private String guestMessageToHost;

    // Getters and Setters

    public UUID getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(UUID accommodationId) {
        this.accommodationId = accommodationId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public Integer getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(Integer numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public Integer getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(Integer numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public Integer getNumberOfInfants() {
        return numberOfInfants;
    }

    public void setNumberOfInfants(Integer numberOfInfants) {
        this.numberOfInfants = numberOfInfants;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getGuestMessageToHost() {
        return guestMessageToHost;
    }

    public void setGuestMessageToHost(String guestMessageToHost) {
        this.guestMessageToHost = guestMessageToHost;
    }
}
