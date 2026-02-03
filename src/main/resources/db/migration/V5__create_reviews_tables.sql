-- ============================================
-- Review & Rating Module Tables
-- ============================================

-- Table: reviews
-- User reviews for accommodations
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reviewer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    accommodation_id UUID NOT NULL REFERENCES accommodations(id) ON DELETE CASCADE,
    booking_id UUID REFERENCES bookings(id) ON DELETE SET NULL,
    overall_rating INTEGER NOT NULL CHECK (overall_rating >= 1 AND overall_rating <= 5),
    cleanliness_rating INTEGER CHECK (cleanliness_rating >= 1 AND cleanliness_rating <= 5),
    accuracy_rating INTEGER CHECK (accuracy_rating >= 1 AND accuracy_rating <= 5),
    communication_rating INTEGER CHECK (communication_rating >= 1 AND communication_rating <= 5),
    location_rating INTEGER CHECK (location_rating >= 1 AND location_rating <= 5),
    value_rating INTEGER CHECK (value_rating >= 1 AND value_rating <= 5),
    title VARCHAR(150),
    content TEXT NOT NULL,
    pros TEXT,
    cons TEXT,
    travel_type VARCHAR(50) CHECK (travel_type IN ('SOLO', 'COUPLE', 'FAMILY', 'FRIENDS', 'BUSINESS')),
    stayed_date DATE,
    is_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED')),
    helpful_count INTEGER DEFAULT 0,
    response_from_host TEXT,
    responded_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    CONSTRAINT chk_reviews_ratings CHECK (
        (overall_rating IS NOT NULL) OR
        (cleanliness_rating IS NOT NULL) OR
        (accuracy_rating IS NOT NULL) OR
        (communication_rating IS NOT NULL) OR
        (location_rating IS NOT NULL) OR
        (value_rating IS NOT NULL)
    )
);

-- Indexes for reviews
CREATE INDEX idx_reviews_accommodation_id ON reviews(accommodation_id);
CREATE INDEX idx_reviews_reviewer_id ON reviews(reviewer_id);
CREATE INDEX idx_reviews_booking_id ON reviews(booking_id);
CREATE INDEX idx_reviews_status ON reviews(status);
CREATE INDEX idx_reviews_created_at ON reviews(accommodation_id, status, created_at DESC);
CREATE INDEX idx_reviews_rating ON reviews(accommodation_id, overall_rating);

-- Table: review_helpful
-- Track which users found reviews helpful
CREATE TABLE review_helpful (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_helpful BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_review_helpful UNIQUE (review_id, user_id)
);

-- Indexes for review_helpful
CREATE INDEX idx_review_helpful_review_id ON review_helpful(review_id);
CREATE INDEX idx_review_helpful_user_id ON review_helpful(user_id);

-- Table: review_photos
-- Photos attached to reviews
CREATE TABLE review_photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    photo_url TEXT NOT NULL,
    caption VARCHAR(255),
    display_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for review_photos
CREATE INDEX idx_review_photos_review_id ON review_photos(review_id);
CREATE INDEX idx_review_photos_order ON review_photos(review_id, display_order);
