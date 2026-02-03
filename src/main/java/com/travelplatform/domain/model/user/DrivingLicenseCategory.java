package com.travelplatform.domain.model.user;

/**
 * Enumeration of driving license categories for car rentals.
 */
public enum DrivingLicenseCategory {
    /**
     * No driving license.
     */
    NONE,

    /**
     * Motorcycle license.
     */
    A,

    /**
     * Car license (up to 3.5 tons).
     */
    B,

    /**
     * Medium vehicle license (3.5-7.5 tons).
     */
    C,

    /**
     * Heavy vehicle license (over 7.5 tons).
     */
    D,

    /**
     * Bus license.
     */
    E,

    /**
     * International driving permit.
     */
    INTERNATIONAL
}
