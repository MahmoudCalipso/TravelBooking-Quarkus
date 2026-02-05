package com.travelplatform.application.service.trust;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

/**
 * Identity verification service (stubbed for integration with external KYC).
 */
@ApplicationScoped
public class IdentityVerificationService {

    public VerificationResult verifyIdentity(UUID userId, VerificationDocuments documents) {
        // Placeholder: assume verification succeeds when docs provided
        boolean success = documents != null && documents.documentType() != null && documents.documentUrl() != null;
        return new VerificationResult(success, success ? "VERIFIED" : "REJECTED");
    }

    public boolean verifyPhoneNumber(String phoneNumber, String code) {
        return phoneNumber != null && !phoneNumber.isBlank() && code != null && code.length() >= 4;
    }

    public record VerificationDocuments(String documentType, String documentUrl) {
    }

    public record VerificationResult(boolean verified, String status) {
    }
}
