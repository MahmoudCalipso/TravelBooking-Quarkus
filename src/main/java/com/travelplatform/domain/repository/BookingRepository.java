package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.booking.BookingPayment;
import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Booking aggregate.
 * Defines the contract for booking data access operations.
 */
public interface BookingRepository {

    /**
     * Saves a new booking.
     *
     * @param booking booking to save
     * @return saved booking
     */
    Booking save(Booking booking);

    /**
     * Updates an existing booking.
     *
     * @param booking booking to update
     * @return updated booking
     */
    Booking update(Booking booking);

    /**
     * Deletes a booking by ID.
     *
     * @param id booking ID
     */
    void deleteById(UUID id);

    /**
     * Finds a booking by ID.
     *
     * @param id booking ID
     * @return optional booking
     */
    Optional<Booking> findById(UUID id);

    /**
     * Finds all bookings.
     *
     * @return list of all bookings
     */
    List<Booking> findAll();

    /**
     * Finds bookings by user ID.
     *
     * @param userId user ID
     * @return list of bookings by user
     */
    List<Booking> findByUserId(UUID userId);

    /**
     * Finds bookings by accommodation ID.
     *
     * @param accommodationId accommodation ID
     * @return list of bookings for the accommodation
     */
    List<Booking> findByAccommodationId(UUID accommodationId);

    /**
     * Finds bookings by status.
     *
     * @param status booking status
     * @return list of bookings with the status
     */
    List<Booking> findByStatus(BookingStatus status);

    /**
     * Finds bookings by payment status.
     *
     * @param paymentStatus payment status
     * @return list of bookings with the payment status
     */
    List<Booking> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Finds bookings by user ID with pagination.
     *
     * @param userId   user ID
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of bookings
     */
    List<Booking> findByUserIdPaginated(UUID userId, BookingStatus status,  int page, int pageSize);

    /**
     * Finds bookings by accommodation ID with pagination.
     *
     * @param accommodationId accommodation ID
     * @param page            page number (0-indexed)
     * @param pageSize        page size
     * @return list of bookings
     */
    List<Booking> findByAccommodationIdPaginated(UUID accommodationId, BookingStatus status ,int page, int pageSize);

    /**
     * Finds bookings by status with pagination.
     *
     * @param status   booking status
     * @param page     page number (0-indexed)
     * @param pageSize page size
     * @return list of bookings
     */
    List<Booking> findByStatusPaginated(BookingStatus status, int page, int pageSize);

    /**
     * Finds bookings by user and status.
     *
     * @param userId user ID
     * @param status booking status
     * @return list of bookings
     */
    List<Booking> findByUserIdAndStatus(UUID userId, BookingStatus status);

    /**
     * Finds bookings by accommodation and status.
     *
     * @param accommodationId accommodation ID
     * @param status          booking status
     * @return list of bookings
     */
    List<Booking> findByAccommodationIdAndStatus(UUID accommodationId, BookingStatus status);

    /**
     * Finds bookings for a date range.
     *
     * @param accommodationId accommodation ID
     * @param checkInDate     check-in date
     * @param checkOutDate    check-out date
     * @return list of overlapping bookings
     */
    List<Booking> findOverlappingBookings(UUID accommodationId, LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * Finds bookings by check-in date.
     *
     * @param checkInDate check-in date
     * @return list of bookings
     */
    List<Booking> findByCheckInDate(LocalDate checkInDate);

    /**
     * Finds bookings by check-out date.
     *
     * @param checkOutDate check-out date
     * @return list of bookings
     */
    List<Booking> findByCheckOutDate(LocalDate checkOutDate);

    /**
     * Finds bookings between dates.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of bookings
     */
    List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Finds active bookings (CONFIRMED status).
     *
     * @return list of active bookings
     */
    List<Booking> findActive();

    /**
     * Finds upcoming bookings for a user.
     *
     * @param userId user ID
     * @return list of upcoming bookings
     */
    List<Booking> findUpcomingByUserId(UUID userId);

    /**
     * Finds past bookings for a user.
     *
     * @param userId user ID
     * @return list of past bookings
     */
    List<Booking> findPastByUserId(UUID userId);

    /**
     * Finds cancelled bookings for a user.
     *
     * @param userId user ID
     * @return list of cancelled bookings
     */
    List<Booking> findCancelledByUserId(UUID userId);

    /**
     * Counts bookings by user.
     *
     * @param userId user ID
     * @return count of bookings by user
     */
    long countByUserId(UUID userId);

    /**
     * Counts bookings by accommodation.
     *
     * @param accommodationId accommodation ID
     * @return count of bookings for the accommodation
     */
    long countByAccommodationId(UUID accommodationId);

    /**
     * Counts bookings by status.
     *
     * @param status booking status
     * @return count of bookings with the status
     */
    long countByStatus(BookingStatus status);

    /**
     * Counts bookings by payment status.
     *
     * @param paymentStatus payment status
     * @return count of bookings with the payment status
     */
    long countByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Calculates total revenue within a date range.
     * 
     * @param startDate start date
     * @param endDate   end date
     * @return total revenue
     */
    java.math.BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate);

    /**
     * Calculates total refunds within a date range.
     * 
     * @param startDate start date
     * @param endDate   end date
     * @return total refunded amount
     */
    java.math.BigDecimal calculateTotalRefunds(LocalDate startDate, LocalDate endDate);

