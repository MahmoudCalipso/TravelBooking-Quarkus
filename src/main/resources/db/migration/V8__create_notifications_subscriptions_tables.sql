-- ============================================
-- Notifications & Subscriptions Module Tables
-- ============================================

-- Table: notifications
-- System notifications to users
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL CHECK (type IN (
        'BOOKING_CONFIRMED', 'BOOKING_CANCELLED', 'PAYMENT_RECEIVED',
        'REEL_APPROVED', 'REEL_LIKED', 'NEW_COMMENT',
        'NEW_REVIEW', 'NEW_MESSAGE', 'EVENT_REMINDER'
    )),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    related_entity_type VARCHAR(50) CHECK (related_entity_type IN ('BOOKING', 'REEL', 'REVIEW', 'MESSAGE', 'EVENT')),
    related_entity_id UUID,
    action_url TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

-- Indexes for notifications
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_is_read ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);

-- Table: premium_visibility_plans
-- Premium placement for suppliers
CREATE TABLE premium_visibility_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_type VARCHAR(50) NOT NULL CHECK (plan_type IN ('BASIC', 'PREMIUM', 'ENTERPRISE')),
    accommodation_id UUID REFERENCES accommodations(id) ON DELETE CASCADE,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    priority_level INTEGER NOT NULL,
    price_paid DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'CANCELLED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_premium_visibility_plans_dates CHECK (end_date > start_date),
    CONSTRAINT chk_premium_visibility_plans_priority CHECK (priority_level > 0)
);

-- Indexes for premium_visibility_plans
CREATE INDEX idx_premium_visibility_plans_supplier_id ON premium_visibility_plans(supplier_id);
CREATE INDEX idx_premium_visibility_plans_accommodation_id ON premium_visibility_plans(accommodation_id);
CREATE INDEX idx_premium_visibility_plans_status ON premium_visibility_plans(status);
CREATE INDEX idx_premium_visibility_plans_dates ON premium_visibility_plans(start_date, end_date);

-- Table: subscription_tiers
-- Subscription tier definitions
CREATE TABLE subscription_tiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    plan_type VARCHAR(50) NOT NULL CHECK (plan_type IN ('BASIC', 'PREMIUM', 'ENTERPRISE')),
    monthly_price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    max_accommodations INTEGER,
    priority_level INTEGER NOT NULL,
    features JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for subscription_tiers
CREATE INDEX idx_subscription_tiers_plan_type ON subscription_tiers(plan_type);
CREATE INDEX idx_subscription_tiers_is_active ON subscription_tiers(is_active);
