package com.travelplatform.application.service.booking;

import com.travelplatform.application.dto.request.booking.CreateBookingRequest;
import com.travelplatform.application.dto.response.booking.BookingResponse;
import com.travelplatform.application.mapper.BookingMapper;
import com.travelplatform.application.validator.BookingValidator;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.booking.BookingPayment;
import com.travelplatform.domain.model.booking.BookingFeeConfig;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.domain.service.AvailabilityService;
import com.travelplatform.domain.service.PricingService;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;
import com.travelplatform.infrastructure.payment.PaymentException;
import com.travelplatform.infrastructure.payment.PaymentGateway;
import com.travelplatform.interfaces.websocket.AvailabilityWebSocketEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Booking operations.
 * Orchestrates booking-related business workflows.
 */
@ApplicationScoped
public class BookingService {

    @Inject
    BookingRepository bookingRepository;

    @Inject
    PaymentGateway paymentGateway;

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    BookingMapper bookingMapper;

    @Inject
    BookingValidator bookingValidator;

    @Inject
    AvailabilityService availabilityService;

    @Inject
    PricingService pricingService;

    @Inject
    BookingFeeConfigService bookingFeeConfigService;

    /**
     * Create a new booking.
     */
    @Transactional
    public BookingResponse createBooking(UUID userId, CreateBookingRequest request) {
        // Validate request
        bookingValidator.validateBookingCreation(request);

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get accommodation
        Accommodation accommodation = accommodationRepository.findById(request.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        // Check if accommodation is approved
        if (accommodation.getStatus() != com.travelplatform.domain.enums.ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Accommodation is not available for booking");
        }

        // Calculate date range
        DateRange dateRange = new DateRange(request.getCheckInDate(), request.getCheckOutDate());

        // Check availability
        List<Booking> existingBookings = bookingRepository.findOverlappingBookings(
                request.getAccommodationId(),
                request.getCheckInDate(),
                request.getCheckOutDate());

        if (!availabilityService.isAvailable(accommodation, request.getCheckInDate(), request.getCheckOutDate(),
                existingBookings)) {
            throw new IllegalArgumentException("Accommodation is not available for the selected dates");
        }

        // Validate guest count
        if (request.getNumberOfGuests() > accommodation.getMaxGuests()) {
            throw new IllegalArgumentException("Number of guests exceeds maximum capacity");
        }

        // Calculate pricing
        long totalNights = dateRange.getNights();
        Money basePricePerNight = accommodation.getBasePrice();
        Money totalBasePrice = pricingService.calculateTotalPrice(accommodation, request.getCheckInDate(),
                request.getCheckOutDate(), request.getNumberOfGuests());
        BookingFeeConfig feeConfig = bookingFeeConfigService.getActiveConfig();
        Money serviceFee = pricingService.calculateServiceFee(totalBasePrice,
                feeConfig.getServiceFeePercentage().doubleValue());
        serviceFee = applyServiceFeeBounds(serviceFee, feeConfig);
        Money cleaningFee = pricingService.calculateServiceFee(totalBasePrice,
                feeConfig.getCleaningFeePercentage().doubleValue());
        Money taxAmount = pricingService.calculateTax(totalBasePrice.add(serviceFee),
                feeConfig.getTaxRate().doubleValue());
        Money discountAmount = new Money(BigDecimal.ZERO, accommodation.getCurrency());
        Money totalPrice = totalBasePrice.add(serviceFee).add(cleaningFee).add(taxAmount);

        // Create booking
        Booking booking = new Booking(
                userId,
                request.getAccommodationId(),
                dateRange,
                request.getNumberOfGuests(),
                request.getNumberOfAdults(),
                request.getNumberOfChildren() != null ? request.getNumberOfChildren() : 0,
                request.getNumberOfInfants() != null ? request.getNumberOfInfants() : 0,
                basePricePerNight,
                serviceFee,
                cleaningFee,
                taxAmount,
                discountAmount,
                request.getSpecialRequests(),
                request.getGuestMessageToHost());

        // Create payment record
        BookingPayment payment = new BookingPayment(
                booking.getId(),
                totalPrice,
                accommodation.getCurrency() != null ? accommodation.getCurrency() : "USD",
                BookingPayment.PaymentMethod.CARD, // Default payment method
                "STRIPE" // Default payment provider
        );
        booking.setPayment(payment);

        // Save booking
        bookingRepository.save(booking);

        // Broadcast availability change
        AvailabilityWebSocketEndpoint.broadcastAvailabilityChange(booking.getAccommodationId(),
                booking.getCheckInDate(), false);

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Initiate payment for a booking using Stripe Connect if applicable.
     */
    @Transactional
    public PaymentGateway.PaymentIntent initiatePayment(UUID userId, UUID bookingId) throws PaymentException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to pay for this booking");
        }

        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        User supplier = userRepository.findById(accommodation.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        String stripeConnectId = supplier.getProfile() != null ? supplier.getProfile().getStripeConnectAccountId()
                : null;

        if (stripeConnectId != null) {
            // Split payment: Supplier receives total base + cleaning, Platform takes
            // service fee + tax?
            // Actually platform fee is service fee.
            BigDecimal appFee = booking.getServiceFee().getAmount();
            return paymentGateway.createPaymentIntentWithTransfer(
                    booking.getId(),
                    booking.getTotalPrice().getAmount(),
                    booking.getTotalPrice().getCurrency(),
                    "CARD",
                    "Booking for " + accommodation.getTitle(),
                    stripeConnectId,
                    appFee);
        } else {
            // Standard payment to platform
            return paymentGateway.createPaymentIntent(
                    booking.getId(),
                    booking.getTotalPrice().getAmount(),
                    booking.getTotalPrice().getCurrency(),
                    "CARD",
                    "Booking for " + accommodation.getTitle());
        }
    }

    /**
     * Get booking by ID.
     */
    @Transactional
    public BookingResponse getBookingById(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify ownership (Traveler who booked it or Supplier who owns the
        // accommodation)
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (!booking.getUserId().equals(userId) && !accommodation.getSupplierId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to view this booking");
        }

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Get bookings by user.
     */
    @Transactional
    public List<BookingResponse> getBookingsByUser(UUID userId, BookingStatus status, int page, int pageSize) {
        List<Booking> bookings = bookingRepository.findByUserIdPaginated(userId, status, page, pageSize);
        return bookingMapper.toBookingResponseList(bookings);
    }

    /**
     * Get bookings by accommodation (for suppliers).
     */
    @Transactional
    public List<BookingResponse> getBookingsByAccommodation(UUID accommodationId, BookingStatus status, int page,
            int pageSize) {
        List<Booking> bookings = bookingRepository.findByAccommodationIdPaginated(accommodationId, status, page,
                pageSize);
        return bookingMapper.toBookingResponseList(bookings);
    }

    /**
     * Get bookings by supplier.
     */
    @Transactional
    public List<BookingResponse> getBookingsBySupplier(UUID supplierId, BookingStatus status, int page, int pageSize) {
        List<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findBySupplierIdAndStatus(supplierId, status);
        } else {
            bookings = bookingRepository.findBySupplierIdPaginated(supplierId, page, pageSize);
        }
        return bookingMapper.toBookingResponseList(bookings);
    }

    /**
     * Get bookings by status.
     */
    @Transactional
    public List<BookingResponse> getBookingsByStatus(BookingStatus status, int page, int pageSize) {
        List<Booking> bookings = bookingRepository.findByStatusPaginated(status, page, pageSize);
        return bookingMapper.toBookingResponseList(bookings);
    }

    /**
     * Confirm booking.
     */
    @Transactional
    public BookingResponse confirmBooking(UUID supplierId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify supplier ownership
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (!accommodation.getSupplierId().equals(supplierId)) {
            throw new IllegalArgumentException("You can only confirm bookings for your own accommodations");
        }

        // Check if booking can be confirmed
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Booking cannot be confirmed in current status");
        }

        // Confirm booking
        booking.confirm();
        bookingRepository.save(booking);

        // Update accommodation booking count
        accommodation.incrementBookingCount();
        accommodationRepository.save(accommodation);

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Cancel booking.
     */
    @Transactional
    public BookingResponse cancelBooking(UUID userId, UUID bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify ownership
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only cancel your own bookings");
        }

        // Check if booking can be cancelled
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Booking cannot be cancelled in current status");
        }

        // Cancel booking
        booking.cancel(reason, userId);
        bookingRepository.save(booking);

        // Release availability for the start date
        AvailabilityWebSocketEndpoint.broadcastAvailabilityChange(booking.getAccommodationId(),
                booking.getCheckInDate(), true);

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Complete booking.
     */
    @Transactional
    public BookingResponse completeBooking(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify ownership (Traveler or Supplier)
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (!booking.getUserId().equals(userId) && !accommodation.getSupplierId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to complete this booking");
        }

        // Check if booking can be completed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Booking cannot be completed in current status");
        }

        // Complete booking
        booking.complete();
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Process payment.
     */
    @Transactional
    public BookingResponse processPayment(UUID userId, UUID bookingId, String transactionId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify ownership (Only Traveler can pay)
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only process payments for your own bookings");
        }

        // Check if payment can be processed
        if (booking.getPayment().getStatus() != PaymentStatus.PENDING
                && booking.getPayment().getStatus() != PaymentStatus.FAILED) {
            throw new IllegalArgumentException("Payment has already been processed or is in invalid state");
        }

        // Process payment
        booking.getPayment().markAsCompleted(transactionId);
        bookingRepository.save(booking);

        // Auto-confirm booking after successful payment
        if (booking.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            booking.confirm();
            bookingRepository.save(booking);
            AvailabilityWebSocketEndpoint.broadcastAvailabilityChange(booking.getAccommodationId(),
                    booking.getCheckInDate(), false);
        }

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Refund booking.
     */
    @Transactional
    public BookingResponse refundBooking(UUID userId, UUID bookingId, String refundReason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify ownership (Traveler or Supplier)
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

        if (!booking.getUserId().equals(userId) && !accommodation.getSupplierId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to refund this booking");
        }

        // Check if booking can be refunded
        if (booking.getPayment().getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment has not been completed");
        }

        // Calculate refund amount based on cancellation policy
        // Calculate days before check-in
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), booking.getCheckInDate());
        int daysBeforeCheckIn = (int) daysDiff;

