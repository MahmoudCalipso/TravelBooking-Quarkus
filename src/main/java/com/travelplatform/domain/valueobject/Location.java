package com.travelplatform.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a geographical location with latitude and longitude coordinates.
 * Immutable object - once created, cannot be modified.
 */
public class Location {
    private final double latitude;
    private final double longitude;

    /**
     * Creates a new Location with the specified coordinates.
     *
     * @param latitude  the latitude coordinate (-90 to 90)
     * @param longitude the longitude coordinate (-180 to 180)
     * @throws IllegalArgumentException if coordinates are out of valid range
     */
    public Location(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Calculates the distance in kilometers between this location and another location
     * using the Haversine formula.
     *
     * @param other the other location
     * @return distance in kilometers
     */
    public double distanceTo(Location other) {
        if (other == null) {
            throw new IllegalArgumentException("Other location cannot be null");
        }

        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0
                && Double.compare(location.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("Location{latitude=%.8f, longitude=%.8f}", latitude, longitude);
    }
}
