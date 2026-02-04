package com.travelplatform.application.service.booking;

import com.travelplatform.application.dto.request.booking.UpdateBookingFeeConfigRequest;
import com.travelplatform.application.dto.response.booking.BookingFeeConfigResponse;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import com.travelplatform.domain.model.booking.BookingFeeConfig;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.repository.BookingFeeConfigRepository;
import com.travelplatform.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service for booking fee configuration management.
 */
@ApplicationScoped
public class BookingFeeConfigService {

    @Inject
    BookingFeeConfigRepository bookingFeeConfigRepository;

    @Inject
    UserRepository userRepository;

    @ConfigProperty(name = "pricing.service-fee-percentage", defaultValue = "10.0")
    BigDecimal defaultServiceFeePercentage;

    @ConfigProperty(name = "pricing.service-fee-minimum", defaultValue = "5.00")
    BigDecimal defaultServiceFeeMinimum;

    @ConfigProperty(name = "pricing.service-fee-maximum", defaultValue = "100.00")
    BigDecimal defaultServiceFeeMaximum;

    @ConfigProperty(name = "pricing.cleaning-fee-percentage", defaultValue = "10.0")
    BigDecimal defaultCleaningFeePercentage;

    @ConfigProperty(name = "pricing.tax-rate", defaultValue = "0.08")
    BigDecimal defaultTaxRate;

    @Transactional
    public BookingFeeConfig getActiveConfig() {
        return bookingFeeConfigRepository.findActive()
                .orElseGet(this::createDefaultConfig);
    }

    @Transactional
    public BookingFeeConfigResponse getActiveConfigResponse() {
        return toResponse(getActiveConfig());
    }

    @Transactional
    public BookingFeeConfigResponse updateConfig(UUID adminId, UpdateBookingFeeConfigRequest request) {
        verifySuperAdmin(adminId);

        BookingFeeConfig config = bookingFeeConfigRepository.findActive()
                .orElseGet(this::createDefaultConfig);

        BigDecimal serviceFeePercentage = request.getServiceFeePercentage() != null
                ? request.getServiceFeePercentage()
                : config.getServiceFeePercentage();
        BigDecimal serviceFeeMinimum = request.getServiceFeeMinimum() != null
                ? request.getServiceFeeMinimum()
                : config.getServiceFeeMinimum();
        BigDecimal serviceFeeMaximum = request.getServiceFeeMaximum() != null
                ? request.getServiceFeeMaximum()
                : config.getServiceFeeMaximum();
        BigDecimal cleaningFeePercentage = request.getCleaningFeePercentage() != null
                ? request.getCleaningFeePercentage()
                : config.getCleaningFeePercentage();
        BigDecimal taxRate = request.getTaxRate() != null
                ? request.getTaxRate()
                : config.getTaxRate();

        config.update(serviceFeePercentage, serviceFeeMinimum, serviceFeeMaximum,
                cleaningFeePercentage, taxRate);
        config.setActive(true);

        bookingFeeConfigRepository.save(config);
        return toResponse(config);
    }

    private BookingFeeConfig createDefaultConfig() {
        BookingFeeConfig config = new BookingFeeConfig(
                defaultServiceFeePercentage,
                defaultServiceFeeMinimum,
                defaultServiceFeeMaximum,
                defaultCleaningFeePercentage,
                defaultTaxRate);
        return bookingFeeConfigRepository.save(config);
    }

    private void verifySuperAdmin(UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (admin.getRole() != UserRole.SUPER_ADMIN) {
            throw new IllegalArgumentException("Only SUPER_ADMIN can perform this action");
        }
        if (admin.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Admin account is not active");
        }
    }

    private BookingFeeConfigResponse toResponse(BookingFeeConfig config) {
        BookingFeeConfigResponse response = new BookingFeeConfigResponse();
        response.setId(config.getId());
        response.setServiceFeePercentage(config.getServiceFeePercentage());
        response.setServiceFeeMinimum(config.getServiceFeeMinimum());
        response.setServiceFeeMaximum(config.getServiceFeeMaximum());
        response.setCleaningFeePercentage(config.getCleaningFeePercentage());
        response.setTaxRate(config.getTaxRate());
        response.setActive(config.isActive());
        response.setCreatedAt(config.getCreatedAt());
        response.setUpdatedAt(config.getUpdatedAt());
        return response;
    }
}
