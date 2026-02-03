-- ============================================
-- Media Assets Table Migration
-- ============================================
-- This migration creates the media_assets table for storing
-- metadata about media files stored in Firebase Storage.
-- Media files are NOT stored in the database, only metadata.
-- ============================================

-- Create media_assets table
CREATE TABLE IF NOT EXISTS media_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    owner_type VARCHAR(50) NOT NULL,
    media_type VARCHAR(50) NOT NULL,
    firebase_path VARCHAR(500) NOT NULL,
    public_url TEXT,
    size_bytes BIGINT NOT NULL DEFAULT 0,
    mime_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add check constraint for owner_type
ALTER TABLE media_assets 
ADD CONSTRAINT chk_media_assets_owner_type 
CHECK (owner_type IN ('USER', 'ACCOMMODATION', 'TRAVEL_REEL', 'REVIEW', 'EVENT', 'CHAT_MESSAGE'));

-- Add check constraint for media_type
ALTER TABLE media_assets 
ADD CONSTRAINT chk_media_assets_media_type 
CHECK (media_type IN ('IMAGE', 'VIDEO', 'AUDIO', 'DOCUMENT'));

-- Add check constraint for size_bytes (must be non-negative)
ALTER TABLE media_assets 
ADD CONSTRAINT chk_media_assets_size_bytes 
CHECK (size_bytes >= 0);

-- Create indexes for performance
CREATE INDEX idx_media_assets_owner_id ON media_assets(owner_id);
CREATE INDEX idx_media_assets_owner_type ON media_assets(owner_type);
CREATE INDEX idx_media_assets_media_type ON media_assets(media_type);
CREATE INDEX idx_media_assets_firebase_path ON media_assets(firebase_path);
CREATE INDEX idx_media_assets_created_at ON media_assets(created_at DESC);

-- Create composite index for owner queries
CREATE INDEX idx_media_assets_owner_composite ON media_assets(owner_id, owner_type, media_type);

-- Add comment to table
COMMENT ON TABLE media_assets IS 'Stores metadata for media files stored in Firebase Storage. Files are NOT stored in database, only metadata and URLs.';

-- Add comments to columns
COMMENT ON COLUMN media_assets.id IS 'Unique identifier for the media asset';
COMMENT ON COLUMN media_assets.owner_id IS 'ID of the entity that owns this media (user, accommodation, reel, etc.)';
COMMENT ON COLUMN media_assets.owner_type IS 'Type of entity that owns this media (USER, ACCOMMODATION, TRAVEL_REEL, REVIEW, EVENT, CHAT_MESSAGE)';
COMMENT ON COLUMN media_assets.media_type IS 'Type of media file (IMAGE, VIDEO, AUDIO, DOCUMENT)';
COMMENT ON COLUMN media_assets.firebase_path IS 'Path to the file in Firebase Storage bucket';
COMMENT ON COLUMN media_assets.public_url IS 'Public URL for accessing the media file (signed URL)';
COMMENT ON COLUMN media_assets.size_bytes IS 'Size of the media file in bytes';
COMMENT ON COLUMN media_assets.mime_type IS 'MIME type of the media file (e.g., image/jpeg, video/mp4)';
COMMENT ON COLUMN media_assets.created_at IS 'Timestamp when the media asset was created';
COMMENT ON COLUMN media_assets.updated_at IS 'Timestamp when the media asset was last updated';

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_media_assets_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_media_assets_updated_at
BEFORE UPDATE ON media_assets
FOR EACH ROW
EXECUTE FUNCTION update_media_assets_updated_at();
