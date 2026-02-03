-- ============================================
-- Events & Travel Programs Module Tables
-- ============================================

-- Table: events
-- Events created by suppliers or associations
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_type VARCHAR(50) CHECK (event_type IN ('TOUR', 'WORKSHOP', 'FESTIVAL', 'ACTIVITY')),
    location_name VARCHAR(255),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    price_per_person DECIMAL(10,2),
    currency VARCHAR(3),
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by UUID REFERENCES users(id),
    CONSTRAINT chk_events_dates CHECK (end_date > start_date),
    CONSTRAINT chk_events_participants CHECK (current_participants >= 0 AND current_participants <= max_participants)
);

-- Indexes for events
CREATE INDEX idx_events_creator_id ON events(creator_id);
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_type ON events(event_type);
CREATE INDEX idx_events_dates ON events(start_date, end_date);
CREATE INDEX idx_events_location ON events USING GIST (latitude, longitude);
CREATE INDEX idx_events_created_at ON events(created_at DESC);

-- Table: event_participants
-- Participants in events
CREATE TABLE event_participants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'REGISTERED' CHECK (status IN ('REGISTERED', 'CANCELLED', 'ATTENDED', 'NO_SHOW')),
    CONSTRAINT uk_event_participants UNIQUE (event_id, user_id)
);

-- Indexes for event_participants
CREATE INDEX idx_event_participants_event_id ON event_participants(event_id);
CREATE INDEX idx_event_participants_user_id ON event_participants(user_id);
CREATE INDEX idx_event_participants_status ON event_participants(status);

-- Table: travel_programs
-- Organized group travel by associations
CREATE TABLE travel_programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    destination VARCHAR(255) NOT NULL,
    itinerary JSONB,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    price_per_person DECIMAL(10,2),
    currency VARCHAR(3),
    included_items TEXT[],
    excluded_items TEXT[],
    max_participants INTEGER,
    min_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    CONSTRAINT chk_travel_programs_dates CHECK (end_date > start_date),
    CONSTRAINT chk_travel_programs_participants CHECK (current_participants >= 0 AND current_participants <= max_participants),
    CONSTRAINT chk_travel_programs_min_max CHECK (min_participants <= max_participants)
);

-- Indexes for travel_programs
CREATE INDEX idx_travel_programs_organizer_id ON travel_programs(organizer_id);
CREATE INDEX idx_travel_programs_status ON travel_programs(status);
CREATE INDEX idx_travel_programs_dates ON travel_programs(start_date, end_date);
CREATE INDEX idx_travel_programs_destination ON travel_programs(destination);
CREATE INDEX idx_travel_programs_created_at ON travel_programs(created_at DESC);
