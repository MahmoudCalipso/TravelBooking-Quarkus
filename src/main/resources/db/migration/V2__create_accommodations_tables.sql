-- ============================================
-- Accommodation Module Tables
-- ============================================

-- Table: accommodations
-- Main table for all accommodation listings from suppliers
CREATE TABLE accommodations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('HOSTEL', 'HOTEL', 'APARTMENT', 'HOUSE', 'VILLA', 'RESORT')),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    base_price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    max_guests INTEGER NOT NULL,
    bedrooms INTEGER,
    beds INTEGER,
    bathrooms DECIMAL(3,1),
    square_meters INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED')),
    visibility_start TIMESTAMP,
    visibility_end TIMESTAMP,
    is_premium BOOLEAN DEFAULT FALSE,
    is_instant_book BOOLEAN DEFAULT FALSE,
    check_in_time TIME,
    check_out_time TIME,
    minimum_nights INTEGER DEFAULT 1,
    maximum_nights INTEGER,
    cancellation_policy VARCHAR(50) CHECK (cancellation_policy IN ('FLEXIBLE', 'MODERATE', 'STRICT', 'SUPER_STRICT')),
    view_count BIGINT DEFAULT 0,
    booking_count INTEGER DEFAULT 0,
    average_rating DECIMAL(3,2),
    review_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by UUID REFERENCES users(id)
);

-- Indexes for accommodations
CREATE INDEX idx_accommodations_supplier_id ON accommodations(supplier_id);
CREATE INDEX idx_accommodations_status ON accommodations(status);
CREATE INDEX idx_accommodations_city_country ON accommodations(city, country);
CREATE INDEX idx_accommodations_location ON accommodations USING GIST (latitude, longitude);
CREATE INDEX idx_accommodations_search ON accommodations(status, is_premium, average_rating);
CREATE INDEX idx_accommodations_visibility ON accommodations(visibility_start, visibility_end);

-- Table: accommodation_images
-- Multiple images per accommodation
CREATE TABLE accommodation_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    accommodation_id UUID NOT NULL REFERENCES accommodations(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    caption VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_accommodation_images_one_primary CHECK (
        (SELECT COUNT(*) FROM accommodation_images WHERE accommodation_id = accommodation_images.accommodation_id AND is_primary = TRUE) <= 1
    )
);

-- Indexes for accommodation_images
CREATE INDEX idx_accommodation_images_accommodation_id ON accommodation_images(accommodation_id);
CREATE INDEX idx_accommodation_images_order ON accommodation_images(accommodation_id, display_order);

-- Table: accommodation_amenities
-- Features and facilities offered
CREATE TABLE accommodation_amenities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    accommodation_id UUID NOT NULL REFERENCES accommodations(id) ON DELETE CASCADE,
    amenity_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) CHECK (category IN ('BASIC', 'SAFETY', 'KITCHEN', 'BATHROOM', 'OUTDOOR', 'ENTERTAINMENT', 'ACCESSIBILITY', 'PARKING')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_accommodation_amenities UNIQUE (accommodation_id, amenity_name)
);

-- Index for accommodation_amenities
CREATE INDEX idx_accommodation_amenities_accommodation_id ON accommodation_amenities(accommodation_id);

-- Table: accommodation_availability
-- Calendar-based availability and pricing
CREATE TABLE accommodation_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    accommodation_id UUID NOT NULL REFERENCES accommodations(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    price_override DECIMAL(10,2),
    minimum_nights_override INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_accommodation_availability UNIQUE (accommodation_id, date)
);

-- Index for accommodation_availability
CREATE INDEX idx_accommodation_availability_accommodation_id ON accommodation_availability(accommodation_id);
CREATE INDEX idx_accommodation_availability_date ON accommodation_availability(date);
