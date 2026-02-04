package com.travelplatform.domain.repository;

import com.travelplatform.domain.model.booking.BookingFeeConfig;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for booking fee configuration.
 */
public interface BookingFeeConfigRepository {

    BookingFeeConfig save(BookingFeeConfig config);

    Optional<BookingFeeConfig> findActive();

    Optional<BookingFeeConfig> findById(UUID id);

    void deactivateAll();
}
