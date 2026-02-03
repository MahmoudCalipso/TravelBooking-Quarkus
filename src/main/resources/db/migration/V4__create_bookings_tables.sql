-- ============================================
-- Booking Module Tables
-- ============================================

-- Table: bookings
-- Reservation records for accommodations
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    accommodation_id UUID NOT NULL REFERENCES accommodations(id) ON DELETE CASCADE,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INTEGER NOT NULL,
    number_of_adults INTEGER NOT NULL,
    number_of_children INTEGER DEFAULT 0,
    number_of_infants INTEGER DEFAULT 0,
    total_nights INTEGER NOT NULL,
    base_price_per_night DECIMAL(10,2) NOT NULL,
    total_base_price DECIMAL(10,2) NOT NULL,
    service_fee DECIMAL(10,2) NOT NULL,
    cleaning_fee DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    total_price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW')),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'UNPAID' CHECK (payment_status IN ('UNPAID', 'PAID', 'REFUNDED', 'PARTIALLY_REFUNDED')),
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP,
    cancelled_by UUID REFERENCES users(id),
    special_requests TEXT,
    guest_message_to_host TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    CONSTRAINT chk_bookings_dates CHECK (check_out_date > check_in_date),
    CONSTRAINT chk_bookings_guests CHECK (number_of_guests > 0 AND number_of_adults > 0),
    CONSTRAINT chk_bookings_children CHECK (number_of_children >= 0),
    CONSTRAINT chk_bookings_infants CHECK (number_of_infants >= 0)
);

-- Indexes for bookings
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_accommodation_id ON bookings(accommodation_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(accommodation_id, check_in_date, check_out_date);
CREATE INDEX idx_bookings_created_at ON bookings(created_at DESC);

-- Table: booking_payments
-- Payment transactions for bookings
CREATE TABLE booking_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CARD', 'PAYPAL', 'BANK_TRANSFER', 'CRYPTO')),
    payment_provider VARCHAR(50),
    transaction_id VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED')),
    failure_reason TEXT,
    refund_amount DECIMAL(10,2),
    refund_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    refunded_at TIMESTAMP
);

-- Indexes for booking_payments
CREATE INDEX idx_booking_payments_booking_id ON booking_payments(booking_id);
CREATE INDEX idx_booking_payments_status ON booking_payments(status);
CREATE INDEX idx_booking_payments_transaction_id ON booking_payments(transaction_id);
CREATE INDEX idx_booking_payments_created_at ON booking_payments(created_at DESC);
