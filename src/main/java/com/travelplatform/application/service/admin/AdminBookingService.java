package com.travelplatform.application.service.admin;

import com.travelplatform.application.dto.response.booking.BookingResponse;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.booking.BookingPayment;
import com.travelplatform.domain.repository.BookingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminBookingService {

    @Inject
    BookingRepository bookingRepository;

    public PaginatedResponse<BookingResponse> listBookings(String status, LocalDate startDate, LocalDate endDate, int page, int size) {
        List<Booking> items;
        if (startDate != null && endDate != null) {
            items = bookingRepository.findByDateRange(startDate, endDate);
        } else {
            items = bookingRepository.findAll();
        }
        if (status != null && !status.isBlank()) {
            BookingStatus st = BookingStatus.valueOf(status);
            items = items.stream().filter(b -> b.getStatus() == st).collect(Collectors.toList());
        }
        items = items.stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
        int from = Math.max(0, page * size);
        int to = Math.min(items.size(), from + size);
        List<BookingResponse> pageItems = from < to
                ? items.subList(from, to).stream().map(this::toResponse).collect(Collectors.toList())
                : List.of();
        return PaginatedResponse.of(pageItems, items.size(), page, size);
    }

    public BookingResponse getBooking(UUID bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        return booking.map(this::toResponse).orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Booking not found"));
    }

    @Transactional
    public void cancelBooking(UUID bookingId, UUID adminId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Booking not found"));
        booking.cancel(reason, adminId);
        bookingRepository.update(booking);
    }

    private BookingResponse toResponse(Booking booking) {
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setUserId(booking.getUserId());
        resp.setAccommodationId(booking.getAccommodationId());
        resp.setCheckInDate(booking.getCheckInDate());
        resp.setCheckOutDate(booking.getCheckOutDate());
        resp.setNumberOfGuests(booking.getNumberOfGuests());
        resp.setNumberOfAdults(booking.getNumberOfAdults());
        resp.setNumberOfChildren(booking.getNumberOfChildren());
        resp.setNumberOfInfants(booking.getNumberOfInfants());
        resp.setTotalNights((int) booking.getTotalNights());
        if (booking.getBasePricePerNight() != null) {
            resp.setBasePricePerNight(booking.getBasePricePerNight().getAmount());
        }
        if (booking.getTotalBasePrice() != null) {
            resp.setTotalBasePrice(booking.getTotalBasePrice().getAmount());
        }
        if (booking.getServiceFee() != null) {
            resp.setServiceFee(booking.getServiceFee().getAmount());
        }
        if (booking.getCleaningFee() != null) {
            resp.setCleaningFee(booking.getCleaningFee().getAmount());
        }
        if (booking.getTaxAmount() != null) {
            resp.setTaxAmount(booking.getTaxAmount().getAmount());
        }
        if (booking.getDiscountAmount() != null) {
            resp.setDiscountAmount(booking.getDiscountAmount().getAmount());
        }
        if (booking.getTotalPrice() != null) {
            resp.setTotalPrice(booking.getTotalPrice().getAmount());
            resp.setCurrency(booking.getTotalPrice().getCurrencyCode());
        }
        resp.setStatus(booking.getStatus());
        resp.setPaymentStatus(booking.getPaymentStatus());
        resp.setCancellationReason(booking.getCancellationReason());
        resp.setCancelledAt(booking.getCancelledAt());
        resp.setSpecialRequests(booking.getSpecialRequests());
        resp.setGuestMessageToHost(booking.getGuestMessageToHost());
        resp.setCreatedAt(booking.getCreatedAt());
        resp.setUpdatedAt(booking.getUpdatedAt());
        resp.setConfirmedAt(booking.getConfirmedAt());

        BookingPayment payment = booking.getPayment();
        if (payment != null) {
            BookingResponse.PaymentInfoResponse p = new BookingResponse.PaymentInfoResponse(
                    payment.getId(),
                    payment.getAmount() != null ? payment.getAmount().getAmount() : null,
                    payment.getAmount() != null ? payment.getAmount().getCurrencyCode() : null,
                    payment.getPaymentMethod(),
                    payment.getPaymentProvider(),
                    payment.getTransactionId(),
                    payment.getStatus(),
                    payment.getPaidAt()
            );
            resp.setPayment(p);
        }
        return resp;
    }
}
