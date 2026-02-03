package com.travelplatform.domain.enums;

/**
 * Enumeration of user account statuses.
 * Controls user access to the platform.
 */
public enum UserStatus {
    /**
     * User account is active and can access all features.
     */
    ACTIVE,

    /**
     * User account is suspended due to policy violations.
     * Cannot login or access platform features.
     */
    SUSPENDED,

    /**
     * User account has been deleted.
     * Data may be retained for compliance but user cannot access.
     */
    DELETED
}
