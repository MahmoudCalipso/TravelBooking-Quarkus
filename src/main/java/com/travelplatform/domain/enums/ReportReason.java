package com.travelplatform.domain.enums;

/**
 * Enumeration of reasons for reporting content.
 */
public enum ReportReason {
    /**
     * Content is spam or repetitive.
     */
    SPAM,

    /**
     * Content contains inappropriate or offensive material.
     */
    INAPPROPRIATE,

    /**
     * Content is misleading or false.
     */
    MISLEADING,

    /**
     * Content violates copyright.
     */
    COPYRIGHT,

    /**
     * Content contains violence or threats.
     */
    VIOLENCE,

    /**
     * Content contains hate speech.
     */
    HATE_SPEECH,

    /**
     * Content contains harassment or bullying.
     */
    HARASSMENT,

    /**
     * Other reason not listed.
     */
    OTHER
}
