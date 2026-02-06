package com.travelplatform.application.dto.response.payment;

import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.booking.BookingPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AdminPaymentResponse {
    public UUID id;
    public UUID bookingId;
    public BigDecimal amount;
    public String currency;
    public BookingPayment.PaymentMethod paymentMethod;
    public String paymentProvider;
    public String transactionId;
    public PaymentStatus status;
    public String failureReason;
    public BigDecimal refundAmount;
    public String refundReason;
    public LocalDateTime createdAt;
    public LocalDateTime paidAt;
    public LocalDateTime refundedAt;

    public static AdminPaymentResponse fromDomain(BookingPayment payment) {
        AdminPaymentResponse resp = new AdminPaymentResponse();
        resp.id = payment.getId();
        resp.bookingId = payment.getBookingId();
        resp.amount = payment.getAmount() != null ? payment.getAmount().getAmount() : null;
        resp.currency = payment.getAmount() != null ? payment.getAmount().getCurrencyCode() : null;
        resp.paymentMethod = payment.getPaymentMethod();
        resp.paymentProvider = payment.getPaymentProvider();
        resp.transactionId = payment.getTransactionId();
        resp.status = payment.getStatus();
        resp.failureReason = payment.getFailureReason();
        resp.refundAmount = payment.getRefundAmount() != null ? payment.getRefundAmount().getAmount() : null;
        resp.refundReason = payment.getRefundReason();
        resp.createdAt = payment.getCreatedAt();
        resp.paidAt = payment.getPaidAt();
        resp.refundedAt = payment.getRefundedAt();
        return resp;
    }
}
