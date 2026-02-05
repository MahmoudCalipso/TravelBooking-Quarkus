package com.travelplatform.application.service.global;

import com.travelplatform.domain.valueobject.Money;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple currency conversion service with in-memory rates cache.
 * Rates are expressed as target per USD for simplicity.
 */
@ApplicationScoped
public class CurrencyConversionService {

    private final Map<String, BigDecimal> usdRates = new ConcurrentHashMap<>();

    @ConfigProperty(name = "currency.rates.usd", defaultValue = "")
    String configuredRates;

    public CurrencyConversionService() {
        usdRates.put("USD", BigDecimal.ONE);
        usdRates.put("EUR", BigDecimal.valueOf(0.92));
        usdRates.put("GBP", BigDecimal.valueOf(0.78));
        usdRates.put("MAD", BigDecimal.valueOf(10.2));
    }

    @PostConstruct
    void loadConfiguredRates() {
        if (configuredRates == null || configuredRates.isBlank()) {
            return;
        }
        // Expect format: "EUR=0.92,GBP=0.78"
        String[] pairs = configuredRates.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                usdRates.put(kv[0].trim().toUpperCase(), new BigDecimal(kv[1].trim()));
            }
        }
    }

    public Money convertCurrency(Money amount, String targetCurrency) {
        if (amount.getCurrencyCode().equalsIgnoreCase(targetCurrency)) {
            return amount;
        }
        BigDecimal sourceRate = usdRates.getOrDefault(amount.getCurrencyCode(), BigDecimal.ONE);
        BigDecimal targetRate = usdRates.getOrDefault(targetCurrency.toUpperCase(), BigDecimal.ONE);
        BigDecimal amountInUsd = amount.getAmount().divide(sourceRate, 4, RoundingMode.HALF_UP);
        BigDecimal targetAmount = amountInUsd.multiply(targetRate).setScale(2, RoundingMode.HALF_UP);
        return new Money(targetAmount, targetCurrency);
    }

    public void updateRate(String currency, BigDecimal ratePerUsd) {
        usdRates.put(currency.toUpperCase(), ratePerUsd);
    }
}
