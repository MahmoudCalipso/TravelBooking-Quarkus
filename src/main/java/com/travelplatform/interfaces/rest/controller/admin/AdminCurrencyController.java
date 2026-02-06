package com.travelplatform.interfaces.rest.controller.admin;

import com.travelplatform.application.dto.response.common.BaseResponse;
import com.travelplatform.application.service.AuditService;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.model.currency.Currency;
import com.travelplatform.domain.repository.CurrencyRepository;
import com.travelplatform.infrastructure.security.authorization.Authorized;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Admin controller for currency management.
 * All endpoints require SUPER_ADMIN role.
 */
@Path("/api/v1/admin/currencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin - Currency Management", description = "SUPER_ADMIN endpoints for managing currencies and exchange rates")
@Authorized(roles = { UserRole.SUPER_ADMIN })
public class AdminCurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(AdminCurrencyController.class);

    @Inject
    CurrencyRepository currencyRepository;

    @Inject
    AuditService auditService;

    /**
     * List all currencies.
     */
    @GET
    @Operation(summary = "List all currencies", description = "Get all supported currencies")
    public BaseResponse<List<Currency>> listCurrencies() {
        logger.info("Admin listing all currencies");
        List<Currency> currencies = currencyRepository.listAll();
        return BaseResponse.success(currencies);
    }

    /**
     * Create new currency.
     */
    @POST
    @Transactional
    @Operation(summary = "Create currency", description = "Add new currency support")
    public BaseResponse<Currency> createCurrency(CreateCurrencyRequest request) {
        logger.info("Admin creating currency: code={}", request.code);

        // Check if currency already exists
        if (currencyRepository.findByCode(request.code).isPresent()) {
            return BaseResponse.error("Currency already exists");
        }

        Currency currency = new Currency();
        currency.setCode(request.code);
        currency.setName(request.name);
        currency.setSymbol(request.symbol);
        currency.setExchangeRate(request.exchangeRate);
        currency.setDecimalPlaces(request.decimalPlaces != null ? request.decimalPlaces : 2);
        currency.setActive(request.active != null ? request.active : true);

        currencyRepository.persist(currency);

        auditService.logAction("CURRENCY_CREATED", "Currency", currency.getId(),
                Map.of("code", request.code, "exchangeRate", request.exchangeRate.toString()));

        return BaseResponse.success(currency, "Currency created successfully");
    }

    /**
     * Update currency exchange rate.
     */
    @PUT
    @Path("/{code}")
    @Transactional
    @Operation(summary = "Update exchange rate", description = "Update currency exchange rate to base currency")
    public BaseResponse<Currency> updateExchangeRate(
            @PathParam("code") String code,
            UpdateExchangeRateRequest request) {

        logger.info("Admin updating exchange rate: code={}, newRate={}", code, request.exchangeRate);

        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Currency not found"));

        BigDecimal oldRate = currency.getExchangeRate();
        currency.setExchangeRate(request.exchangeRate);
        currency.setLastUpdated(Instant.now());

        currencyRepository.persist(currency);

        auditService.logAction("CURRENCY_RATE_UPDATED", "Currency", currency.getId(),
                Map.of("code", code, "oldRate", oldRate.toString(), "newRate", request.exchangeRate.toString()));

        return BaseResponse.success(currency, "Exchange rate updated successfully");
    }

    /**
     * Disable currency.
     */
    @POST
    @Path("/{code}/disable")
    @Transactional
    @Operation(summary = "Disable currency", description = "Remove currency from circulation")
    public BaseResponse<Void> disableCurrency(@PathParam("code") String code) {
        logger.info("Admin disabling currency: code={}", code);

        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Currency not found"));

        currency.setActive(false);
        currencyRepository.persist(currency);

        auditService.logAction("CURRENCY_DISABLED", "Currency", currency.getId(),
                Map.of("code", code));

        return BaseResponse.success("Currency disabled successfully");
    }

    /**
     * Set base currency.
     */
    @POST
    @Path("/base")
    @Transactional
    @Operation(summary = "Set base currency", description = "Set the platform's base currency for exchange calculations")
    public BaseResponse<Void> setBaseCurrency(SetBaseCurrencyRequest request) {
        logger.info("Admin setting base currency: code={}", request.code);

        Currency currency = currencyRepository.findByCode(request.code)
                .orElseThrow(() -> new NotFoundException("Currency not found"));

        // Set all currencies to non-base
        currencyRepository.listAll().forEach(c -> {
            c.setIsBase(false);
            currencyRepository.persist(c);
        });

        // Set selected currency as base
        currency.setIsBase(true);
        currency.setExchangeRate(BigDecimal.ONE); // Base currency always has rate 1.0
        currencyRepository.persist(currency);

        auditService.logAction("BASE_CURRENCY_SET", "Currency", currency.getId(),
                Map.of("code", request.code));

        return BaseResponse.success("Base currency set successfully");
    }

    // Request DTOs

    public static class CreateCurrencyRequest {
        public String code;
        public String name;
        public String symbol;
        public BigDecimal exchangeRate;
        public Integer decimalPlaces;
        public Boolean active;
    }

    public static class UpdateExchangeRateRequest {
        public BigDecimal exchangeRate;
    }

    public static class SetBaseCurrencyRequest {
        public String code;
    }
}