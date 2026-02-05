package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.service.supplier.AutomatedMessagingService;
import com.travelplatform.application.service.supplier.PricingCalendarService;
import com.travelplatform.application.service.supplier.SupplierAnalyticsService;
import com.travelplatform.domain.valueobject.DateRange;
import com.travelplatform.domain.valueobject.Money;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Path("/api/v1/suppliers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("SUPPLIER_SUBSCRIBER")
public class SupplierToolsController {

    @Inject
    SupplierAnalyticsService supplierAnalyticsService;

    @Inject
    AutomatedMessagingService automatedMessagingService;

    @Inject
    PricingCalendarService pricingCalendarService;

    @GET
    @Path("/{supplierId}/analytics")
    public Response getDashboard(@PathParam("supplierId") UUID supplierId,
                                 @QueryParam("startDate") String startDate,
                                 @QueryParam("endDate") String endDate) {
        DateRange range = null;
        if (startDate != null && endDate != null) {
            range = new DateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        }
        return Response.ok(supplierAnalyticsService.getDashboard(supplierId, range)).build();
    }

    @POST
    @Path("/{accommodationId}/pricing-calendar")
    public Response getPricingCalendar(@PathParam("accommodationId") UUID accommodationId,
                                       @QueryParam("yearMonth") String yearMonth,
                                       @QueryParam("basePrice") BigDecimal basePrice,
                                       @QueryParam("currency") String currency) {
        YearMonth ym = yearMonth != null ? YearMonth.parse(yearMonth) : YearMonth.now();
        Money money = new Money(basePrice != null ? basePrice : BigDecimal.ZERO, currency != null ? currency : "USD");
        return Response.ok(pricingCalendarService.getCalendar(accommodationId, ym, money)).build();
    }
}
