-- ============================================
-- Analytics & Audit Module Tables
-- ============================================

-- Table: accommodation_analytics
-- Daily metrics for accommodations
CREATE TABLE accommodation_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    accommodation_id UUID NOT NULL REFERENCES accommodations(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    view_count INTEGER DEFAULT 0,
    detail_view_count INTEGER DEFAULT 0,
    favorite_count INTEGER DEFAULT 0,
    booking_inquiries INTEGER DEFAULT 0,
    booking_conversions INTEGER DEFAULT 0,
    revenue DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_accommodation_analytics UNIQUE (accommodation_id, date)
);

-- Indexes for accommodation_analytics
CREATE INDEX idx_accommodation_analytics_accommodation_id ON accommodation_analytics(accommodation_id);
CREATE INDEX idx_accommodation_analytics_date ON accommodation_analytics(date);

-- Table: reel_analytics
-- Daily metrics for reels
CREATE TABLE reel_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reel_id UUID NOT NULL REFERENCES travel_reels(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    views BIGINT DEFAULT 0,
    unique_views BIGINT DEFAULT 0,
    likes INTEGER DEFAULT 0,
    shares INTEGER DEFAULT 0,
    comments INTEGER DEFAULT 0,
    average_watch_time INTEGER,
    completion_rate DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reel_analytics UNIQUE (reel_id, date)
);

-- Indexes for reel_analytics
CREATE INDEX idx_reel_analytics_reel_id ON reel_analytics(reel_id);
CREATE INDEX idx_reel_analytics_date ON reel_analytics(date);

-- Table: audit_logs
-- Track all sensitive operations
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    ip_address VARCHAR(50),
    user_agent TEXT,
    changes JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for audit_logs
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);

-- Table: user_sessions
-- Active JWT sessions
CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    ip_address VARCHAR(50),
    user_agent TEXT,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP
);

-- Indexes for user_sessions
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_token_hash ON user_sessions(token_hash);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX idx_user_sessions_last_activity_at ON user_sessions(last_activity_at DESC);
