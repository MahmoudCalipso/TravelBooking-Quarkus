package com.travelplatform.interfaces.rest.webhook;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.booking.BookingPayment;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.valueobject.Money;
import com.travelplatform.infrastructure.audit.AuditLogService;
import com.travelplatform.infrastructure.payment.StripePaymentGateway;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

@Path("/api/v1/webhooks/stripe")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Inject
    StripePaymentGateway stripePaymentGateway;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    AuditLogService auditLogService;

    @ConfigProperty(name = "stripe.webhook.secret")
    String webhookSecret;

    @POST
    @Transactional
    public Response handleWebhook(String payload, @HeaderParam("Stripe-Signature") String signature) {
        if (signature == null || signature.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing Stripe-Signature header").build();
        }

        try {
            if (!stripePaymentGateway.verifyWebhookSignature(payload, signature, webhookSecret)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid signature").build();
            }

            Event event = Webhook.constructEvent(payload, signature, webhookSecret);
            String eventType = event.getType();
            log.info("Stripe webhook event received: {}", eventType);

            switch (eventType) {
                case "payment_intent.succeeded" -> handlePaymentIntent(event, PaymentStatus.COMPLETED, null);
                case "payment_intent.payment_failed" -> handlePaymentIntent(event, PaymentStatus.FAILED,
                        "Stripe payment failed");
                case "charge.refunded" -> handleChargeRefunded(event);
                default -> log.debug("Unhandled Stripe event type: {}", eventType);
            }

            return Response.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature verification failed", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Signature verification failed").build();
        } catch (Exception e) {
            log.error("Stripe webhook processing failed", e);
            return Response.serverError().entity("Webhook processing failed").build();
        }
    }

    private void handlePaymentIntent(Event event, PaymentStatus status, String failureReason) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Optional<com.stripe.model.StripeObject> stripeObject = deserializer.getObject();
        if (stripeObject.isEmpty()) {
            log.warn("Stripe webhook missing data object for event {}", event.getId());
            return;
        }

        PaymentIntent intent = (PaymentIntent) stripeObject.get();
        updateBookingPayment(intent.getId(), status, failureReason);
    }

    private void handleChargeRefunded(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Optional<com.stripe.model.StripeObject> stripeObject = deserializer.getObject();
        if (stripeObject.isEmpty()) {
            log.warn("Stripe webhook missing charge object for event {}", event.getId());
            return;
        }

        Charge charge = (Charge) stripeObject.get();
        String transactionId = charge.getPaymentIntent();
        Money refundAmount = null;
        if (charge.getAmountRefunded() != null && charge.getCurrency() != null) {
            refundAmount = new Money(
                    BigDecimal.valueOf(charge.getAmountRefunded()).divide(BigDecimal.valueOf(100)),
                    charge.getCurrency().toUpperCase()
            );
        }
        updateRefund(transactionId, refundAmount, "Refund processed by Stripe");
    }

    private void updateBookingPayment(String transactionId, PaymentStatus status, String failureReason) {
        bookingRepository.findPaymentByTransactionId(transactionId).ifPresent(payment -> {
            bookingRepository.findById(payment.getBookingId()).ifPresent(booking -> {
                BookingPayment updatedPayment = updatePaymentStatus(payment, status, failureReason);
                booking.setPayment(updatedPayment);
                booking.updatePaymentStatus(status);
                if (status == PaymentStatus.COMPLETED) {
                    booking.confirm();
                }
                bookingRepository.update(booking);
                auditLogService.logPaymentTransaction(booking.getId(), updatedPayment.getAmount(), status);
            });
        });
    }

    private void updateRefund(String transactionId, Money refundAmount, String reason) {
        bookingRepository.findPaymentByTransactionId(transactionId).ifPresent(payment -> {
            bookingRepository.findById(payment.getBookingId()).ifPresent(booking -> {
                payment.markAsRefunded(refundAmount, reason);
                booking.setPayment(payment);
                booking.updatePaymentStatus(refundAmount != null && refundAmount.getAmount()
                        .compareTo(payment.getAmount().getAmount()) < 0
                        ? PaymentStatus.PARTIALLY_REFUNDED : PaymentStatus.REFUNDED);
                bookingRepository.update(booking);
                auditLogService.logPaymentTransaction(booking.getId(), payment.getAmount(), booking.getPaymentStatus());
            });
        });
    }

    private BookingPayment updatePaymentStatus(BookingPayment payment, PaymentStatus status, String failureReason) {
        switch (status) {
            case COMPLETED -> payment.markAsCompleted(payment.getTransactionId());
            case FAILED -> payment.markAsFailed(failureReason != null ? failureReason : "Payment failed");
            case PROCESSING, PENDING -> payment.markAsProcessing();
            default -> {
            }
        }
        return payment;
    }
}
