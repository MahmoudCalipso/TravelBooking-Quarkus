-- ============================================
-- Initial Data Seeding
-- ============================================

-- Insert default subscription tiers
INSERT INTO subscription_tiers (id, name, description, plan_type, monthly_price, currency, max_accommodations, priority_level, features, is_active, created_at, updated_at) VALUES
(
    gen_random_uuid(),
    'Basic Plan',
    'Standard listing visibility for individual suppliers',
    'BASIC',
    0.00,
    'USD',
    1,
    1,
    '{"features": ["Standard listing visibility", "Basic analytics", "Email support"]}'::jsonb,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'Premium Plan',
    'Priority search placement and featured badges',
    'PREMIUM',
    29.99,
    'USD',
    5,
    10,
    '{"features": ["Priority search placement", "Featured badges", "Advanced analytics", "Priority support", "Promotional reel support"]}'::jsonb,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'Enterprise Plan',
    'Multiple properties with API access',
    'ENTERPRISE',
    99.99,
    'USD',
    50,
    100,
    '{"features": ["Unlimited properties", "API access", "Dedicated account manager", "Custom branding", "Advanced analytics dashboard", "24/7 priority support"]}'::jsonb,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Note: SUPER_ADMIN users should be created manually in production
-- This is a placeholder for development/testing purposes only
-- In production, create SUPER_ADMIN users directly in the database with hashed passwords

-- Insert sample accommodation amenities (for reference)
-- These are commonly used amenities that can be referenced when creating accommodations
-- Note: Amenities are stored per accommodation, not as a separate lookup table

-- Create a function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at on all tables
CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_accommodations_updated_at
    BEFORE UPDATE ON accommodations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_travel_reels_updated_at
    BEFORE UPDATE ON travel_reels
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_events_updated_at
    BEFORE UPDATE ON events
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_travel_programs_updated_at
    BEFORE UPDATE ON travel_programs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_notifications_updated_at
    BEFORE UPDATE ON notifications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_subscription_tiers_updated_at
    BEFORE UPDATE ON subscription_tiers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create function to update accommodation average rating
CREATE OR REPLACE FUNCTION update_accommodation_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE accommodations
    SET average_rating = (
        SELECT COALESCE(AVG(r.overall_rating), 0)
        FROM reviews r
        WHERE r.accommodation_id = NEW.accommodation_id
        AND r.status = 'APPROVED'
    ),
    review_count = (
        SELECT COUNT(*)
        FROM reviews r
        WHERE r.accommodation_id = NEW.accommodation_id
        AND r.status = 'APPROVED'
    )
    WHERE id = NEW.accommodation_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to update accommodation rating when review is approved
CREATE TRIGGER trigger_update_accommodation_rating
    AFTER INSERT OR UPDATE ON reviews
    FOR EACH ROW
    WHEN (NEW.status = 'APPROVED' OR OLD.status IS DISTINCT FROM NEW.status)
    EXECUTE FUNCTION update_accommodation_rating();

-- Create function to update reel engagement counters
CREATE OR REPLACE FUNCTION update_reel_engagement_counters()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE travel_reels
    SET like_count = (
        SELECT COUNT(*)
        FROM reel_engagement
        WHERE reel_id = NEW.reel_id AND engagement_type = 'LIKE'
    ),
    comment_count = (
        SELECT COUNT(*)
        FROM reel_comments
        WHERE reel_id = NEW.reel_id AND status = 'VISIBLE'
    ),
    share_count = (
        SELECT COUNT(*)
        FROM reel_engagement
        WHERE reel_id = NEW.reel_id AND engagement_type = 'SHARE'
    ),
    bookmark_count = (
        SELECT COUNT(*)
        FROM reel_engagement
        WHERE reel_id = NEW.reel_id AND engagement_type = 'BOOKMARK'
    )
    WHERE id = NEW.reel_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to update reel counters
CREATE TRIGGER trigger_update_reel_engagement
    AFTER INSERT OR DELETE ON reel_engagement
    FOR EACH ROW
    EXECUTE FUNCTION update_reel_engagement_counters();

CREATE TRIGGER trigger_update_reel_comments
    AFTER INSERT OR UPDATE ON reel_comments
    FOR EACH ROW
    EXECUTE FUNCTION update_reel_engagement_counters();

-- Create function to update review helpful count
CREATE OR REPLACE FUNCTION update_review_helpful_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE reviews
    SET helpful_count = (
        SELECT COUNT(*)
        FROM review_helpful
        WHERE review_id = NEW.review_id AND is_helpful = TRUE
    )
    WHERE id = NEW.review_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to update review helpful count
CREATE TRIGGER trigger_update_review_helpful
    AFTER INSERT OR UPDATE ON review_helpful
    FOR EACH ROW
    EXECUTE FUNCTION update_review_helpful_count();

-- Create function to update conversation unread counts
CREATE OR REPLACE FUNCTION update_conversation_unread_counts()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE conversations
    SET unread_count_p1 = (
        SELECT COUNT(*)
        FROM direct_messages dm
        WHERE dm.conversation_id = NEW.conversation_id
        AND dm.sender_id = conversations.participant1_id
        AND dm.is_read = FALSE
    ),
    unread_count_p2 = (
        SELECT COUNT(*)
        FROM direct_messages dm
        WHERE dm.conversation_id = NEW.conversation_id
        AND dm.sender_id = conversations.participant2_id
        AND dm.is_read = FALSE
    ),
    last_message_at = (
        SELECT MAX(created_at)
        FROM direct_messages dm
        WHERE dm.conversation_id = NEW.conversation_id
    )
    WHERE id = NEW.conversation_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to update conversation unread counts
CREATE TRIGGER trigger_update_conversation_unread
    AFTER INSERT OR UPDATE ON direct_messages
    FOR EACH ROW
    EXECUTE FUNCTION update_conversation_unread_counts();
