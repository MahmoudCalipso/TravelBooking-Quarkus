package com.travelplatform.domain.model.user;

/**
 * Enumeration of work status/occupation types for user profiles.
 */
public enum WorkStatus {
    /**
     * Currently employed.
     */
    WORKER,

    /**
     * Currently studying.
     */
    STUDENT,

    /**
     * Retired.
     */
    RETIRED,

    /**
     * Self-employed.
     */
    SELF_EMPLOYED,

    /**
     * Currently unemployed.
     */
    UNEMPLOYED,

    /**
     * Freelancer.
     */
    FREELANCER,

    /**
     * Entrepreneur/Business owner.
     */
    ENTREPRENEUR,

    /**
     * Other occupation not listed.
     */
    OTHER
}
