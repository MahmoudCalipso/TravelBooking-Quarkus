package com.travelplatform.infrastructure.payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment Gateway interface for processing payments.
 * This interface defines the contract for payment operations,
 * allowing different implementations (Stripe, PayPal, Bank Transfer, etc.).
 */
public interface PaymentGateway {

    /**
     * Create a payment intent for a booking.
     *
     * @param bookingId The booking ID
     * @param amount The payment amount
     * @param currency The currency code (e.g., "USD")
     * @param paymentMethod The payment method (CARD, PAYPAL, BANK_TRANSFER, CRYPTO)
     * @param description The payment description
     * @return PaymentIntent containing the payment details
     * @throws PaymentException if payment creation fails
     */
    PaymentIntent createPaymentIntent(UUID bookingId, BigDecimal amount, String currency, 
                                      String paymentMethod, String description) throws PaymentException;

    /**
     * Confirm a payment intent.
     *
     * @param paymentIntentId The payment intent ID
     * @param paymentMethodId The payment method ID from the payment provider
     * @return PaymentResult containing the payment result
     * @throws PaymentException if payment confirmation fails
     */
    PaymentResult confirmPayment(String paymentIntentId, String paymentMethodId) throws PaymentException;

    /**
     * Capture a payment (for card payments that require capture).
     *
     * @param paymentIntentId The payment intent ID
     * @return PaymentResult containing the payment result
     * @throws PaymentException if payment capture fails
     */
    PaymentResult capturePayment(String paymentIntentId) throws PaymentException;

    /**
     * Refund a payment.
     *
     * @param paymentIntentId The payment intent ID
     * @param amount The amount to refund (null for full refund)
     * @param reason The reason for the refund
     * @return RefundResult containing the refund details
     * @throws PaymentException if refund fails
     */
    RefundResult refundPayment(String paymentIntentId, BigDecimal amount, String reason) throws PaymentException;

    /**
     * Get payment status.
     *
     * @param paymentIntentId The payment intent ID
     * @return PaymentStatus containing the current status
     * @throws PaymentException if status retrieval fails
     */
    PaymentStatus getPaymentStatus(String paymentIntentId) throws PaymentException;

    /**
     * Create a customer.
     *
     * @param email The customer email
     * @param name The customer name
     * @param phone The customer phone number (optional)
     * @return Customer containing the customer details
     * @throws PaymentException if customer creation fails
     */
    Customer createCustomer(String email, String name, String phone) throws PaymentException;

    /**
     * Create a payment method for a customer.
     *
     * @param customerId The customer ID
     * @param paymentMethodType The payment method type (CARD, PAYPAL, BANK_ACCOUNT)
     * @param paymentMethodDetails The payment method details (card token, PayPal email, etc.)
     * @return PaymentMethod containing the payment method details
     * @throws PaymentException if payment method creation fails
     */
    PaymentMethod createPaymentMethod(String customerId, String paymentMethodType, 
                                       PaymentMethodDetails paymentMethodDetails) throws PaymentException;

    /**
     * Get customer payment methods.
     *
     * @param customerId The customer ID
     * @return List of payment methods
     * @throws PaymentException if retrieval fails
     */
    java.util.List<PaymentMethod> getCustomerPaymentMethods(String customerId) throws PaymentException;

    /**
     * Delete a payment method.
     *
     * @param paymentMethodId The payment method ID
     * @throws PaymentException if deletion fails
     */
    void deletePaymentMethod(String paymentMethodId) throws PaymentException;

    /**
     * Create a setup intent for saving payment methods.
     *
     * @param customerId The customer ID
     * @param paymentMethodType The payment method type
     * @return SetupIntent containing the setup intent details
     * @throws PaymentException if setup intent creation fails
     */
    SetupIntent createSetupIntent(String customerId, String paymentMethodType) throws PaymentException;

    /**
     * Verify webhook signature.
     *
     * @param payload The webhook payload
     * @param signature The webhook signature
     * @param secret The webhook secret
     * @return true if signature is valid, false otherwise
     */
    boolean verifyWebhookSignature(String payload, String signature, String secret);

    /**
     * Parse webhook event.
     *
     * @param payload The webhook payload
     * @return WebhookEvent containing the event details
     * @throws PaymentException if parsing fails
     */
    WebhookEvent parseWebhookEvent(String payload) throws PaymentException;

