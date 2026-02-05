package com.travelplatform.application.service.trust;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Basic rule-based fraud scoring for bookings.
 */
@ApplicationScoped
public class FraudDetectionService {

    public FraudScore analyzeBooking(LocalDate accountCreatedAt, boolean emailVerified, boolean phoneVerified,
                                     int bookingsLast24h, double bookingAmount, double averageUserAmount,
                                     boolean vpnDetected) {
        double score = 0.0;
        if (accountCreatedAt != null && accountCreatedAt.isAfter(LocalDate.now().minusDays(7))) {
            score += 20;
        }
        if (!emailVerified || !phoneVerified) {
            score += 15;
        }
        if (bookingsLast24h >= 5) {
            score += 30;
        }
        if (averageUserAmount > 0 && bookingAmount > averageUserAmount * 1.5) {
            score += 25;
        }
        if (vpnDetected) {
            score += 40;
        }

        if (score > 70) {
            return FraudScore.HIGH_RISK;
        } else if (score > 40) {
            return FraudScore.MEDIUM_RISK;
        }
        return FraudScore.LOW_RISK;
    }

    public enum FraudScore {
        LOW_RISK,
        MEDIUM_RISK,
        HIGH_RISK
    }
}
