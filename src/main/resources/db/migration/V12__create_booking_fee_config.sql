-- ============================================
-- Booking Fee Configuration
-- ============================================

CREATE TABLE booking_fee_config (
    id UUID PRIMARY KEY,
    service_fee_percentage DECIMAL(5,2) NOT NULL,
    service_fee_minimum DECIMAL(10,2),
    service_fee_maximum DECIMAL(10,2),
    cleaning_fee_percentage DECIMAL(5,2) NOT NULL,
    tax_rate DECIMAL(5,4) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_booking_fee_config_active ON booking_fee_config(is_active);

-- Seed default configuration
INSERT INTO booking_fee_config (
    id,
    service_fee_percentage,
    service_fee_minimum,
    service_fee_maximum,
    cleaning_fee_percentage,
    tax_rate,
    is_active,
    created_at,
    updated_at
) VALUES (
    gen_random_uuid(),
    10.00,
    5.00,
    100.00,
    10.00,
    0.0800,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
