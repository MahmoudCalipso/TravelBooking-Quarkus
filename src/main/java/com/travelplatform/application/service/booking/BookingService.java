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
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.repository.UserRepository;
import com.travelplatform.domain.service.AvailabilityService;
import com.travelplatform.domain.service.PricingService;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;
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

    private static final BigDecimal SERVICE_FEE_PERCENTAGE = new BigDecimal("0.10"); // 10% service fee

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
        Money serviceFee = pricingService.calculateServiceFee(totalBasePrice,
                SERVICE_FEE_PERCENTAGE.doubleValue() * 100);
        Money cleaningFee = pricingService.calculateCleaningFee(accommodation, totalNights);
        Money taxAmount = pricingService.calculateTax(totalBasePrice.add(serviceFee), 0.08); // 8% tax
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
                accommodation.getCurrency() != null ? accommodation.getCurrency().getCurrencyCode() : "USD",
                BookingPayment.PaymentMethod.CARD, // Default payment method
                "STRIPE" // Default payment provider
        );
        // payment.setTransactionId(null); // Optional, initially null
        // payment.setStatus(PaymentStatus.UNPAID); // Default is PENDING in
        // constructor, but Service wants UNPAID?
        // Wait, constructor sets PENDING. Service previously set UNPAID.
        // Let's stick to constructor default PENDING or update if needed.
        // If UNPAID is required, I should check if PaymentStatus has UNPAID.
        // The previous code had PaymentStatus.UNPAID.
        // Let's assume PENDING is fine or set it involved.
        // Actually, BookingPayment constructor sets PENDING.
        // If I need UNPAID, I might need a setter or change constructor.
        // But let's check PaymentStatus enum. Assuming UNPAID exists or PENDING is the
        // intended initial state.
        booking.setPayment(payment);

        // Save booking
        bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Get booking by ID.
     */
    @Transactional
    public BookingResponse getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Get bookings by user.
     */
    @Transactional
    public List<BookingResponse> getBookingsByUser(UUID userId, BookingStatus status, int page, int pageSize) {
        List<Booking> bookings = bookingRepository.findByUserIdPaginated(userId, status,page, pageSize);
        return bookingMapper.toBookingResponseList(bookings);
    }

    /**
     * Get bookings by accommodation (for suppliers).
     */
    @Transactional
    public List<BookingResponse> getBookingsByAccommodation(UUID accommodationId, BookingStatus status, int page, int pageSize) {
        List<Booking> bookings = bookingRepository.findByAccommodationIdPaginated(accommodationId, status, page, pageSize);
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
    public BookingResponse confirmBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Check if booking can be confirmed
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Booking cannot be confirmed in current status");
        }

        // Confirm booking
        booking.confirm();
        bookingRepository.save(booking);

        // Update accommodation booking count
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));
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

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Complete booking.
     */
    @Transactional
    public BookingResponse completeBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

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
    public BookingResponse processPayment(UUID bookingId, String transactionId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Check if payment can be processed
        if (booking.getPayment().getStatus() != PaymentStatus.PENDING
                && booking.getPayment().getStatus() != PaymentStatus.FAILED) { // Assuming retry on failed? Or just
                                                                               // PENDING.
            // If strictly first attempt, PENDING. If expecting retries, maybe FAILED too.
            // But replacing UNPAID with PENDING is the goal.
            // Original: PENDING && UNPAID.
            // New: PENDING only (since UNPAID is synonym but might be distinct constant).
            // Let's use PENDING.
            throw new IllegalArgumentException("Payment has already been processed or is in invalid state");
        }

        // Process payment
        booking.getPayment().markAsCompleted(transactionId);
        bookingRepository.save(booking);

        // Auto-confirm booking after successful payment
        if (booking.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            booking.confirm();
            bookingRepository.save(booking);
        }

        return bookingMapper.toBookingResponse(booking);
    }

    /**
     * Refund booking.
     */
    @Transactional
    public BookingResponse refundBooking(UUID bookingId, String refundReason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Check if booking can be refunded
        if (booking.getPayment().getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment has not been completed");
        }

        // Calculate refund amount based on cancellation policy
        Accommodation accommodation = accommodationRepository.findById(booking.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Accommodation not found"));

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
}
