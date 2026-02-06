package com.travelplatform.domain.service;

import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for pricing calculations.
 * Handles all pricing-related business logic including discounts,
 * seasonal pricing, and total price calculations.
 */
@jakarta.enterprise.context.ApplicationScoped
public class PricingService {

    /**
     * Calculates the total price for a booking.
     *
     * @param accommodation accommodation to book
     * @param checkInDate   check-in date
     * @param checkOutDate  check-out date
     * @param guests        number of guests
     * @return calculated total price
     */
    public Money calculateTotalPrice(Accommodation accommodation, LocalDate checkInDate,
            LocalDate checkOutDate, int guests) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
        }
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        if (guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be positive");
        }
        if (guests > accommodation.getMaxGuests()) {
            throw new IllegalArgumentException("Number of guests exceeds maximum capacity");
        }

        DateRange dateRange = new DateRange(checkInDate, checkOutDate);
        long nights = dateRange.getNights();

        Money basePrice = accommodation.getBasePrice();
        Money totalBasePrice = basePrice.multiply(nights);

        // Apply guest-based pricing (if more guests, higher price)
        Money guestPrice = calculateGuestPrice(basePrice, guests, accommodation.getMaxGuests());

        // Apply seasonal pricing (if applicable)
        Money seasonalPrice = calculateSeasonalPrice(totalBasePrice, checkInDate, checkOutDate);

        // Calculate total before discounts
        Money subtotal = totalBasePrice.add(guestPrice).add(seasonalPrice);

        return subtotal;
    }

    /**
     * Calculates the service fee for a booking.
     * Service fee is typically a percentage of the total price.
     *
     * @param totalPrice           total booking price
     * @param serviceFeePercentage service fee percentage (e.g., 10.0 for 10%)
     * @return calculated service fee
     */
    public Money calculateServiceFee(Money totalPrice, double serviceFeePercentage) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (serviceFeePercentage < 0 || serviceFeePercentage > 100) {
            throw new IllegalArgumentException("Service fee percentage must be between 0 and 100");
        }

        return totalPrice.percentage(serviceFeePercentage);
    }

    /**
     * Calculates the cleaning fee for a booking.
     *
     * @param accommodation accommodation
     * @param nights        number of nights
     * @return calculated cleaning fee
     */
    public Money calculateCleaningFee(Accommodation accommodation, long nights) {
        if (accommodation == null) {
            throw new IllegalArgumentException("Accommodation cannot be null");
        }
        if (nights <= 0) {
            throw new IllegalArgumentException("Number of nights must be positive");
        }

        // Cleaning fee can be a fixed amount or based on nights
        // For now, we'll use a simple calculation
        Money basePrice = accommodation.getBasePrice();
        return basePrice.percentage(10); // 10% of base price as cleaning fee
    }

    /**
     * Calculates the tax amount for a booking.
     *
     * @param totalPrice total booking price
     * @param taxRate    tax rate (e.g., 0.10 for 10%)
     * @return calculated tax amount
     */
    public Money calculateTax(Money totalPrice, double taxRate) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1");
        }

        return totalPrice.percentage(taxRate * 100);
    }

    /**
     * Calculates the discount amount for a booking.
     *
     * @param totalPrice    total booking price
     * @param discountType  discount type (PERCENTAGE, FIXED_AMOUNT, LONG_STAY,
     *                      EARLY_BIRD)
     * @param discountValue discount value
     * @param nights        number of nights
     * @param daysInAdvance days booked in advance
     * @return calculated discount amount
     */
    public Money calculateDiscount(Money totalPrice, String discountType, double discountValue,
            int nights, int daysInAdvance) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (discountType == null) {
            throw new IllegalArgumentException("Discount type cannot be null");
        }

        switch (discountType.toUpperCase()) {
            case "PERCENTAGE":
                if (discountValue < 0 || discountValue > 100) {
                    throw new IllegalArgumentException("Percentage discount must be between 0 and 100");
                }
                return totalPrice.percentage(discountValue);

            case "FIXED_AMOUNT":
                if (discountValue < 0) {
                    throw new IllegalArgumentException("Fixed discount amount cannot be negative");
                }
                return new Money(discountValue, totalPrice.getCurrency());

            case "LONG_STAY":
                // Discount for longer stays (e.g., 5% for 7+ nights, 10% for 14+ nights)
                if (nights >= 14) {
                    return totalPrice.percentage(10);
                } else if (nights >= 7) {
                    return totalPrice.percentage(5);
                }
                return new Money(0, totalPrice.getCurrency());

            case "EARLY_BIRD":
                // Discount for booking in advance (e.g., 10% for 30+ days, 5% for 14+ days)
                if (daysInAdvance >= 30) {
                    return totalPrice.percentage(10);
                } else if (daysInAdvance >= 14) {
                    return totalPrice.percentage(5);
                }
                return new Money(0, totalPrice.getCurrency());

            default:
                throw new IllegalArgumentException("Unknown discount type: " + discountType);
        }
    }

    /**
     * Calculates the final price after all fees and discounts.
     *
     * @param basePrice      base price
     * @param serviceFee     service fee
     * @param cleaningFee    cleaning fee
     * @param taxAmount      tax amount
     * @param discountAmount discount amount
     * @return final price
     */
    public Money calculateFinalPrice(Money basePrice, Money serviceFee, Money cleaningFee,
            Money taxAmount, Money discountAmount) {
        if (basePrice == null) {
            throw new IllegalArgumentException("Base price cannot be null");
        }
        if (serviceFee == null) {
            throw new IllegalArgumentException("Service fee cannot be null");
        }
        if (cleaningFee == null) {
            throw new IllegalArgumentException("Cleaning fee cannot be null");
        }
        if (taxAmount == null) {
            throw new IllegalArgumentException("Tax amount cannot be null");
        }
        if (discountAmount == null) {
            throw new IllegalArgumentException("Discount amount cannot be null");
        }

        Money subtotal = basePrice.add(serviceFee).add(cleaningFee).add(taxAmount);
        return subtotal.subtract(discountAmount);
    }

    /**
     * Calculates the refund amount for a cancelled booking.
     *
     * @param totalPrice         booking to cancel
     * @param cancellationPolicy cancellation policy
     * @param daysBeforeCheckIn  days before check-in
     * @return refund amount
     */
    public Money calculateRefundAmount(Money totalPrice, String cancellationPolicy,
            int daysBeforeCheckIn) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (cancellationPolicy == null) {
            throw new IllegalArgumentException("Cancellation policy cannot be null");
        }

        // Money totalPrice = booking.getTotalPrice(); // Removed as we pass totalPrice
        // directly

        switch (cancellationPolicy.toUpperCase()) {
            case "FLEXIBLE":
                // Full refund if cancelled 24+ hours before check-in
                if (daysBeforeCheckIn >= 1) {
                    return totalPrice;
                }
                return new Money(0, totalPrice.getCurrency());

            case "MODERATE":
                // Full refund if cancelled 5+ days before, 50% if 1-4 days
                if (daysBeforeCheckIn >= 5) {
                    return totalPrice;
                } else if (daysBeforeCheckIn >= 1) {
                    return totalPrice.percentage(50);
                }
                return new Money(0, totalPrice.getCurrency());

            case "STRICT":
                // 50% refund if cancelled 7+ days before
                if (daysBeforeCheckIn >= 7) {
                    return totalPrice.percentage(50);
                }
                return new Money(0, totalPrice.getCurrency());

            case "SUPER_STRICT":
                // No refund unless cancelled 14+ days before
                if (daysBeforeCheckIn >= 14) {
                    return totalPrice.percentage(50);
                }
                return new Money(0, totalPrice.getCurrency());

            default:
                throw new IllegalArgumentException("Unknown cancellation policy: " + cancellationPolicy);
        }
    }

    /**
     * Calculates guest-based pricing adjustment.
     *
     * @param basePrice base price per night
     * @param guests    number of guests
     * @param maxGuests maximum guests allowed
     * @return guest price adjustment
     */
    private Money calculateGuestPrice(Money basePrice, int guests, int maxGuests) {
        // Additional charge for extra guests beyond a threshold
        int extraGuests = Math.max(0, guests - 2); // First 2 guests included in base price
        if (extraGuests > 0) {
            return basePrice.percentage(10 * extraGuests); // 10% extra per guest
        }
        return new Money(0, basePrice.getCurrency());
    }

    /**
     * Calculates seasonal pricing adjustment.
     *
     * @param basePrice    base price
     * @param checkInDate  check-in date
     * @param checkOutDate check-out date
     * @return seasonal price adjustment
     */
    private Money calculateSeasonalPrice(Money basePrice, LocalDate checkInDate, LocalDate checkOutDate) {
        // Check if dates fall in peak season (e.g., summer months)
        boolean isPeakSeason = isPeakSeason(checkInDate, checkOutDate);

        if (isPeakSeason) {
            return basePrice.percentage(20); // 20% premium for peak season
        }

        // Check if dates fall in off-peak season (e.g., winter months)
        boolean isOffPeak = isOffPeakSeason(checkInDate, checkOutDate);

        if (isOffPeak) {
            return basePrice.percentage(-10); // 10% discount for off-peak
        }

        return new Money(0, basePrice.getCurrency());
    }

    /**
     * Checks if dates fall in peak season.
     *
     * @param checkInDate  check-in date
     * @param checkOutDate check-out date
     * @return true if peak season
     */
    private boolean isPeakSeason(LocalDate checkInDate, LocalDate checkOutDate) {
        // Peak season: June to August (summer)
        return checkInDate.getMonthValue() >= 6 && checkInDate.getMonthValue() <= 8;
    }

    /**
     * Checks if dates fall in off-peak season.
     *
     * @param checkInDate  check-in date
     * @param checkOutDate check-out date
     * @return true if off-peak season
     */
    private boolean isOffPeakSeason(LocalDate checkInDate, LocalDate checkOutDate) {
        // Off-peak: November to February (winter)
        return checkInDate.getMonthValue() >= 11 || checkInDate.getMonthValue() <= 2;
    }

    /**
     * Calculates the average price per night.
     *
     * @param totalPrice total price
     * @param nights     number of nights
     * @return average price per night
     */
    public Money calculateAveragePricePerNight(Money totalPrice, int nights) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (nights <= 0) {
            throw new IllegalArgumentException("Number of nights must be positive");
        }

        return totalPrice.divide(nights);
    }

    /**
     * Calculates the price per guest.
     *
     * @param totalPrice total price
     * @param guests     number of guests
     * @return price per guest
     */
    public Money calculatePricePerGuest(Money totalPrice, int guests) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price cannot be null");
        }
        if (guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be positive");
        }

        return totalPrice.divide(guests);
    }
}
