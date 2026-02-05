package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.service.global.CurrencyConversionService;
import com.travelplatform.application.service.global.LocalizationService;
import com.travelplatform.application.service.global.LocalPaymentService;
import com.travelplatform.application.service.global.LocalPaymentService.PaymentRequest;
import com.travelplatform.domain.valueobject.Money;
import jakarta.inject.Inject;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.Locale;

@Path("/api/v1/global")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class GlobalizationController {

    @Inject
    LocalizationService localizationService;

    @Inject
    CurrencyConversionService currencyConversionService;

    @Inject
    LocalPaymentService localPaymentService;

    @POST
    @Path("/translate")
    public Response translate(@QueryParam("key") String key, @QueryParam("locale") String locale) {
        Locale loc = locale != null ? Locale.forLanguageTag(locale) : Locale.ENGLISH;
        return Response.ok(localizationService.translate(key, loc)).build();
    }

    @POST
    @Path("/currency/convert")
    public Response convertCurrency(@QueryParam("amount") BigDecimal amount,
                                    @QueryParam("from") String from,
                                    @QueryParam("to") String to) {
        Money money = new Money(amount, from);
        return Response.ok(currencyConversionService.convertCurrency(money, to)).build();
    }

    @POST
    @Path("/payments/local")
    public Response processLocalPayment(PaymentRequest request) {
        return Response.ok(localPaymentService.processLocalPayment(request)).build();
    }
}
