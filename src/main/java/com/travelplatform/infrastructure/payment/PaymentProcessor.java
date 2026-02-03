package com.travelplatform.infrastructure.payment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment processor service that orchestrates payment operations.
 * This service provides high-level payment processing functionality
 * using the PaymentGateway implementation.
 */
@ApplicationScoped
public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    @Inject
    private PaymentGateway paymentGateway;

    /**
     * Process a booking payment.
     *
     * @param bookingId The booking ID
     * @param amount The payment amount
     * @param currency The currency code
     * @param paymentMethod The payment method type
     * @param description The payment description
     * @return PaymentIntent containing the payment details
     * @throws PaymentException if payment processing fails
     */
    @Transactional
    public PaymentGateway.PaymentIntent processBookingPayment(UUID bookingId, BigDecimal amount, 
                                                         String currency, String paymentMethod, 
                                                         String description) throws PaymentException {
        log.info("Processing booking payment for booking: {}, amount: {} {}", bookingId, amount, currency);
        
        try {
            PaymentGateway.PaymentIntent intent = paymentGateway.createPaymentIntent(
                    bookingId, amount, currency, paymentMethod, description);
            
            log.info("Payment intent created successfully: {}", intent.getId());
            return intent;
            
        } catch (PaymentException e) {
            log.error("Failed to process booking payment for booking: {}", bookingId, e);
            throw e;
        }
    }

    /**
     * Confirm a payment.
     *
     * @param paymentIntentId The payment intent ID
     * @param paymentMethodId The payment method ID
     * @return PaymentResult containing the payment result
     * @throws PaymentException if payment confirmation fails
     */
    @Transactional
    public PaymentGateway.PaymentResult confirmPayment(String paymentIntentId, String paymentMethodId) throws PaymentException {
        log.info("Confirming payment: {}", paymentIntentId);
        
        try {
            PaymentGateway.PaymentResult result = paymentGateway.confirmPayment(paymentIntentId, paymentMethodId);
            
            if ("succeeded".equals(result.getStatus())) {
                log.info("Payment confirmed successfully: {}", paymentIntentId);
            } else {
                log.warn("Payment confirmation returned status: {} for payment: {}", result.getStatus(), paymentIntentId);
            }
            
            return result;
            
        } catch (PaymentException e) {
            log.error("Failed to confirm payment: {}", paymentIntentId, e);
            throw e;
        }
    }

    /**
     * Capture a payment.
     *
     * @param paymentIntentId The payment intent ID
     * @return PaymentResult containing the payment result
     * @throws PaymentException if payment capture fails
     */
    @Transactional
    public PaymentGateway.PaymentResult capturePayment(String paymentIntentId) throws PaymentException {
        log.info("Capturing payment: {}", paymentIntentId);
        
        try {
            PaymentGateway.PaymentResult result = paymentGateway.capturePayment(paymentIntentId);
            
            if ("succeeded".equals(result.getStatus())) {
                log.info("Payment captured successfully: {}", paymentIntentId);
            }
            
            return result;
            
        } catch (PaymentException e) {
            log.error("Failed to capture payment: {}", paymentIntentId, e);
            throw e;
        }
    }

    /**
     * Refund a payment.
     *
     * @param paymentIntentId The payment intent ID
     * @param amount The amount to refund (null for full refund)
     * @param reason The reason for the refund
     * @return RefundResult containing the refund details
     * @throws PaymentException if refund fails
     */
    @Transactional
    public PaymentGateway.RefundResult refundPayment(String paymentIntentId, BigDecimal amount, String reason) throws PaymentException {
        log.info("Refunding payment: {}, amount: {}, reason: {}", paymentIntentId, amount, reason);
        
        try {
            PaymentGateway.RefundResult result = paymentGateway.refundPayment(paymentIntentId, amount, reason);
            
            if ("succeeded".equals(result.getStatus())) {
                log.info("Payment refunded successfully: {}, refund amount: {}", paymentIntentId, result.getAmount());
            }
            
            return result;
            
        } catch (PaymentException e) {
            log.error("Failed to refund payment: {}", paymentIntentId, e);
            throw e;
        }
    }

    /**
     * Get payment status.
     *
     * @param paymentIntentId The payment intent ID
     * @return PaymentStatus containing the current status
     * @throws PaymentException if status retrieval fails
     */
    public PaymentGateway.PaymentStatus getPaymentStatus(String paymentIntentId) throws PaymentException {
        log.debug("Getting payment status for: {}", paymentIntentId);
        
        try {
            return paymentGateway.getPaymentStatus(paymentIntentId);
            
        } catch (PaymentException e) {
            log.error("Failed to get payment status for: {}", paymentIntentId, e);
            throw e;
        }
    }

    /**
     * Create a customer.
     *
     * @param email The customer email
     * @param name The customer name
     * @param phone The customer phone number (optional)
     * @return Customer containing the customer details
     * @throws PaymentException if customer creation fails
     */
    @Transactional
    public PaymentGateway.Customer createCustomer(String email, String name, String phone) throws PaymentException {
        log.info("Creating customer for email: {}", email);
        
        try {
            PaymentGateway.Customer customer = paymentGateway.createCustomer(email, name, phone);
            
            log.info("Customer created successfully: {}", customer.getId());
            return customer;
            
        } catch (PaymentException e) {
            log.error("Failed to create customer for email: {}", email, e);
            throw e;
        }
    }

    /**
     * Create a payment method for a customer.
     *
     * @param customerId The customer ID
     * @param paymentMethodType The payment method type
     * @param paymentMethodDetails The payment method details
     * @return PaymentMethod containing the payment method details
     * @throws PaymentException if payment method creation fails
     */
    @Transactional
    public PaymentGateway.PaymentMethod createPaymentMethod(String customerId, String paymentMethodType,
                                                         PaymentGateway.PaymentMethodDetails paymentMethodDetails) throws PaymentException {
        log.info("Creating payment method for customer: {}, type: {}", customerId, paymentMethodType);
        
        try {
            PaymentGateway.PaymentMethod paymentMethod = paymentGateway.createPaymentMethod(
                    customerId, paymentMethodType, paymentMethodDetails);
            
            log.info("Payment method created successfully: {}", paymentMethod.getId());
            return paymentMethod;
            
        } catch (PaymentException e) {
            log.error("Failed to create payment method for customer: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Get customer payment methods.
     *
     * @param customerId The customer ID
     * @return List of payment methods
     * @throws PaymentException if retrieval fails
     */
    public java.util.List<PaymentGateway.PaymentMethod> getCustomerPaymentMethods(String customerId) throws PaymentException {
        log.debug("Getting payment methods for customer: {}", customerId);
        
        try {
            return paymentGateway.getCustomerPaymentMethods(customerId);
            
        } catch (PaymentException e) {
            log.error("Failed to get payment methods for customer: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Delete a payment method.
     *
     * @param paymentMethodId The payment method ID
     * @throws PaymentException if deletion fails
     */
    @Transactional
    public void deletePaymentMethod(String paymentMethodId) throws PaymentException {
        log.info("Deleting payment method: {}", paymentMethodId);
        
        try {
            paymentGateway.deletePaymentMethod(paymentMethodId);
            
            log.info("Payment method deleted successfully: {}", paymentMethodId);
            
        } catch (PaymentException e) {
            log.error("Failed to delete payment method: {}", paymentMethodId, e);
            throw e;
        }
    }

    /**
     * Create a setup intent for saving payment methods.
     *
     * @param customerId The customer ID
     * @param paymentMethodType The payment method type
     * @return SetupIntent containing the setup intent details
     * @throws PaymentException if setup intent creation fails
     */
    @Transactional
    public PaymentGateway.SetupIntent createSetupIntent(String customerId, String paymentMethodType) throws PaymentException {
        log.info("Creating setup intent for customer: {}, type: {}", customerId, paymentMethodType);
        
        try {
            PaymentGateway.SetupIntent setupIntent = paymentGateway.createSetupIntent(customerId, paymentMethodType);
            
            log.info("Setup intent created successfully: {}", setupIntent.getId());
            return setupIntent;
            
        } catch (PaymentException e) {
            log.error("Failed to create setup intent for customer: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Verify webhook signature.
     *
     * @param payload The webhook payload
     * @param signature The webhook signature
     * @param secret The webhook secret
     * @return true if signature is valid, false otherwise
     */
    public boolean verifyWebhookSignature(String payload, String signature, String secret) {
        log.debug("Verifying webhook signature");
        
        try {
            boolean isValid = paymentGateway.verifyWebhookSignature(payload, signature, secret);
            
            if (isValid) {
                log.info("Webhook signature verified successfully");
            } else {
                log.warn("Webhook signature verification failed");
            }
            
            return isValid;
            
        } catch (PaymentException e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    /**
     * Parse webhook event.
     *
     * @param payload The webhook payload
     * @return WebhookEvent containing the event details
     * @throws PaymentException if parsing fails
     */
    public PaymentGateway.WebhookEvent parseWebhookEvent(String payload) throws PaymentException {
        log.debug("Parsing webhook event");
        
        try {
            PaymentGateway.WebhookEvent event = paymentGateway.parseWebhookEvent(payload);
            
            log.info("Webhook event parsed: type: {}, id: {}", event.getEventType(), event.getId());
            return event;
            
        } catch (PaymentException e) {
            log.error("Failed to parse webhook event", e);
            throw e;
        }
    }

    /**
     * Check if payment is successful.
     *
     * @param status The payment status
     * @return true if payment is successful, false otherwise
     */
    public boolean isPaymentSuccessful(String status) {
        return "succeeded".equals(status);
    }

    /**
     * Check if payment is pending.
     *
     * @param status The payment status
     * @return true if payment is pending, false otherwise
     */
    public boolean isPaymentPending(String status) {
        return "processing".equals(status) || 
               "requires_action".equals(status) || 
               "requires_confirmation".equals(status);
    }

    /**
     * Check if payment is failed.
     *
     * @param status The payment status
     * @return true if payment is failed, false otherwise
     */
    public boolean isPaymentFailed(String status) {
        return "canceled".equals(status) || 
               "requires_payment_method".equals(status);
    }

    /**
     * Check if refund is successful.
     *
     * @param status The refund status
     * @return true if refund is successful, false otherwise
     */
    public boolean isRefundSuccessful(String status) {
        return "succeeded".equals(status);
    }

    /**
     * Get human-readable payment status description.
     *
     * @param status The payment status
     * @return Human-readable description
     */
    public String getPaymentStatusDescription(String status) {
        switch (status) {
            case "succeeded":
                return "Payment completed successfully";
            case "processing":
                return "Payment is being processed";
            case "requires_payment_method":
                return "Payment method required";
            case "requires_confirmation":
                return "Payment requires confirmation";
            case "requires_action":
                return "Payment requires additional action";
            case "requires_capture":
                return "Payment authorized, awaiting capture";
            case "canceled":
                return "Payment canceled";
            default:
                return "Unknown status: " + status;
        }
    }
}
