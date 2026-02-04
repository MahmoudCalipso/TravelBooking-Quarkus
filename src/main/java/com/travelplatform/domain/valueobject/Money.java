package com.travelplatform.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a monetary amount with currency.
 * Immutable object - once created, cannot be modified.
 */
public class Money {
    private final BigDecimal amount;
    private final String currencyCode;

    /**
     * Creates a new Money value with the specified amount and currency.
     *
     * @param amount   the monetary amount (must be non-negative)
     * @param currency the currency code (e.g., "USD", "EUR")
     * @throws IllegalArgumentException if amount is negative or currency is invalid
     */
    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        String normalized = currency.trim().toUpperCase();
        if (!normalized.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Currency code must be a 3-letter ISO code");
        }

        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currencyCode = normalized;
    }

    /**
     * Creates a new Money value with the specified amount and currency.
     *
     * @param amount   the monetary amount (must be non-negative)
     * @param currency the currency code (e.g., "USD", "EUR")
     * @throws IllegalArgumentException if amount is negative or currency is invalid
     */
    public Money(double amount, String currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    public Money(int amount, String currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Adds another Money value to this one.
     * Currencies must match.
     *
     * @param other the other Money value to add
     * @return a new Money value with the sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money cannot be null");
        }
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currencyCode);
    }

    /**
     * Subtracts another Money value from this one.
     * Currencies must match.
     *
     * @param other the other Money value to subtract
     * @return a new Money value with the difference
     * @throws IllegalArgumentException if currencies don't match or result is
     *                                  negative
     */
    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money cannot be null");
        }
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot subtract money with different currencies");
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(result, this.currencyCode);
    }

    /**
     * Multiplies this Money value by a factor.
     *
     * @param factor the multiplication factor (must be non-negative)
     * @return a new Money value with the product
     * @throws IllegalArgumentException if factor is negative
     */
    public Money multiply(double factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }
        BigDecimal result = this.amount.multiply(BigDecimal.valueOf(factor)).setScale(2, RoundingMode.HALF_UP);
        return new Money(result, this.currencyCode);
    }

    /**
     * Multiplies this Money value by a factor.
     *
     * @param factor the multiplication factor (must be non-negative)
     * @return a new Money value with the product
     * @throws IllegalArgumentException if factor is negative
     */
    public Money multiply(BigDecimal factor) {
        if (factor == null || factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Factor cannot be null or negative");
        }
        return new Money(this.amount.multiply(factor), this.currencyCode);
    }

    /**
     * Divides this Money value by a divisor.
     *
     * @param divisor the divisor (must be positive)
     * @return a new Money value with the quotient
     * @throws IllegalArgumentException if divisor is zero or negative
     */
    public Money divide(double divisor) {
        if (divisor <= 0) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        return new Money(this.amount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP), this.currencyCode);
    }

    /**
     * Calculates a percentage of this Money value.
     *
     * @param percentage the percentage (e.g., 10 for 10%)
     * @return a new Money value with the percentage amount
     */
    public Money percentage(double percentage) {
        return multiply(percentage / 100.0);
    }

    /**
     * Checks if this Money value is greater than another.
     *
     * @param other the other Money value to compare
     * @return true if this is greater than other
     */
    public boolean isGreaterThan(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money cannot be null");
        }
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies");
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Checks if this Money value is less than another.
     *
     * @param other the other Money value to compare
     * @return true if this is less than other
     */
    public boolean isLessThan(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money cannot be null");
        }
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies");
        }
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Checks if this Money value is zero.
     *
     * @return true if amount is zero
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currencyCode.equals(money.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currencyCode);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", currencyCode, amount);
    }
}
