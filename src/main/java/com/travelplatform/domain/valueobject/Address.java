package com.travelplatform.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a physical address.
 * Immutable object - once created, cannot be modified.
 */
public class Address {
    private final String streetAddress;
    private final String city;
    private final String stateProvince;
    private final String country;
    private final String postalCode;

    /**
     * Creates a new Address with all fields.
     *
     * @param streetAddress street address (required)
     * @param city          city name (required)
     * @param stateProvince state or province (optional)
     * @param country       country name (required)
     * @param postalCode    postal/zip code (optional)
     * @throws IllegalArgumentException if required fields are null or empty
     */
    public Address(String streetAddress, String city, String stateProvince, String country, String postalCode) {
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Street address cannot be null or empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }

        this.streetAddress = streetAddress.trim();
        this.city = city.trim();
        this.stateProvince = stateProvince != null ? stateProvince.trim() : null;
        this.country = country.trim();
        this.postalCode = postalCode != null ? postalCode.trim() : null;
    }

    /**
     * Creates a new Address without state/province.
     *
     * @param streetAddress street address (required)
     * @param city          city name (required)
     * @param country       country name (required)
     * @param postalCode    postal/zip code (optional)
     */
    public Address(String streetAddress, String city, String country, String postalCode) {
        this(streetAddress, city, null, country, postalCode);
    }

    // Removed ambiguous constructor Address(String, String, String, String) to
    // avoid collision with (String, String, String, String) - postalCode variant

    /**
     * Creates a new Address with only required fields.
     *
     * @param streetAddress street address (required)
     * @param city          city name (required)
     * @param country       country name (required)
     */
    public Address(String streetAddress, String city, String country) {
        this(streetAddress, city, null, country, null);
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Checks if state/province is available.
     *
     * @return true if state/province is set
     */
    public boolean hasStateProvince() {
        return stateProvince != null && !stateProvince.isEmpty();
    }

    /**
     * Checks if postal code is available.
     *
     * @return true if postal code is set
     */
    public boolean hasPostalCode() {
        return postalCode != null && !postalCode.isEmpty();
    }

    /**
     * Returns the full address as a single line string.
     *
     * @return formatted full address
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress);

        if (hasStateProvince()) {
            sb.append(", ").append(city).append(", ").append(stateProvince);
        } else {
            sb.append(", ").append(city);
        }

        if (hasPostalCode()) {
            sb.append(" ").append(postalCode);
        }

        sb.append(", ").append(country);

        return sb.toString();
    }

    /**
     * Returns the city and country as a short location string.
     *
     * @return formatted city, country
     */
    public String getCityCountry() {
        return String.format("%s, %s", city, country);
    }

    /**
     * Returns the address formatted for display (multi-line).
     *
     * @return formatted address with line breaks
     */
    public String getDisplayAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress).append("\n");

        if (hasStateProvince()) {
            sb.append(city).append(", ").append(stateProvince);
        } else {
            sb.append(city);
        }

        if (hasPostalCode()) {
            sb.append(" ").append(postalCode);
        }

        sb.append("\n").append(country);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Address address = (Address) o;
        return streetAddress.equals(address.streetAddress)
                && city.equals(address.city)
                && Objects.equals(stateProvince, address.stateProvince)
                && country.equals(address.country)
                && Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetAddress, city, stateProvince, country, postalCode);
    }

    @Override
    public String toString() {
        return String.format("Address{street='%s', city='%s', state='%s', country='%s', postal='%s'}",
                streetAddress, city, stateProvince, country, postalCode);
    }
}
