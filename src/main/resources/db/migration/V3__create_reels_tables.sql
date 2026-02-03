-- ============================================
-- Travel Reels Module Tables (Short Video Content)
-- ============================================

-- Table: travel_reels
-- Short-form video content created by travelers and suppliers
CREATE TABLE travel_reels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    creator_type VARCHAR(50) NOT NULL CHECK (creator_type IN ('TRAVELER', 'SUPPLIER_SUBSCRIBER')),
    video_url TEXT NOT NULL,
    thumbnail_url TEXT NOT NULL,
    title VARCHAR(100),
    description VARCHAR(500),
    duration INTEGER NOT NULL CHECK (duration >= 1 AND duration <= 90),
    location_latitude DECIMAL(10,8),
    location_longitude DECIMAL(11,8),
    location_name VARCHAR(255),
    tags TEXT[],
    related_entity_type VARCHAR(50) CHECK (related_entity_type IN ('ACCOMMODATION', 'EVENT', 'DESTINATION')),
    related_entity_id UUID,
    visibility VARCHAR(50) NOT NULL DEFAULT 'PUBLIC' CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE')),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED')),
    is_promotional BOOLEAN DEFAULT FALSE,
    view_count BIGINT DEFAULT 0,
    unique_view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    comment_count INTEGER DEFAULT 0,
    share_count BIGINT DEFAULT 0,
    bookmark_count BIGINT DEFAULT 0,
    average_watch_time INTEGER,
    completion_rate DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by UUID REFERENCES users(id)
);

-- Indexes for travel_reels
CREATE INDEX idx_travel_reels_creator_id ON travel_reels(creator_id);
CREATE INDEX idx_travel_reels_status ON travel_reels(status);
CREATE INDEX idx_travel_reels_feed ON travel_reels(status, visibility, created_at DESC);
CREATE INDEX idx_travel_reels_location ON travel_reels USING GIST (location_latitude, location_longitude);
CREATE INDEX idx_travel_reels_related_entity ON travel_reels(related_entity_type, related_entity_id);
CREATE INDEX idx_travel_reels_created_at ON travel_reels(created_at DESC);
CREATE INDEX idx_travel_reels_tags ON travel_reels USING GIN (tags);

-- Table: reel_engagement
-- Track user interactions with reels
CREATE TABLE reel_engagement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reel_id UUID NOT NULL REFERENCES travel_reels(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    engagement_type VARCHAR(50) NOT NULL CHECK (engagement_type IN ('VIEW', 'LIKE', 'SHARE', 'BOOKMARK')),
    watch_duration INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reel_engagement UNIQUE (reel_id, user_id, engagement_type)
);

-- Indexes for reel_engagement
CREATE INDEX idx_reel_engagement_reel_id ON reel_engagement(reel_id);
CREATE INDEX idx_reel_engagement_user_id ON reel_engagement(user_id);
CREATE INDEX idx_reel_engagement_type ON reel_engagement(engagement_type);

-- Table: reel_comments
-- User comments on reels
CREATE TABLE reel_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reel_id UUID NOT NULL REFERENCES travel_reels(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    parent_comment_id UUID REFERENCES reel_comments(id) ON DELETE CASCADE,
    content VARCHAR(300) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'VISIBLE' CHECK (status IN ('VISIBLE', 'HIDDEN', 'FLAGGED')),
    like_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for reel_comments
CREATE INDEX idx_reel_comments_reel_id ON reel_comments(reel_id);
CREATE INDEX idx_reel_comments_parent_id ON reel_comments(parent_comment_id);
CREATE INDEX idx_reel_comments_created_at ON reel_comments(reel_id, created_at);
CREATE INDEX idx_reel_comments_user_id ON reel_comments(user_id);

-- Table: reel_reports
-- User reports for inappropriate content
CREATE TABLE reel_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reel_id UUID NOT NULL REFERENCES travel_reels(id) ON DELETE CASCADE,
    reported_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reason VARCHAR(50) NOT NULL CHECK (reason IN ('SPAM', 'INAPPROPRIATE', 'MISLEADING', 'COPYRIGHT', 'VIOLENCE', 'HATE_SPEECH', 'HARASSMENT', 'OTHER')),
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'REVIEWED', 'DISMISSED', 'ACTION_TAKEN')),
    reviewed_by UUID REFERENCES users(id),
    admin_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP
);

-- Indexes for reel_reports
CREATE INDEX idx_reel_reports_reel_id ON reel_reports(reel_id);
CREATE INDEX idx_reel_reports_reported_by ON reel_reports(reported_by);
CREATE INDEX idx_reel_reports_status ON reel_reports(status);
CREATE INDEX idx_reel_reports_created_at ON reel_reports(created_at DESC);
