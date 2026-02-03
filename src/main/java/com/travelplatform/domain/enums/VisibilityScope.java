package com.travelplatform.domain.enums;

/**
 * Enumeration of content visibility scopes.
 * Controls who can view content like reels.
 */
public enum VisibilityScope {
    /**
     * Content is visible to everyone.
     */
    PUBLIC,

    /**
     * Content is visible only to followers.
     */
    FOLLOWERS_ONLY,

    /**
     * Content is private, visible only to the creator.
     */
    PRIVATE
}
