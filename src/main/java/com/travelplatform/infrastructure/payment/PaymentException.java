package com.travelplatform.infrastructure.payment;

/**
 * Exception thrown when payment operations fail.
 */
public class PaymentException extends RuntimeException {

    private final String errorCode;

    public PaymentException(String message) {
        super(message);
        this.errorCode = "PAYMENT_ERROR";
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PAYMENT_ERROR";
    }

    public PaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Common error codes
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String PAYMENT_DECLINED = "PAYMENT_DECLINED";
    public static final String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
    public static final String INVALID_CARD = "INVALID_CARD";
    public static final String EXPIRED_CARD = "EXPIRED_CARD";
    public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
    public static final String INVALID_CURRENCY = "INVALID_CURRENCY";
    public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";
    public static final String PAYMENT_METHOD_NOT_FOUND = "PAYMENT_METHOD_NOT_FOUND";
    public static final String DUPLICATE_PAYMENT = "DUPLICATE_PAYMENT";
    public static final String REFUND_FAILED = "REFUND_FAILED";
    public static final String REFUND_LIMIT_EXCEEDED = "REFUND_LIMIT_EXCEEDED";
    public static final String WEBHOOK_VERIFICATION_FAILED = "WEBHOOK_VERIFICATION_FAILED";
    public static final String INVALID_WEBHOOK_SIGNATURE = "INVALID_WEBHOOK_SIGNATURE";
    public static final String SETUP_FAILED = "SETUP_FAILED";
    public static final String NETWORK_ERROR = "NETWORK_ERROR";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
}
