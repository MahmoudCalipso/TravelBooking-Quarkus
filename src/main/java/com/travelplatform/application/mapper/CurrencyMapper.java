package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.currency.CreateCurrencyRequest;
import com.travelplatform.application.dto.response.currency.CurrencyResponse;
import com.travelplatform.domain.model.currency.Currency;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Currency domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface CurrencyMapper {

    default Currency toCurrency(CreateCurrencyRequest request) {
        if (request == null) {
            return null;
        }
        return new Currency(
                request.getCountryName(),
                request.getCurrencyCode(),
                request.getCurrencySymbol());
    }

    default CurrencyResponse toResponse(Currency currency) {
        if (currency == null) {
            return null;
        }
        CurrencyResponse response = new CurrencyResponse();
        response.setId(currency.getId());
        response.setCountryName(currency.getCountryName());
        response.setCurrencyCode(currency.getCurrencyCode());
        response.setCurrencySymbol(currency.getCurrencySymbol());
        response.setCreatedAt(currency.getCreatedAt());
        response.setUpdatedAt(currency.getUpdatedAt());
        return response;
    }

    default List<CurrencyResponse> toResponseList(List<Currency> currencies) {
        if (currencies == null) {
            return null;
        }
        return currencies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
