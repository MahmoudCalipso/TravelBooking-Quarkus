-- ============================================
-- User Module Tables
-- ============================================

-- Table: users
-- Primary table for all platform users regardless of role
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('SUPER_ADMIN', 'TRAVELER', 'SUPPLIER_SUBSCRIBER', 'ASSOCIATION_MANAGER')),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Indexes for users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role_status ON users(role, status);

-- Table: user_profiles
-- Extended profile information for users
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255),
    photo_url TEXT,
    birth_date DATE,
    gender VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY')),
    bio TEXT,
    location VARCHAR(255),
    phone_number VARCHAR(50),
    driving_license_category VARCHAR(10) CHECK (driving_license_category IN ('NONE', 'A', 'B', 'C', 'D', 'E', 'INTERNATIONAL')),
    occupation VARCHAR(100) CHECK (occupation IN ('WORKER', 'STUDENT', 'RETIRED', 'SELF_EMPLOYED', 'UNEMPLOYED', 'FREELANCER', 'ENTREPRENEUR', 'OTHER')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_profiles_user_id UNIQUE (user_id)
);

-- Index for user_profiles
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);

-- Table: user_preferences
-- User travel preferences and notification settings
CREATE TABLE user_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    preferred_destinations TEXT[],
    budget_range VARCHAR(50) CHECK (budget_range IN ('BUDGET', 'MODERATE', 'LUXURY')),
    travel_style VARCHAR(50) CHECK (travel_style IN ('ADVENTURE', 'CULTURAL', 'RELAXATION', 'BUSINESS')),
    interests TEXT[],
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    notification_types JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_preferences_user_id UNIQUE (user_id)
);

-- Index for user_preferences
CREATE INDEX idx_user_preferences_user_id ON user_preferences(user_id);

-- Table: user_follows
-- Social following relationships between travelers
CREATE TABLE user_follows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_follows_follower_following UNIQUE (follower_id, following_id),
    CONSTRAINT chk_user_follows_not_self CHECK (follower_id != following_id)
);

-- Indexes for user_follows
CREATE INDEX idx_user_follows_follower_id ON user_follows(follower_id);
CREATE INDEX idx_user_follows_following_id ON user_follows(following_id);
