package com.travelplatform.application.service.supplier;

import com.travelplatform.domain.enums.PaymentStatus;
import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.repository.BookingRepository;
import com.travelplatform.domain.valueobject.DateRange;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Supplier-facing analytics dashboard with lightweight calculations.
 * This implementation uses placeholder calculations; plug in repositories later.
 */
@ApplicationScoped
public class SupplierAnalyticsService {

    @Inject
    BookingRepository bookingRepository;

    public SupplierDashboard getDashboard(UUID supplierId, DateRange dateRange) {
        List<Booking> bookings = bookingRepository.findBySupplierIdPaginated(supplierId, 0, 1000);
        BigDecimal revenue = calculateRevenue(bookings, dateRange);
        BigDecimal averageBookingValue = calculateAverageBookingValue(bookings, dateRange);
        double occupancyRate = calculateOccupancyRate(bookings);
        double conversionRate = calculateConversionRate(bookings);

        return SupplierDashboard.builder()
                .totalRevenue(revenue)
                .revenueGrowth(0.08)
                .averageBookingValue(averageBookingValue)
                .occupancyRate(occupancyRate)
                .averageRating(4.6)
                .responseTimeMinutes(42)
                .viewCount(1520)
                .clickThroughRate(0.17)
                .conversionRate(conversionRate)
                .vsMarketAverage(0.05)
                .vsLastPeriod(-0.02)
                .suggestions(generateSuggestions(supplierId))
                .build();
    }

    public List<Suggestion> generateSuggestions(UUID supplierId) {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Improve response time",
                "Hosts who respond within 1 hour see 40% more bookings."));
        suggestions.add(new Suggestion("Add more photos",
                "Listings with 10+ photos get 3x more views."));
        suggestions.add(new Suggestion("Price adjustment",
                "Your price is 15% higher than similar properties nearby."));
        return suggestions;
    }

    /**
     * Simple DTOs to avoid leaking internals.
     */
    public record Suggestion(String title, String detail) {
    }

    public static class SupplierDashboard {
        private BigDecimal totalRevenue;
        private double revenueGrowth;
        private BigDecimal averageBookingValue;
        private double occupancyRate;
        private double averageRating;
        private int responseTimeMinutes;
        private int viewCount;
        private double clickThroughRate;
        private double conversionRate;
        private double vsMarketAverage;
        private double vsLastPeriod;
        private List<Suggestion> suggestions;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final SupplierDashboard d = new SupplierDashboard();

            public Builder totalRevenue(BigDecimal v) { d.totalRevenue = v; return this; }
            public Builder revenueGrowth(double v) { d.revenueGrowth = v; return this; }
            public Builder averageBookingValue(BigDecimal v) { d.averageBookingValue = v; return this; }
            public Builder occupancyRate(double v) { d.occupancyRate = v; return this; }
            public Builder averageRating(double v) { d.averageRating = v; return this; }
            public Builder responseTimeMinutes(int v) { d.responseTimeMinutes = v; return this; }
            public Builder viewCount(int v) { d.viewCount = v; return this; }
            public Builder clickThroughRate(double v) { d.clickThroughRate = v; return this; }
            public Builder conversionRate(double v) { d.conversionRate = v; return this; }
            public Builder vsMarketAverage(double v) { d.vsMarketAverage = v; return this; }
            public Builder vsLastPeriod(double v) { d.vsLastPeriod = v; return this; }
            public Builder suggestions(List<Suggestion> v) { d.suggestions = v; return this; }
            public SupplierDashboard build() { return d; }
        }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public double getRevenueGrowth() { return revenueGrowth; }
        public BigDecimal getAverageBookingValue() { return averageBookingValue; }
        public double getOccupancyRate() { return occupancyRate; }
        public double getAverageRating() { return averageRating; }
        public int getResponseTimeMinutes() { return responseTimeMinutes; }
        public int getViewCount() { return viewCount; }
        public double getClickThroughRate() { return clickThroughRate; }
        public double getConversionRate() { return conversionRate; }
        public double getVsMarketAverage() { return vsMarketAverage; }
        public double getVsLastPeriod() { return vsLastPeriod; }
        public List<Suggestion> getSuggestions() { return suggestions; }
    }

    /**
     * Utility to compute growth rate safely.
     */
    public double calculateGrowth(double current, double previous) {
        if (previous <= 0) {
            return current > 0 ? 1.0 : 0.0;
        }
        return BigDecimal.valueOf((current - previous) / previous)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private BigDecimal calculateRevenue(List<Booking> bookings, DateRange range) {
        return bookings.stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.COMPLETED)
                .filter(b -> isWithinRange(range, b.getCreatedAt().toLocalDate()))
                .map(b -> b.getTotalPrice().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverageBookingValue(List<Booking> bookings, DateRange range) {
        List<BigDecimal> amounts = bookings.stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.COMPLETED)
                .filter(b -> isWithinRange(range, b.getCreatedAt().toLocalDate()))
                .map(b -> b.getTotalPrice().getAmount())
                .toList();
        if (amounts.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(amounts.size()), 2, RoundingMode.HALF_UP);
    }

    private double calculateOccupancyRate(List<Booking> bookings) {
        long confirmed = bookings.stream().filter(Booking::isConfirmed).count();
        return bookings.isEmpty() ? 0.0 : ((double) confirmed / bookings.size());
    }

    private double calculateConversionRate(List<Booking> bookings) {
        long paid = bookings.stream().filter(b -> b.getPaymentStatus() == PaymentStatus.COMPLETED).count();
        return bookings.isEmpty() ? 0.0 : ((double) paid / bookings.size());
    }

    private boolean isWithinRange(DateRange range, LocalDate date) {
        if (range == null || date == null) {
            return true;
        }
        return (date.isEqual(range.getStartDate()) || date.isAfter(range.getStartDate()))
                && (date.isEqual(range.getEndDate()) || date.isBefore(range.getEndDate().plusDays(1)));
    }
}
