package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.request.currency.CreateCurrencyRequest;
import com.travelplatform.application.dto.response.common.SuccessResponse;
import com.travelplatform.application.dto.response.currency.CurrencyResponse;
import com.travelplatform.application.service.currency.CurrencyService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing currencies.
 */
@Path("/api/v1/currencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Currencies", description = "Manage currency reference data")
@Authenticated
public class CurrencyController {

    @Inject
    CurrencyService currencyService;

    @GET
    @Operation(summary = "List all currencies")
    public Response listCurrencies() {
        List<CurrencyResponse> responses = currencyService.listAll();
        return Response.ok(new SuccessResponse<>(responses)).build();
    }

    @GET
    @Path("/{code}")
    @Operation(summary = "Get currency by ISO code")
    public Response getByCode(@PathParam("code") String code) {
        CurrencyResponse response = currencyService.getByCode(code);
        return Response.ok(new SuccessResponse<>(response)).build();
    }

    @POST
    @Operation(summary = "Create a new currency")
    public Response createCurrency(@Valid CreateCurrencyRequest request) {
        CurrencyResponse response = currencyService.createCurrency(request);
        return Response.status(Response.Status.CREATED)
                .entity(new SuccessResponse<>(response, "Currency created"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing currency")
    public Response updateCurrency(@PathParam("id") UUID id, @Valid CreateCurrencyRequest request) {
        CurrencyResponse response = currencyService.updateCurrency(id, request);
        return Response.ok(new SuccessResponse<>(response, "Currency updated")).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a currency")
    public Response deleteCurrency(@PathParam("id") UUID id) {
        currencyService.deleteCurrency(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