    /**
     * Payment intent class.
     */
    class PaymentIntent {
        private String id;
        private String clientSecret;
        private String status;
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;
        private String description;
        private java.time.Instant createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public java.time.Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.Instant createdAt) {
            this.createdAt = createdAt;
        }
    }

    /**
     * Payment result class.
     */
    class PaymentResult {
        private String paymentIntentId;
        private String paymentId;
        private String status;
        private String transactionId;
        private BigDecimal amount;
        private String currency;
        private String failureReason;
        private java.time.Instant processedAt;

        public String getPaymentIntentId() {
            return paymentIntentId;
        }

        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public void setFailureReason(String failureReason) {
            this.failureReason = failureReason;
        }

        public java.time.Instant getProcessedAt() {
            return processedAt;
        }

        public void setProcessedAt(java.time.Instant processedAt) {
            this.processedAt = processedAt;
        }
    }

    /**
     * Refund result class.
     */
    class RefundResult {
        private String refundId;
        private String paymentIntentId;
        private String paymentId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private String reason;
        private java.time.Instant processedAt;

        public String getRefundId() {
            return refundId;
        }

        public void setRefundId(String refundId) {
            this.refundId = refundId;
        }

        public String getPaymentIntentId() {
            return paymentIntentId;
        }

        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public java.time.Instant getProcessedAt() {
            return processedAt;
        }

        public void setProcessedAt(java.time.Instant processedAt) {
            this.processedAt = processedAt;
        }
    }

    /**
     * Payment status class.
     */
    class PaymentStatus {
        private String status;
        private String description;
        private java.time.Instant lastUpdated;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public java.time.Instant getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(java.time.Instant lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
    }

    /**
     * Customer class.
     */
    class Customer {
        private String id;
        private String email;
        private String name;
        private String phone;
        private String defaultPaymentMethodId;
        private java.time.Instant createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDefaultPaymentMethodId() {
            return defaultPaymentMethodId;
        }

        public void setDefaultPaymentMethodId(String defaultPaymentMethodId) {
            this.defaultPaymentMethodId = defaultPaymentMethodId;
        }

        public java.time.Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.Instant createdAt) {
            this.createdAt = createdAt;
        }
    }

    /**
     * Payment method class.
     */
    class PaymentMethod {
        private String id;
        private String customerId;
        private String type;
        private String brand;
        private String last4Digits;
        private String expiryMonth;
        private String expiryYear;
        private boolean isDefault;
        private java.time.Instant createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getLast4Digits() {
            return last4Digits;
        }

        public void setLast4Digits(String last4Digits) {
            this.last4Digits = last4Digits;
        }

        public String getExpiryMonth() {
            return expiryMonth;
        }

        public void setExpiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
        }

        public String getExpiryYear() {
            return expiryYear;
        }

        public void setExpiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean isDefault) {
            this.isDefault = isDefault;
        }

        public java.time.Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.Instant createdAt) {
            this.createdAt = createdAt;
        }
    }

    /**
     * Payment method details class.
     */
    class PaymentMethodDetails {
        private String cardToken;
        private String cardNumber;
        private String cardExpiryMonth;
        private String cardExpiryYear;
        private String cardCvc;
        private String cardHolderName;
        private String paypalEmail;
        private String bankAccountNumber;
        private String bankRoutingNumber;

        public String getCardToken() {
            return cardToken;
        }

        public void setCardToken(String cardToken) {
            this.cardToken = cardToken;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getCardExpiryMonth() {
            return cardExpiryMonth;
        }

        public void setCardExpiryMonth(String cardExpiryMonth) {
            this.cardExpiryMonth = cardExpiryMonth;
        }

        public String getCardExpiryYear() {
            return cardExpiryYear;
        }

        public void setCardExpiryYear(String cardExpiryYear) {
            this.cardExpiryYear = cardExpiryYear;
        }

        public String getCardCvc() {
            return cardCvc;
        }

        public void setCardCvc(String cardCvc) {
            this.cardCvc = cardCvc;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getPaypalEmail() {
            return paypalEmail;
        }

        public void setPaypalEmail(String paypalEmail) {
            this.paypalEmail = paypalEmail;
        }

        public String getBankAccountNumber() {
            return bankAccountNumber;
        }

        public void setBankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        public String getBankRoutingNumber() {
            return bankRoutingNumber;
        }

        public void setBankRoutingNumber(String bankRoutingNumber) {
            this.bankRoutingNumber = bankRoutingNumber;
        }
    }

    /**
     * Setup intent class.
     */
    class SetupIntent {
        private String id;
        private String clientSecret;
        private String status;
        private String customerId;
        private String paymentMethodType;
        private java.time.Instant createdAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getPaymentMethodType() {
            return paymentMethodType;
        }

        public void setPaymentMethodType(String paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
        }

        public java.time.Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(java.time.Instant createdAt) {
            this.createdAt = createdAt;
        }
    }

    /**
     * Webhook event class.
     */
    class WebhookEvent {
        private String id;
        private String type;
        private String eventType;
        private String data;
        private java.time.Instant created;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public java.time.Instant getCreated() {
            return created;
        }

        public void setCreated(java.time.Instant created) {
            this.created = created;
        }
    }
}