    /**
     * Counts completed bookings within a date range.
     * 
     * @param startDate start date
     * @param endDate   end date
     * @return count of completed bookings
     */
    long countCompletedBookings(LocalDate startDate, LocalDate endDate);

    /**
     * Counts cancelled bookings within a date range.
     * 
     * @param startDate start date
     * @param endDate   end date
     * @return count of cancelled bookings
     */
    long countCancelledBookings(LocalDate startDate, LocalDate endDate);

    /**
     * Counts all bookings.
     *
     * @return total count of bookings
     */
    long countAll();

    /**
     * Standard count method.
     */
    long count();

    /**
     * Finds bookings by total price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of bookings within price range
     */
    List<Booking> findByTotalPriceRange(double minPrice, double maxPrice);

    /**
     * Finds bookings by number of guests.
     *
     * @param guests number of guests
     * @return list of bookings with the guest count
     */
    List<Booking> findByNumberOfGuests(int guests);

    /**
     * Finds bookings by currency.
     *
     * @param currency currency code
     * @return list of bookings with the currency
     */
    List<Booking> findByCurrency(String currency);

    /**
     * Finds booking payments by booking ID.
     *
     * @param bookingId booking ID
     * @return list of payments
     */
    List<BookingPayment> findPaymentsByBookingId(UUID bookingId);

    /**
     * Finds booking payment by transaction ID.
     *
     * @param transactionId transaction ID
     * @return optional payment
     */
    Optional<BookingPayment> findPaymentByTransactionId(String transactionId);

    /**
     * Finds payments by status.
     *
     * @param status payment status
     * @return list of payments with the status
     */
    List<BookingPayment> findPaymentsByStatus(PaymentStatus status);

    /**
     * Finds payments by payment method.
     *
     * @param paymentMethod payment method
     * @return list of payments with the method
     */
    List<BookingPayment> findPaymentsByPaymentMethod(String paymentMethod);

    /**
     * Counts payments by booking.
     *
     * @param bookingId booking ID
     * @return count of payments
     */
    long countPaymentsByBookingId(UUID bookingId);

    /**
     * Calculates total revenue for an accommodation.
     *
     * @param accommodationId accommodation ID
     * @return total revenue
     */
    double calculateTotalRevenueByAccommodation(UUID accommodationId);

    /**
     * Calculates total revenue for a date range.
     *
     * @param startDate start date
     * @param endDate   end date
     * @return total revenue
     */
    double calculateTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Calculates total revenue for an accommodation in a date range.
     *
     * @param accommodationId accommodation ID
     * @param startDate       start date
     * @param endDate         end date
     * @return total revenue
     */
    double calculateTotalRevenueByAccommodationAndDateRange(UUID accommodationId, LocalDate startDate,
            LocalDate endDate);

    /**
     * Finds bookings with special requests.
     *
     * @return list of bookings with special requests
     */
    List<Booking> findWithSpecialRequests();

    /**
     * Finds bookings by cancellation reason.
     *
     * @param reason cancellation reason
     * @return list of bookings
     */
    List<Booking> findByCancellationReason(String reason);

    /**
     * Finds bookings cancelled by a specific user.
     *
     * @param cancelledBy user ID who cancelled
     * @return list of bookings
     */
    List<Booking> findByCancelledBy(UUID cancelledBy);

    /**
     * Finds bookings sorted by total price.
     *
     * @param limit maximum number of results
     * @return list of bookings
     */
    List<Booking> findMostExpensive(int limit);

    /**
     * Finds bookings sorted by total price (ascending).
     *
     * @param limit maximum number of results
     * @return list of bookings
     */
    List<Booking> findLeastExpensive(int limit);

    /**
     * Finds bookings with failed payments.
     *
     * @return list of bookings with failed payments
     */
    List<Booking> findWithFailedPayments();

    /**
     * Finds bookings with pending payments.
     *
     * @return list of bookings with pending payments
     */
    List<Booking> findWithPendingPayments();

    /**
     * Finds bookings with refunds.
     *
     * @return list of bookings with refunds
     */
    List<Booking> findWithRefunds();

    /**
     * Finds bookings by supplier with pagination.
     *
     * @param supplierId supplier user ID
     * @param page       page number (0-indexed)
     * @param pageSize   page size
     * @return list of bookings
     */
    List<Booking> findBySupplierIdPaginated(UUID supplierId, int page, int pageSize);

    /**
     * Finds bookings by supplier and status.
     *
     * @param supplierId supplier user ID
     * @param status     booking status
     * @return list of bookings
     */
    List<Booking> findBySupplierIdAndStatus(UUID supplierId, BookingStatus status);

    /**
     * Calculate platform service fees collected from bookings for specific supplier roles.
     *
     * @param roles supplier roles to include (e.g., SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER)
     * @param start start date-time (inclusive)
     * @param end end date-time (inclusive)
     * @param statuses booking statuses to include when counting fees
     * @return total service fee amount (currency as stored)
     */
    java.math.BigDecimal calculateServiceFeesBySupplierRoles(
            java.util.List<com.travelplatform.domain.enums.UserRole> roles,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end,
            java.util.List<BookingStatus> statuses);

    /**
     * Counts bookings by supplier.
     *
     * @param supplierId supplier user ID
     * @return count of bookings for supplier
     */
    long countBySupplierId(UUID supplierId);
}