        Money refundAmount = pricingService.calculateRefundAmount(
                booking.getTotalPrice(),
                accommodation.getCancellationPolicy(),
                daysBeforeCheckIn);

        // Process refund
        booking.getPayment().markAsRefunded(refundAmount, refundReason);
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Get payment status for a booking.
     */
    @Transactional
    public String getPaymentStatus(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only view your own bookings");
        }
        return booking.getPayment() != null ? booking.getPayment().getStatus().name() : PaymentStatus.UNPAID.name();
    }

    /**
     * Reject booking by supplier.
     */
    @Transactional
    public BookingResponse rejectBooking(UUID supplierId, UUID bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));
        if (!accommodation.getSupplierId().equals(supplierId)) {
            throw new IllegalArgumentException("You can only reject bookings for your accommodations");
        }
        booking.cancel(reason, supplierId);
        bookingRepository.save(booking);
        AvailabilityWebSocketEndpoint.broadcastAvailabilityChange(booking.getAccommodationId(),
                booking.getCheckInDate(), true);
        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Get booking statistics for supplier.
     */
    @Transactional
    public BookingStatistics getSupplierBookingStatistics(UUID supplierId) {
        List<Booking> bookings = bookingRepository.findBySupplierIdPaginated(supplierId, 0, Integer.MAX_VALUE);

        int totalBookings = bookings.size();
        int confirmedBookings = (int) bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        int completedBookings = (int) bookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
        int cancelledBookings = (int) bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        BigDecimal totalRevenue = bookings.stream()
                .filter(b -> b.getPayment().getStatus() == PaymentStatus.COMPLETED)
                .map(b -> b.getTotalPrice().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BookingStatistics(totalBookings, confirmedBookings, completedBookings, cancelledBookings,
                totalRevenue);
    }

    /**
     * Booking statistics record.
     */
    public record BookingStatistics(
            int totalBookings,
            int confirmedBookings,
            int completedBookings,
            int cancelledBookings,
            BigDecimal totalRevenue) {
    }

    private Money applyServiceFeeBounds(Money serviceFee, BookingFeeConfig feeConfig) {
        if (serviceFee == null || feeConfig == null) {
            return serviceFee;
        }
        BigDecimal amount = serviceFee.getAmount();
        if (feeConfig.getServiceFeeMinimum() != null
                && amount.compareTo(feeConfig.getServiceFeeMinimum()) < 0) {
            return new Money(feeConfig.getServiceFeeMinimum(), serviceFee.getCurrency());
        }
        if (feeConfig.getServiceFeeMaximum() != null
                && amount.compareTo(feeConfig.getServiceFeeMaximum()) > 0) {
            return new Money(feeConfig.getServiceFeeMaximum(), serviceFee.getCurrency());
        }
        return serviceFee;
    }
}
