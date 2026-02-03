package com.travelplatform.domain.valueobject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value object representing a range of dates.
 * Immutable object - once created, cannot be modified.
 */
public class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates a new DateRange with the specified start and end dates.
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @throws IllegalArgumentException if dates are null or start is after end
     */
    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Calculates the number of nights in this date range.
     *
     * @return number of nights
     */
    public long getNights() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static int calculateNights(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calculates the number of days in this date range (inclusive).
     *
     * @return number of days
     */
    public long getDays() {
        return getNights() + 1;
    }

    /**
     * Checks if this date range overlaps with another date range.
     *
     * @param other the other date range
     * @return true if the ranges overlap
     */
    public boolean overlapsWith(DateRange other) {
        if (other == null) {
            return false;
        }
        return !this.endDate.isBefore(other.startDate) && !this.startDate.isAfter(other.endDate);
    }

    /**
     * Checks if this date range contains a specific date.
     *
     * @param date the date to check
     * @return true if the date is within the range (inclusive)
     */
    public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Checks if this date range is completely before another date range.
     *
     * @param other the other date range
     * @return true if this range ends before the other starts
     */
    public boolean isBefore(DateRange other) {
        if (other == null) {
            return false;
        }
        return this.endDate.isBefore(other.startDate);
    }

    /**
     * Checks if this date range is completely after another date range.
     *
     * @param other the other date range
     * @return true if this range starts after the other ends
     */
    public boolean isAfter(DateRange other) {
        if (other == null) {
            return false;
        }
        return this.startDate.isAfter(other.endDate);
    }

    /**
     * Creates a new DateRange that is the intersection of this and another range.
     *
     * @param other the other date range
     * @return a new DateRange representing the intersection, or null if no overlap
     */
    public DateRange intersection(DateRange other) {
        if (other == null || !overlapsWith(other)) {
            return null;
        }
        LocalDate newStart = this.startDate.isAfter(other.startDate) ? this.startDate : other.startDate;
        LocalDate newEnd = this.endDate.isBefore(other.endDate) ? this.endDate : other.endDate;
        return new DateRange(newStart, newEnd);
    }

    /**
     * Checks if this date range is in the past.
     *
     * @return true if the end date is before today
     */
    public boolean isInPast() {
        return endDate.isBefore(LocalDate.now());
    }

    /**
     * Checks if this date range is in the future.
     *
     * @return true if the start date is after today
     */
    public boolean isInFuture() {
        return startDate.isAfter(LocalDate.now());
    }

    /**
     * Checks if this date range includes today.
     *
     * @return true if today is within the range
     */
    public boolean includesToday() {
        return contains(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DateRange dateRange = (DateRange) o;
        return startDate.equals(dateRange.startDate) && endDate.equals(dateRange.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return String.format("DateRange{from=%s, to=%s}", startDate, endDate);
    }
}
