package com.travelplatform.application.service.supplier;

import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pricing calendar service for supplier bulk updates and view.
 */
@ApplicationScoped
public class PricingCalendarService {

    public PricingCalendar getCalendar(UUID accommodationId, YearMonth month, Money basePrice) {
        List<PricingDay> days = new ArrayList<>();
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            days.add(new PricingDay(date, basePrice, false, null));
        }
        return new PricingCalendar(accommodationId, month, days);
    }

    public List<PricingDay> bulkUpdatePricing(UUID accommodationId, DateRange range, Money newPrice) {
        List<PricingDay> updated = new ArrayList<>();
        LocalDate date = range.getStartDate();
        while (!date.isAfter(range.getEndDate())) {
            updated.add(new PricingDay(date, newPrice, false, "BULK_UPDATE"));
            date = date.plusDays(1);
        }
        return updated;
    }

    public record PricingCalendar(UUID accommodationId, YearMonth month, List<PricingDay> days) {
    }

    public record PricingDay(LocalDate date, Money price, boolean booked, String note) {
    }
}
