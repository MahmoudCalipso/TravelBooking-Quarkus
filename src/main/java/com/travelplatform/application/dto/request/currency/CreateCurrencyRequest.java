package com.travelplatform.application.dto.request.currency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a currency.
 */
public class CreateCurrencyRequest {

    @NotBlank(message = "Country name is required")
    @Size(max = 150, message = "Country name must be at most 150 characters")
    private String countryName;

    @NotBlank(message = "Currency code is required")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency code must be a 3-letter ISO code")
    private String currencyCode;

    @NotBlank(message = "Currency symbol is required")
    @Size(max = 16, message = "Currency symbol must be at most 16 characters")
    private String currencySymbol;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}
