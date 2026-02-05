package com.travelplatform.application.service.engagement;

import com.travelplatform.infrastructure.persistence.entity.ReferralEntity;
import com.travelplatform.infrastructure.persistence.repository.JpaReferralRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple referral program service with in-memory tracking.
 */
@ApplicationScoped
public class ReferralProgramService {

    private final Map<String, UUID> referralCodeOwners = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> referralEarnings = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> successfulReferrals = new ConcurrentHashMap<>();

    @Inject
    JpaReferralRepository referralRepository;

    /**
     * Generate a unique referral code for a user.
     */
    public String generateReferralCode(UUID userId) {
        String code = ("REF-" + userId.toString().substring(0, 6)).toUpperCase();
        referralCodeOwners.putIfAbsent(code, userId);
        referralRepository.findByCode(code).orElseGet(() -> {
            ReferralEntity entity = new ReferralEntity(UUID.randomUUID(), code, userId);
            referralRepository.persist(entity);
            return entity;
        });
        return code;
    }

    /**
     * Track referral usage.
     */
    public void trackReferral(String referralCode, UUID newUserId) {
        referralRepository.findByCode(referralCode).ifPresent(entity -> {
            if (!entity.getOwnerId().equals(newUserId)) {
                entity.setSuccessfulReferrals(entity.getSuccessfulReferrals() + 1);
                entity.setEarnedCredits(entity.getEarnedCredits() + 25);
                referralRepository.persist(entity);
                referralCodeOwners.putIfAbsent(referralCode, entity.getOwnerId());
                successfulReferrals.merge(entity.getOwnerId(), 1, Integer::sum);
                referralEarnings.merge(entity.getOwnerId(), 25, Integer::sum);
            }
        });
    }

    public ReferralStats getReferralStats(UUID userId) {
        ReferralEntity entity = referralRepository.findByOwnerId(userId)
                .orElseGet(() -> {
                    String code = generateReferralCode(userId);
                    ReferralEntity newEntity = new ReferralEntity(UUID.randomUUID(), code, userId);
                    referralRepository.persist(newEntity);
                    return newEntity;
                });
        return new ReferralStats(
                entity.getReferralCode(),
                entity.getSuccessfulReferrals(),
                entity.getEarnedCredits());
    }

    public record ReferralStats(String referralCode, int totalReferrals, int earnedCredits) {
    }
}
