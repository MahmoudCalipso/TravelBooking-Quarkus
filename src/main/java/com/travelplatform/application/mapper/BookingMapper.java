package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.booking.CreateBookingRequest;
import com.travelplatform.application.dto.response.booking.BookingResponse;
import com.travelplatform.application.dto.response.booking.BookingResponse.PaymentInfoResponse;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.booking.BookingPayment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Mapper for Booking domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface BookingMapper {

    // Entity to Response DTO
    BookingResponse toBookingResponse(Booking booking);

    java.util.List<BookingResponse> toBookingResponseList(java.util.List<Booking> bookings);

    default PaymentInfoResponse toPaymentInfoResponse(BookingPayment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentInfoResponse(
                payment.getId(),
                payment.getAmount().getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getPaymentProvider(),
                payment.getTransactionId(),
                payment.getStatus(),
                payment.getPaidAt());
    }

    // Request DTO to Entity
    default Booking toBookingFromRequest(CreateBookingRequest request, UUID userId, UUID accommodationId,
            BigDecimal basePricePerNight, Integer totalNights) {
        // Calculate pricing
        BigDecimal totalBasePrice = basePricePerNight.multiply(BigDecimal.valueOf(totalNights));
        BigDecimal serviceFee = totalBasePrice.multiply(BigDecimal.valueOf(0.10)); // 10% service fee
        BigDecimal cleaningFee = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal totalPrice = totalBasePrice.add(serviceFee).add(cleaningFee).add(taxAmount).subtract(discountAmount);

        com.travelplatform.domain.valueobject.DateRange dateRange = new com.travelplatform.domain.valueobject.DateRange(
                request.getCheckInDate(), request.getCheckOutDate());

        String currencyCode = "USD"; // Default or passed context needed
        com.travelplatform.domain.valueobject.Money basePriceMoney = new com.travelplatform.domain.valueobject.Money(
                basePricePerNight, currencyCode);
        com.travelplatform.domain.valueobject.Money serviceFeeMoney = new com.travelplatform.domain.valueobject.Money(
                serviceFee, currencyCode);
        com.travelplatform.domain.valueobject.Money cleaningFeeMoney = new com.travelplatform.domain.valueobject.Money(
                cleaningFee, currencyCode);
        com.travelplatform.domain.valueobject.Money taxMoney = new com.travelplatform.domain.valueobject.Money(
                taxAmount, currencyCode);
        com.travelplatform.domain.valueobject.Money discountMoney = new com.travelplatform.domain.valueobject.Money(
                discountAmount, currencyCode);

        return new Booking(
                userId,
                accommodationId,
                dateRange,
                request.getNumberOfGuests(),
                request.getNumberOfAdults(),
                request.getNumberOfChildren(),
                request.getNumberOfInfants(),
                basePriceMoney,
                serviceFeeMoney,
                cleaningFeeMoney,
                taxMoney,
                discountMoney,
                request.getSpecialRequests(),
                request.getGuestMessageToHost());
    }

    default Integer calculateTotalNights(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    @Named("userId")
    default UUID mapUserId(Booking booking) {
        return booking != null ? booking.getUserId() : null;
    }

    @Named("userName")
    default String mapUserName(Booking booking) {
        // TODO: Fix domain model navigation. Booking only has userId.
        return null;
        /*
         * return booking != null && booking.getUser() != null &&
         * booking.getUser().getProfile() != null
         * ? booking.getUser().getProfile().getFullName()
         * : null;
         */
    }

    @Named("accommodationId")
    default UUID mapAccommodationId(Booking booking) {
        return booking != null ? booking.getAccommodationId() : null;
    }

    @Named("accommodationTitle")
    default String mapAccommodationTitle(Booking booking) {
        // TODO: Fix domain model navigation or fetch external data. Booking only has
        // accommodationId.
        return null;
        /*
         * return booking != null && booking.getAccommodation() != null
         * ? booking.getAccommodation().getTitle()
         * : null;
         */
    }

    @Named("accommodationAddress")
    default String mapAccommodationAddress(Booking booking) {
        // TODO: Fix domain model navigation or fetch external data. Booking only has
        // accommodationId.
        return null;
        /*
         * return booking != null && booking.getAccommodation() != null
         * ? booking.getAccommodation().getAddress()
         * : null;
         */
    }

    @Named("accommodationCity")
    default String mapAccommodationCity(Booking booking) {
        // TODO: Fix domain model navigation or fetch external data. Booking only has
        // accommodationId.
        return null;
        /*
         * return booking != null && booking.getAccommodation() != null
         * ? booking.getAccommodation().getCity()
         * : null;
         */
    }

    @Named("accommodationCountry")
    default String mapAccommodationCountry(Booking booking) {
        // TODO: Fix domain model navigation or fetch external data. Booking only has
        // accommodationId.
        return null;
        /*
         * return booking != null && booking.getAccommodation() != null
         * ? booking.getAccommodation().getCountry()
         * : null;
         */
    }

    @Named("status")
    default BookingStatus mapStatus(Booking booking) {
        return booking != null ? booking.getStatus() : null;
    }

    @Named("paymentStatus")
    default PaymentStatus mapPaymentStatus(Booking booking) {
        return booking != null && booking.getPayment() != null ? booking.getPayment().getStatus() : null;
    }
}
