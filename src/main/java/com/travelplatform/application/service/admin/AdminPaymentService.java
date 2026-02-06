package com.travelplatform.application.service.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplatform.application.dto.response.common.PaginatedResponse;
import com.travelplatform.application.dto.response.payment.AdminPaymentResponse;
import com.travelplatform.application.dto.response.payment.AdminPaymentStatsResponse;
import com.travelplatform.application.dto.response.payment.PaymentDisputeResponse;
import com.travelplatform.application.dto.response.payment.PaymentWebhookLogResponse;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.model.booking.BookingPayment;
import com.travelplatform.domain.repository.AccommodationRepository;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.valueobject.Money;
import com.travelplatform.infrastructure.persistence.entity.AuditLogEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminPaymentService {

    @Inject
    BookingRepository bookingRepository;
    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    EntityManager entityManager;

    @Inject
    ObjectMapper objectMapper;

    public PaginatedResponse<AdminPaymentResponse> listPayments(PaymentStatus status, UUID supplierId,
            LocalDate startDate, LocalDate endDate, int page, int size) {
        List<PaymentStatus> statuses = status != null ? List.of(status) : List.of(PaymentStatus.values());

        List<BookingPayment> payments = bookingRepository.findPayments(
                statuses, startDate, endDate, supplierId == null ? page : 0, supplierId == null ? size : Integer.MAX_VALUE);
        long total = bookingRepository.countPayments(statuses, startDate, endDate);

        if (supplierId != null) {
            payments = filterBySupplier(payments, supplierId);
            total = payments.size();
            payments = paginate(payments, page, size);
        }

        List<AdminPaymentResponse> pageItems = payments.stream()
                .sorted(Comparator.comparing(BookingPayment::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(AdminPaymentResponse::fromDomain)
                .collect(Collectors.toList());

        return PaginatedResponse.of(pageItems, total, page, size);
    }

    public AdminPaymentStatsResponse stats(LocalDateRange range) {
        LocalDate start = range != null ? range.startDate : null;
        LocalDate end = range != null ? range.endDate : null;

        List<BookingPayment> allPayments = bookingRepository.findPayments(List.of(), start, end, 0, Integer.MAX_VALUE);
        List<BookingPayment> successful = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED || p.getStatus() == PaymentStatus.PAID)
                .toList();
        List<BookingPayment> failed = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .toList();
        List<BookingPayment> refunded = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.REFUNDED || p.getStatus() == PaymentStatus.PARTIALLY_REFUNDED)
                .toList();

        AdminPaymentStatsResponse stats = new AdminPaymentStatsResponse();
        stats.totalTransactions = allPayments.size();
        stats.successfulTransactions = successful.size();
        stats.completedTransactions = successful.size();
        stats.failedTransactions = failed.size();
        stats.refundedTransactions = refunded.size();
        stats.totalRevenueByCurrency = aggregateByCurrency(successful);
        stats.averageAmountByCurrency = averageByCurrency(successful);
        stats.refundedAmountByCurrency = aggregateRefundsByCurrency(refunded);
        return stats;
    }

    @Transactional
    public void processRefund(UUID paymentId, BigDecimal amount, String reason) {
        Optional<BookingPayment> paymentOpt = bookingRepository.findPaymentById(paymentId);
        BookingPayment payment = paymentOpt.orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Payment not found"));
        if (!payment.canBeRefunded()) {
            throw new jakarta.ws.rs.BadRequestException("Payment cannot be refunded");
        }
        Money refundAmount = amount != null ? new Money(amount, payment.getCurrency()) : payment.getAmount();
        if (amount != null && amount.compareTo(payment.getAmount().getAmount()) < 0) {
            payment.markAsPartiallyRefunded(refundAmount, reason);
        } else {
            payment.markAsRefunded(refundAmount, reason);
        }
        // persist via booking update
        bookingRepository.savePayment(payment);
    }

    public List<PaymentWebhookLogResponse> getWebhookLogs(int page, int size) {
        TypedQuery<AuditLogEntity> query = entityManager.createQuery(
                "SELECT a FROM AuditLogEntity a WHERE a.action = :action ORDER BY a.createdAt DESC",
                AuditLogEntity.class);
        query.setParameter("action", "PAYMENT_EVENT");
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList().stream()
                .map(this::toWebhookLogResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentDisputeResponse> getDisputes(int page, int size) {
        List<PaymentStatus> disputeStatuses = List.of(PaymentStatus.FAILED, PaymentStatus.REFUNDED,
                PaymentStatus.PARTIALLY_REFUNDED);
        List<BookingPayment> disputes = bookingRepository.findPayments(disputeStatuses, null, null, page, size);
        return disputes.stream()
                .map(this::toDisputeResponse)
                .collect(Collectors.toList());
    }

    private Map<String, BigDecimal> aggregateByCurrency(List<BookingPayment> payments) {
        return payments.stream()
                .filter(p -> p.getAmount() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getAmount().getCurrencyCode(),
                        Collectors.reducing(BigDecimal.ZERO, p -> p.getAmount().getAmount(), BigDecimal::add)
                ));
    }

    private Map<String, BigDecimal> aggregateRefundsByCurrency(List<BookingPayment> payments) {
        return payments.stream()
                .filter(p -> p.getRefundAmount() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getRefundAmount().getCurrencyCode(),
                        Collectors.reducing(BigDecimal.ZERO, p -> p.getRefundAmount().getAmount(), BigDecimal::add)
                ));
    }

    private List<BookingPayment> filterBySupplier(List<BookingPayment> payments, UUID supplierId) {
        Set<UUID> accommodationIds = accommodationRepository.findBySupplierId(supplierId).stream()
                .map(Accommodation::getId)
                .collect(Collectors.toSet());
        if (accommodationIds.isEmpty()) {
            return List.of();
        }
        return payments.stream()
                .filter(payment -> bookingRepository.findById(payment.getBookingId())
                        .map(Booking::getAccommodationId)
                        .filter(accommodationIds::contains)
                        .isPresent())
                .collect(Collectors.toList());
    }

    private List<BookingPayment> paginate(List<BookingPayment> payments, int page, int size) {
        int from = Math.max(0, page * size);
        int to = Math.min(payments.size(), from + size);
        if (from >= to) {
            return List.of();
        }
        return payments.subList(from, to);
    }

    private PaymentWebhookLogResponse toWebhookLogResponse(AuditLogEntity entity) {
        PaymentWebhookLogResponse dto = new PaymentWebhookLogResponse();
        dto.id = entity.getId();
        dto.action = entity.getAction();
        dto.entityType = entity.getEntityType();
        dto.entityId = entity.getEntityId();
        dto.createdAt = entity.getCreatedAt();
        dto.payload = parsePayload(entity.getChanges());
        return dto;
    }

    private Map<String, Object> parsePayload(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("raw", json);
            return fallback;
        }
    }

    private PaymentDisputeResponse toDisputeResponse(BookingPayment payment) {
        PaymentDisputeResponse dto = new PaymentDisputeResponse();
        dto.paymentId = payment.getId();
        dto.bookingId = payment.getBookingId();
        dto.transactionId = payment.getTransactionId();
        dto.amount = payment.getAmount() != null ? payment.getAmount().getAmount() : null;
        dto.currency = payment.getAmount() != null ? payment.getAmount().getCurrencyCode() : payment.getCurrency();
        dto.status = payment.getStatus();
        dto.reason = payment.getFailureReason() != null ? payment.getFailureReason() : payment.getRefundReason();
        dto.refundAmount = payment.getRefundAmount() != null ? payment.getRefundAmount().getAmount() : null;
        dto.createdAt = payment.getCreatedAt();
        dto.updatedAt = payment.getRefundedAt() != null ? payment.getRefundedAt() : payment.getPaidAt();
        return dto;
    }

    private Map<String, BigDecimal> averageByCurrency(List<BookingPayment> payments) {
        Map<String, List<BookingPayment>> byCurrency = payments.stream()
                .filter(p -> p.getAmount() != null)
                .collect(Collectors.groupingBy(p -> p.getAmount().getCurrencyCode()));
        return byCurrency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            BigDecimal sum = e.getValue().stream()
                                    .map(p -> p.getAmount().getAmount())
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            int count = e.getValue().size();
                            return count == 0 ? BigDecimal.ZERO : sum.divide(BigDecimal.valueOf(count), java.math.RoundingMode.HALF_UP);
                        }
                ));
    }

    public static class LocalDateRange {
        public LocalDate startDate;
        public LocalDate endDate;

        public LocalDateRange() {
        }

        public LocalDateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
