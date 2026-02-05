package com.travelplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "referrals")
public class ReferralEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String referralCode;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private int successfulReferrals;

    @Column(nullable = false)
    private int earnedCredits;

    public ReferralEntity() {
    }

    public ReferralEntity(UUID id, String referralCode, UUID ownerId) {
        this.id = id;
        this.referralCode = referralCode;
        this.ownerId = ownerId;
        this.successfulReferrals = 0;
        this.earnedCredits = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public int getSuccessfulReferrals() {
        return successfulReferrals;
    }

    public void setSuccessfulReferrals(int successfulReferrals) {
        this.successfulReferrals = successfulReferrals;
    }

    public int getEarnedCredits() {
        return earnedCredits;
    }

    public void setEarnedCredits(int earnedCredits) {
        this.earnedCredits = earnedCredits;
    }
}
