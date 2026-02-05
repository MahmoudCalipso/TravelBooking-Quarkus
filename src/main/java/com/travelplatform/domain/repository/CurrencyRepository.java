package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.currency.Currency;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository contract for Currency aggregate.
 */
public interface CurrencyRepository {

    Currency save(Currency currency);

    Currency update(Currency currency);

    void deleteById(UUID id);

    Optional<Currency> findById(UUID id);

    Optional<Currency> findByCode(String currencyCode);

    List<Currency> findAll();

    List<Currency> findByCountryName(String countryName);

    boolean existsByCode(String currencyCode);
}
