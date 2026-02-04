package com.travelplatform.infrastructure.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.travelplatform.infrastructure.payment.PaymentException.*;
/**
 * Stripe implementation of the PaymentGateway interface.
 * This class handles all payment operations using the Stripe API.
 */
@ApplicationScoped
public class StripePaymentGateway implements PaymentGateway {

    @Inject
    @ConfigProperty(name = "stripe.api.key")
    private String stripeApiKey;

    @Inject
    @ConfigProperty(name = "stripe.webhook.secret")
    private String webhookSecret;

    @Inject
    @ConfigProperty(name = "stripe.application.fee.percentage")
    private double applicationFeePercentage;

    /**
     * Initialize Stripe API key.
     */
    private void initializeStripe() {
        if (Stripe.apiKey == null || !Stripe.apiKey.equals(stripeApiKey)) {
            Stripe.apiKey = stripeApiKey;
        }
    }

    @Override
    public PaymentIntent createPaymentIntent(UUID bookingId, BigDecimal amount, String currency,
                                          String paymentMethod, String description) throws PaymentException {
        initializeStripe();

        try {
            // Convert amount to cents (Stripe uses smallest currency unit)
            long amountInCents = amount.multiply(new BigDecimal("100")).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency.toLowerCase())
                    .setDescription(description)
                    .putAllMetadata(Map.of(
                            "booking_id", bookingId.toString(),
                            "payment_method", paymentMethod
                    ))
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            com.stripe.model.PaymentIntent stripeIntent = com.stripe.model.PaymentIntent.create(params);

            PaymentIntent intent = new PaymentIntent();
            intent.setId(stripeIntent.getId());
            intent.setClientSecret(stripeIntent.getClientSecret());
            intent.setStatus(stripeIntent.getStatus());
            intent.setAmount(new BigDecimal(stripeIntent.getAmount()).divide(new BigDecimal("100")));
            intent.setCurrency(stripeIntent.getCurrency().toUpperCase());
            intent.setPaymentMethod(stripeIntent.getPaymentMethod());
            intent.setDescription(stripeIntent.getDescription());
            intent.setCreatedAt(Instant.ofEpochSecond(stripeIntent.getCreated()));

            return intent;

        } catch (StripeException e) {
            throw new PaymentException(PaymentException.PAYMENT_FAILED, "Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResult confirmPayment(String paymentIntentId, String paymentMethodId) throws PaymentException {
        initializeStripe();

        try {
            PaymentIntentRetrieveParams retrieveParams = PaymentIntentRetrieveParams.builder()
                    .addExpand("payment_method")
                    .build();

            com.stripe.model.PaymentIntent stripeIntent = com.stripe.model.PaymentIntent.retrieve(
                    paymentIntentId, retrieveParams, null);

            // If payment method is provided, attach it to the intent
            if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
                PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams.builder()
                        .setPaymentMethod(paymentMethodId)
                        .build();
                stripeIntent = stripeIntent.update(updateParams);
            }

            // Confirm the payment
            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder().build();
            stripeIntent = stripeIntent.confirm(confirmParams);

            PaymentResult result = new PaymentResult();
            result.setPaymentIntentId(stripeIntent.getId());
            result.setPaymentId(stripeIntent.getLatestCharge());
            result.setStatus(stripeIntent.getStatus());
            result.setTransactionId(stripeIntent.getId());
            result.setAmount(new BigDecimal(stripeIntent.getAmount()).divide(new BigDecimal("100")));
            result.setCurrency(stripeIntent.getCurrency().toUpperCase());

            if (stripeIntent.getLastPaymentError() != null) {
                result.setFailureReason(stripeIntent.getLastPaymentError().getMessage());
            }

            if ("succeeded".equals(stripeIntent.getStatus())) {
                result.setProcessedAt(Instant.now());
            }

            return result;

        } catch (StripeException e) {
            throw new PaymentException(PAYMENT_FAILED, "Failed to confirm payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentResult capturePayment(String paymentIntentId) throws PaymentException {
        initializeStripe();

        try {
            com.stripe.model.PaymentIntent stripeIntent = com.stripe.model.PaymentIntent.retrieve(paymentIntentId);

            if ("requires_capture".equals(stripeIntent.getStatus())) {
                stripeIntent = stripeIntent.capture();
            }

            PaymentResult result = new PaymentResult();
            result.setPaymentIntentId(stripeIntent.getId());
            result.setPaymentId(stripeIntent.getLatestCharge());
            result.setStatus(stripeIntent.getStatus());
            result.setTransactionId(stripeIntent.getId());
            result.setAmount(new BigDecimal(stripeIntent.getAmount()).divide(new BigDecimal("100")));
            result.setCurrency(stripeIntent.getCurrency().toUpperCase());
            result.setProcessedAt(Instant.now());

            return result;

        } catch (StripeException e) {
            throw new PaymentException(PAYMENT_FAILED, "Failed to capture payment: " + e.getMessage(), e);
        }
    }

    @Override
    public RefundResult refundPayment(String paymentIntentId, BigDecimal amount, String reason) throws PaymentException {
        initializeStripe();

        try {
            // Get the charge ID from the payment intent
            com.stripe.model.PaymentIntent stripeIntent = com.stripe.model.PaymentIntent.retrieve(paymentIntentId);
            String chargeId = stripeIntent.getLatestCharge();

            if (chargeId == null) {
                throw new PaymentException(REFUND_FAILED, "No charge found for payment intent");
            }

            RefundCreateParams.Builder refundParamsBuilder = RefundCreateParams.builder()
                    .setCharge(chargeId)
                    .setReason(mapRefundReason(reason));

            if (amount != null) {
                long amountInCents = amount.multiply(new BigDecimal("100")).longValue();
                refundParamsBuilder.setAmount(amountInCents);
            }

            RefundCreateParams refundParams = refundParamsBuilder.build();
            Refund refund = Refund.create(refundParams);

            RefundResult result = new RefundResult();
            result.setRefundId(refund.getId());
            result.setPaymentIntentId(paymentIntentId);
            result.setPaymentId(chargeId);
            result.setAmount(new BigDecimal(refund.getAmount()).divide(new BigDecimal("100")));
            result.setCurrency(refund.getCurrency().toUpperCase());
            result.setStatus(refund.getStatus());
            result.setReason(reason);
            result.setProcessedAt(Instant.ofEpochSecond(refund.getCreated()));

            return result;

        } catch (StripeException e) {
            throw new PaymentException(REFUND_FAILED, "Failed to refund payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentIntentId) throws PaymentException {
        initializeStripe();

        try {
            com.stripe.model.PaymentIntent stripeIntent = com.stripe.model.PaymentIntent.retrieve(paymentIntentId);

            PaymentStatus status = new PaymentStatus();
            status.setStatus(stripeIntent.getStatus());
            status.setDescription(getStatusDescription(stripeIntent.getStatus()));
            status.setLastUpdated(Instant.ofEpochSecond(stripeIntent.getCreated()));

            return status;

        } catch (StripeException e) {
            throw new PaymentException(NOT_FOUND, "Payment intent not found: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer createCustomer(String email, String name, String phone) throws PaymentException {
        initializeStripe();

        try {
            CustomerCreateParams.Builder paramsBuilder = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name);

            if (phone != null && !phone.isEmpty()) {
                paramsBuilder.setPhone(phone);
            }

            CustomerCreateParams params = paramsBuilder.build();
            com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.create(params);

            Customer customer = new Customer();
            customer.setId(stripeCustomer.getId());
            customer.setEmail(stripeCustomer.getEmail());
            customer.setName(stripeCustomer.getName());
            customer.setPhone(stripeCustomer.getPhone());
            customer.setCreatedAt(Instant.ofEpochSecond(stripeCustomer.getCreated()));

            return customer;

        } catch (StripeException e) {
            throw new PaymentException(INVALID_REQUEST, "Failed to create customer: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentMethod createPaymentMethod(String customerId, String paymentMethodType,
                                           PaymentMethodDetails paymentMethodDetails) throws PaymentException {
        initializeStripe();

        try {
            com.stripe.model.PaymentMethod stripePaymentMethod;

            if ("CARD".equals(paymentMethodType)) {
                // Create payment method from card details
                PaymentMethodCreateParams.CardDetails cardDetails = PaymentMethodCreateParams.CardDetails.builder()
                        .setNumber(paymentMethodDetails.getCardNumber())
                        .setExpMonth(Long.parseLong(paymentMethodDetails.getCardExpiryMonth()))
                        .setExpYear(Long.parseLong(paymentMethodDetails.getCardExpiryYear()))
                        .setCvc(paymentMethodDetails.getCardCvc())
                        .build();

                PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                        .setType(PaymentMethodCreateParams.Type.CARD)
                        .setCard(cardDetails)
                        .build();

                stripePaymentMethod = com.stripe.model.PaymentMethod.create(params);

                // Attach to customer
                PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                        .setCustomer(customerId)
                        .build();
                stripePaymentMethod = stripePaymentMethod.attach(attachParams);

            } else {
                throw new PaymentException(INVALID_REQUEST, "Unsupported payment method type: " + paymentMethodType);
            }

            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setId(stripePaymentMethod.getId());
            paymentMethod.setCustomerId(customerId);
            paymentMethod.setType(stripePaymentMethod.getType());

            if (stripePaymentMethod.getCard() != null) {
                paymentMethod.setBrand(stripePaymentMethod.getCard().getBrand());
                paymentMethod.setLast4Digits(stripePaymentMethod.getCard().getLast4());
                paymentMethod.setExpiryMonth(String.valueOf(stripePaymentMethod.getCard().getExpMonth()));
                paymentMethod.setExpiryYear(String.valueOf(stripePaymentMethod.getCard().getExpYear()));
            }

            paymentMethod.setCreatedAt(Instant.now());

            return paymentMethod;

        } catch (StripeException e) {
            throw new PaymentException(INVALID_CARD, "Failed to create payment method: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws PaymentException {
        initializeStripe();

        try {
            PaymentMethodListParams params = PaymentMethodListParams.builder()
                    .setCustomer(customerId)
                    .setType(PaymentMethodListParams.Type.CARD)
                    .build();

            PaymentMethodCollection paymentMethods = com.stripe.model.PaymentMethod.list(params);

            List<PaymentMethod> result = new ArrayList<>();
            for (com.stripe.model.PaymentMethod stripePaymentMethod : paymentMethods.getData()) {
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setId(stripePaymentMethod.getId());
                paymentMethod.setCustomerId(customerId);
                paymentMethod.setType(stripePaymentMethod.getType());

                if (stripePaymentMethod.getCard() != null) {
                    paymentMethod.setBrand(stripePaymentMethod.getCard().getBrand());
                    paymentMethod.setLast4Digits(stripePaymentMethod.getCard().getLast4());
                    paymentMethod.setExpiryMonth(String.valueOf(stripePaymentMethod.getCard().getExpMonth()));
                    paymentMethod.setExpiryYear(String.valueOf(stripePaymentMethod.getCard().getExpYear()));
                }

                paymentMethod.setCreatedAt(Instant.now());
                result.add(paymentMethod);
            }

            return result;

        } catch (StripeException e) {
            throw new PaymentException(CUSTOMER_NOT_FOUND, "Failed to get payment methods: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePaymentMethod(String paymentMethodId) throws PaymentException {
        initializeStripe();

        try {
            com.stripe.model.PaymentMethod paymentMethod = com.stripe.model.PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.detach();

        } catch (StripeException e) {
            throw new PaymentException(PAYMENT_METHOD_NOT_FOUND, "Failed to delete payment method: " + e.getMessage(), e);
        }
    }

    @Override
    public SetupIntent createSetupIntent(String customerId, String paymentMethodType) throws PaymentException {
        initializeStripe();

        try {
            SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                    .setCustomer(customerId)
                    .addPaymentMethodType(paymentMethodType.toLowerCase())
                    .build();

            com.stripe.model.SetupIntent stripeSetupIntent = com.stripe.model.SetupIntent.create(params);

            SetupIntent setupIntent = new SetupIntent();
            setupIntent.setId(stripeSetupIntent.getId());
            setupIntent.setClientSecret(stripeSetupIntent.getClientSecret());
            setupIntent.setStatus(stripeSetupIntent.getStatus());
            setupIntent.setCustomerId(customerId);
            setupIntent.setPaymentMethodType(paymentMethodType);
            setupIntent.setCreatedAt(Instant.ofEpochSecond(stripeSetupIntent.getCreated()));

            return setupIntent;

        } catch (StripeException e) {
            throw new PaymentException(SETUP_FAILED, "Failed to create setup intent: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature, String secret) throws PaymentException {
        try {
            initializeStripe();

            Event event = Webhook.constructEvent(payload, signature, secret != null ? secret : webhookSecret);
            return event != null;

        } catch (Exception e) {
            throw new PaymentException(INVALID_WEBHOOK_SIGNATURE, "Webhook signature verification failed: " + e.getMessage(), e);
        }
    }

    @Override
    public WebhookEvent parseWebhookEvent(String payload) throws PaymentException {
        try {
            initializeStripe();

            Event event = Event.GSON.fromJson(payload, Event.class);

            WebhookEvent webhookEvent = new WebhookEvent();
            webhookEvent.setId(event.getId());
            webhookEvent.setType(event.getType());
            webhookEvent.setEventType(event.getType());
            webhookEvent.setData(event.getData().toJson());
            webhookEvent.setCreated(Instant.ofEpochSecond(event.getCreated()));

            return webhookEvent;

        } catch (Exception e) {
            throw new PaymentException(WEBHOOK_VERIFICATION_FAILED, "Failed to parse webhook event: " + e.getMessage(), e);
        }
    }

    /**
     * Map refund reason to Stripe refund reason.
     */
    private RefundCreateParams.Reason mapRefundReason(String reason) {
        if (reason == null) {
            return RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
        }

        switch (reason.toLowerCase()) {
            case "duplicate":
                return RefundCreateParams.Reason.DUPLICATE;
            case "fraudulent":
                return RefundCreateParams.Reason.FRAUDULENT;
            default:
                return RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
        }
    }

    /**
     * Get human-readable description for payment status.
     */
    private String getStatusDescription(String status) {
        switch (status) {
            case "requires_payment_method":
                return "Payment method is required";
            case "requires_confirmation":
                return "Payment requires confirmation";
            case "requires_action":
                return "Payment requires additional action";
            case "processing":
                return "Payment is being processed";
            case "requires_capture":
                return "Payment is authorized and requires capture";
            case "canceled":
                return "Payment has been canceled";
            case "succeeded":
                return "Payment succeeded";
            default:
                return "Unknown status: " + status;
        }
    }
}
