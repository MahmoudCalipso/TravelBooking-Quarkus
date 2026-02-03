package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.booking.CreateBookingRequest;
import com.travelplatform.application.dto.response.booking.BookingResponse;
import com.travelplatform.application.dto.response.booking.BookingResponse.PaymentInfoResponse;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.booking.BookingPayment;
import com.travelplatform.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Mapper for Booking domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface BookingMapper {

    // Custom mapping methods for Money to BigDecimal
    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal amount, String currency) {
        return amount != null ? new Money(amount, currency) : null;
    }

    // Entity to Response DTO
    default BookingResponse toBookingResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setAccommodationId(booking.getAccommodationId());
        response.setCheckInDate(booking.getDateRange() != null ? booking.getDateRange().getStartDate() : null);
        response.setCheckOutDate(booking.getDateRange() != null ? booking.getDateRange().getEndDate() : null);
        response.setNumberOfGuests(booking.getNumberOfGuests());
        response.setNumberOfAdults(booking.getNumberOfAdults());
        response.setNumberOfChildren(booking.getNumberOfChildren());
        response.setNumberOfInfants(booking.getNumberOfInfants());
        response.setTotalNights((int) booking.getTotalNights());
        response.setBasePricePerNight(moneyToBigDecimal(booking.getBasePricePerNight()));
        response.setTotalBasePrice(moneyToBigDecimal(booking.getTotalBasePrice()));
        response.setServiceFee(moneyToBigDecimal(booking.getServiceFee()));
        response.setCleaningFee(moneyToBigDecimal(booking.getCleaningFee()));
        response.setTaxAmount(moneyToBigDecimal(booking.getTaxAmount()));
        response.setDiscountAmount(moneyToBigDecimal(booking.getDiscountAmount()));
        response.setTotalPrice(moneyToBigDecimal(booking.getTotalPrice()));
        response.setCurrency(booking.getCurrency());
        response.setStatus(booking.getStatus());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setCancellationReason(booking.getCancellationReason());
        response.setCancelledAt(booking.getCancelledAt());
        response.setSpecialRequests(booking.getSpecialRequests());
        response.setGuestMessageToHost(booking.getGuestMessageToHost());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        response.setConfirmedAt(booking.getConfirmedAt());
        response.setPayment(toPaymentInfoResponse(booking.getPayment()));
        return response;
    }

    java.util.List<BookingResponse> toBookingResponseList(java.util.List<Booking> bookings);

    default PaymentInfoResponse toPaymentInfoResponse(BookingPayment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentInfoResponse(
                payment.getId(),
                moneyToBigDecimal(payment.getAmount()),
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
        BigDecimal cleaningFee = new BigDecimal("50.00"); // Fixed cleaning fee
        BigDecimal taxAmount = totalBasePrice.multiply(BigDecimal.valueOf(0.08)); // 8% tax
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal totalPrice = totalBasePrice.add(serviceFee).add(cleaningFee).add(taxAmount).subtract(discountAmount);

        return new Booking(
                userId,
                accommodationId,
                new com.travelplatform.domain.valueobject.DateRange(request.getCheckInDate(), request.getCheckOutDate()),
                request.getNumberOfGuests(),
                request.getNumberOfAdults() != null ? request.getNumberOfAdults() : 0,
                request.getNumberOfChildren() != null ? request.getNumberOfChildren() : 0,
                request.getNumberOfInfants() != null ? request.getNumberOfInfants() : 0,
                new Money(basePricePerNight, "USD"),
                new Money(serviceFee, "USD"),
                new Money(cleaningFee, "USD"),
                new Money(taxAmount, "USD"),
                new Money(discountAmount, "USD"),
                request.getSpecialRequests(),
                request.getGuestMessageToHost());
    }

    default void updateBookingFromRequest(CreateBookingRequest request, @MappingTarget Booking booking) {
        // Booking updates are limited, mainly status changes
    }
}
