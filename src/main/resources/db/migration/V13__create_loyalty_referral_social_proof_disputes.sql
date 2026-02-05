-- Loyalty accounts
CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    points INT NOT NULL DEFAULT 0,
    badges TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_loyalty_accounts_user_id ON loyalty_accounts(user_id);

-- Referrals
CREATE TABLE IF NOT EXISTS referrals (
    id UUID PRIMARY KEY,
    referral_code VARCHAR(32) NOT NULL UNIQUE,
    owner_id UUID NOT NULL,
    successful_referrals INT NOT NULL DEFAULT 0,
    earned_credits INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_referrals_owner_id ON referrals(owner_id);

-- Social proof metrics
CREATE TABLE IF NOT EXISTS social_proof (
    accommodation_id UUID PRIMARY KEY,
    current_viewers INT NOT NULL DEFAULT 0,
    recent_bookings INT NOT NULL DEFAULT 0,
    last_booked_seconds BIGINT NOT NULL DEFAULT 0,
    popularity_score INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Disputes
CREATE TABLE IF NOT EXISTS disputes (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    initiator_id UUID NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    resolution TEXT,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_disputes_booking_id ON disputes(booking_id);
CREATE INDEX IF NOT EXISTS idx_disputes_initiator_id ON disputes(initiator_id);

-- Device tokens for push/WebSocket targeting
CREATE TABLE IF NOT EXISTS device_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(256) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_device_tokens_user_id ON device_tokens(user_id);
