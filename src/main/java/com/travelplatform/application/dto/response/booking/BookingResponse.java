package com.travelplatform.application.dto.response.booking;

import com.travelplatform.domain.enums.BookingStatus;
import com.travelplatform.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for booking response.
 */
public class BookingResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private UUID accommodationId;
    private String accommodationTitle;
    private String accommodationAddress;
    private String accommodationCity;
    private String accommodationCountry;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
    private Integer numberOfInfants;
    private Integer totalNights;
    private BigDecimal basePricePerNight;
    private BigDecimal totalBasePrice;
    private BigDecimal serviceFee;
    private BigDecimal cleaningFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String currency;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private String specialRequests;
    private String guestMessageToHost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private PaymentInfoResponse payment;

    // Nested class for payment info
    public static class PaymentInfoResponse {
        private UUID id;
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;
        private String paymentProvider;
        private String transactionId;
        private PaymentStatus status;
        private LocalDateTime paidAt;

        public PaymentInfoResponse() {}

        public PaymentInfoResponse(UUID id, BigDecimal amount, String currency, String paymentMethod,
                                   String paymentProvider, String transactionId, PaymentStatus status, LocalDateTime paidAt) {
            this.id = id;
            this.amount = amount;
            this.currency = currency;
            this.paymentMethod = paymentMethod;
            this.paymentProvider = paymentProvider;
            this.transactionId = transactionId;
            this.status = status;
            this.paidAt = paidAt;
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentProvider() { return paymentProvider; }
        public void setPaymentProvider(String paymentProvider) { this.paymentProvider = paymentProvider; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        public LocalDateTime getPaidAt() { return paidAt; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public UUID getAccommodationId() { return accommodationId; }
    public void setAccommodationId(UUID accommodationId) { this.accommodationId = accommodationId; }
    public String getAccommodationTitle() { return accommodationTitle; }
    public void setAccommodationTitle(String accommodationTitle) { this.accommodationTitle = accommodationTitle; }
    public String getAccommodationAddress() { return accommodationAddress; }
    public void setAccommodationAddress(String accommodationAddress) { this.accommodationAddress = accommodationAddress; }
    public String getAccommodationCity() { return accommodationCity; }
    public void setAccommodationCity(String accommodationCity) { this.accommodationCity = accommodationCity; }
    public String getAccommodationCountry() { return accommodationCountry; }
    public void setAccommodationCountry(String accommodationCountry) { this.accommodationCountry = accommodationCountry; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public Integer getNumberOfAdults() { return numberOfAdults; }
    public void setNumberOfAdults(Integer numberOfAdults) { this.numberOfAdults = numberOfAdults; }
    public Integer getNumberOfChildren() { return numberOfChildren; }
    public void setNumberOfChildren(Integer numberOfChildren) { this.numberOfChildren = numberOfChildren; }
    public Integer getNumberOfInfants() { return numberOfInfants; }
    public void setNumberOfInfants(Integer numberOfInfants) { this.numberOfInfants = numberOfInfants; }
    public Integer getTotalNights() { return totalNights; }
    public void setTotalNights(Integer totalNights) { this.totalNights = totalNights; }
    public BigDecimal getBasePricePerNight() { return basePricePerNight; }
    public void setBasePricePerNight(BigDecimal basePricePerNight) { this.basePricePerNight = basePricePerNight; }
    public BigDecimal getTotalBasePrice() { return totalBasePrice; }
    public void setTotalBasePrice(BigDecimal totalBasePrice) { this.totalBasePrice = totalBasePrice; }
    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }
    public BigDecimal getCleaningFee() { return cleaningFee; }
    public void setCleaningFee(BigDecimal cleaningFee) { this.cleaningFee = cleaningFee; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public String getGuestMessageToHost() { return guestMessageToHost; }
    public void setGuestMessageToHost(String guestMessageToHost) { this.guestMessageToHost = guestMessageToHost; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public PaymentInfoResponse getPayment() { return payment; }
    public void setPayment(PaymentInfoResponse payment) { this.payment = payment; }
}
