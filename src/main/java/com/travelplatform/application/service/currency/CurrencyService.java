package com.travelplatform.application.service.currency;

import com.travelplatform.application.dto.request.currency.CreateCurrencyRequest;
import com.travelplatform.application.dto.response.currency.CurrencyResponse;
import com.travelplatform.application.mapper.CurrencyMapper;
import com.travelplatform.domain.model.currency.Currency;
import com.travelplatform.domain.repository.CurrencyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Application service for managing currencies.
 */
@ApplicationScoped
public class CurrencyService {

    @Inject
    CurrencyRepository currencyRepository;

    @Inject
    CurrencyMapper currencyMapper;

    public CurrencyResponse createCurrency(CreateCurrencyRequest request) {
        Currency currency = currencyMapper.toCurrency(normalize(request));
        if (currencyRepository.existsByCode(currency.getCurrencyCode())) {
            throw new WebApplicationException("Currency code already exists", Response.Status.CONFLICT);
        }
        Currency saved = currencyRepository.save(currency);
        return currencyMapper.toResponse(saved);
    }

    public CurrencyResponse updateCurrency(UUID currencyId, CreateCurrencyRequest request) {
        Currency existing = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new NotFoundException("Currency not found"));
        CreateCurrencyRequest normalized = normalize(request);

        existing.setCountryName(normalized.getCountryName());
        existing.setCurrencyCode(normalized.getCurrencyCode());
        existing.setCurrencySymbol(normalized.getCurrencySymbol());

        Currency updated = currencyRepository.update(existing);
        return currencyMapper.toResponse(updated);
    }

    public List<CurrencyResponse> listAll() {
        return currencyMapper.toResponseList(currencyRepository.findAll());
    }

    public CurrencyResponse getByCode(String code) {
        return currencyRepository.findByCode(code.toUpperCase(Locale.ROOT))
                .map(currencyMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Currency not found"));
    }

    public void deleteCurrency(UUID id) {
        if (currencyRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Currency not found");
        }
        currencyRepository.deleteById(id);
    }

    private CreateCurrencyRequest normalize(CreateCurrencyRequest request) {
        CreateCurrencyRequest normalized = new CreateCurrencyRequest();
        normalized.setCountryName(request.getCountryName() != null ? request.getCountryName().trim() : null);
        normalized.setCurrencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode().trim().toUpperCase(Locale.ROOT) : null);
        normalized.setCurrencySymbol(request.getCurrencySymbol() != null ? request.getCurrencySymbol().trim() : null);
        return normalized;
    }
}
