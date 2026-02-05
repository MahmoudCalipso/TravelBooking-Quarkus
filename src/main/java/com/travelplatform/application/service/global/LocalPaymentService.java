package com.travelplatform.application.service.global;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Routing layer for region-specific payment methods.
 */
@ApplicationScoped
public class LocalPaymentService {

    public PaymentResult processLocalPayment(PaymentRequest request) {
        // Placeholder routing logic based on region
        String provider;
        switch (request.region().toUpperCase()) {
            case "EU" -> provider = "SEPA";
            case "APAC" -> provider = "ALIPAY";
            case "LATAM" -> provider = "MERCADO_PAGO";
            case "MEA" -> provider = "PAYTABS";
            default -> provider = "CARD";
        }
        return new PaymentResult(true, provider, "LOCAL_TXN_" + System.currentTimeMillis());
    }

    public record PaymentRequest(String region, String currency, String method, double amount) {
    }

    public record PaymentResult(boolean success, String provider, String transactionId) {
    }
}
