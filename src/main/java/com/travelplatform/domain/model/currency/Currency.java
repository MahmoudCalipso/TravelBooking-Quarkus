package com.travelplatform.domain.model.currency;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a currency and its originating country.
 */
public class Currency {
    private final UUID id;
    private String countryName;
    private String currencyCode;
    private String currencySymbol;
    private java.math.BigDecimal exchangeRate;
    private Integer decimalPlaces;
    private boolean active;
    private boolean isBase;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates a new Currency with the provided details.
     *
     * @param countryName    country name associated with the currency
     * @param currencyCode   ISO 4217 currency code (e.g., USD)
     * @param currencySymbol symbol used for the currency (e.g., $)
     */
    public Currency(String countryName, String currencyCode, String currencySymbol) {
        validateInputs(countryName, currencyCode, currencySymbol);
        this.id = UUID.randomUUID();
        this.countryName = countryName.trim();
        this.currencyCode = normalizeCode(currencyCode);
        this.currencySymbol = currencySymbol.trim();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * No-arg constructor for frameworks.
     */
    public Currency() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.active = true;
        this.isBase = false;
        this.exchangeRate = java.math.BigDecimal.ONE;
        this.decimalPlaces = 2;
    }

    /**
     * Reconstructs a Currency from persistence.
     */
    public Currency(UUID id, String countryName, String currencyCode, String currencySymbol,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateInputs(countryName, currencyCode, currencySymbol);
        this.id = id;
        this.countryName = countryName.trim();
        this.currencyCode = normalizeCode(currencyCode);
        this.currencySymbol = currencySymbol.trim();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt != null ? updatedAt : createdAt;
    }

    private void validateInputs(String countryName, String currencyCode, String currencySymbol) {
        if (countryName == null || countryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Country name cannot be null or empty");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        if (currencyCode.trim().length() != 3) {
            throw new IllegalArgumentException("Currency code must be a 3-letter ISO code");
        }
        if (currencySymbol == null || currencySymbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency symbol cannot be null or empty");
        }
    }

    private String normalizeCode(String currencyCode) {
        return currencyCode.trim().toUpperCase();
    }

    public UUID getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the country name.
     */
    public void setCountryName(String countryName) {
        if (countryName != null && !countryName.trim().isEmpty()) {
            this.countryName = countryName.trim();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the currency code.
     */
    public void setCurrencyCode(String currencyCode) {
        if (currencyCode != null && !currencyCode.trim().isEmpty()) {
            if (currencyCode.trim().length() != 3) {
                throw new IllegalArgumentException("Currency code must be a 3-letter ISO code");
            }
            this.currencyCode = normalizeCode(currencyCode);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the currency symbol.
     */
    public void setCurrencySymbol(String currencySymbol) {
        if (currencySymbol != null && !currencySymbol.trim().isEmpty()) {
            this.currencySymbol = currencySymbol.trim();
            this.updatedAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Currency currency = (Currency) o;
        return id.equals(currency.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Currency{id=%s, code='%s', country='%s'}", id, currencyCode, countryName);
    }
}
