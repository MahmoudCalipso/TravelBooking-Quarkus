package com.travelplatform.application.service.engagement;

import com.travelplatform.domain.model.booking.Booking;
import com.travelplatform.domain.valueobject.Money;
import com.travelplatform.infrastructure.persistence.entity.LoyaltyAccountEntity;
import com.travelplatform.infrastructure.persistence.repository.JpaLoyaltyAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Loyalty program service with persistence plus a short-lived in-memory cache
 * to reduce read load while keeping writes authoritative in the database.
 */
@ApplicationScoped
public class LoyaltyProgramService {

    private static final Logger log = LoggerFactory.getLogger(LoyaltyProgramService.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    @Inject
    JpaLoyaltyAccountRepository loyaltyAccountRepository;

    private final Map<UUID, CacheEntry> loyaltyCache = new ConcurrentHashMap<>();

    /**
     * Award points for a booking.
     * Rule: 1 point per currency unit of total price.
     */
    public void awardPoints(UUID userId, Booking booking) {
        int points = booking != null ? booking.getTotalPrice().getAmount().intValue() : 0;
        LoyaltyAccountEntity entity = getOrCreateAccount(userId);
        entity.setPoints(entity.getPoints() + points);
        entity.setBadges(String.join(",", computeBadges(entity.getPoints())));
        loyaltyAccountRepository.persist(entity);
        updateCache(userId, entity.getPoints(), entity.getBadges());
        log.debug("Awarded {} points to user {}", points, userId);
    }

    /**
     * Redeem points for a monetary credit. 100 points = 1 unit of currency.
     */
    public Money redeemPoints(UUID userId, int pointsToRedeem, String currency) {
        LoyaltyAccountEntity account = loyaltyAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Loyalty account not found"));
        int current = account.getPoints();
        if (pointsToRedeem <= 0 || pointsToRedeem > current) {
            throw new IllegalArgumentException("Insufficient points");
        }
        account.setPoints(current - pointsToRedeem);
        loyaltyAccountRepository.persist(account);
        updateCache(userId, account.getPoints(), account.getBadges());
        BigDecimal credit = BigDecimal.valueOf(pointsToRedeem).divide(BigDecimal.valueOf(100));
        return new Money(credit, currency);
    }

    /**
     * Get current badge list for a user.
     */
    public Set<String> getUserBadges(UUID userId) {
        CacheEntry cached = loyaltyCache.get(userId);
        if (cached != null && !cached.isExpired()) {
            return parseBadges(cached.badges());
        }

        return loyaltyAccountRepository.findByUserId(userId)
                .map(a -> {
                    updateCache(userId, a.getPoints(), a.getBadges());
                    return parseBadges(a.getBadges());
                })
                .orElseGet(Set::of);
    }

    /**
     * Get current points for a user.
     */
    public int getPoints(UUID userId) {
        CacheEntry cached = loyaltyCache.get(userId);
        if (cached != null && !cached.isExpired()) {
            return cached.points();
        }
        return loyaltyAccountRepository.findByUserId(userId)
                .map(a -> {
                    updateCache(userId, a.getPoints(), a.getBadges());
                    return a.getPoints();
                })
                .orElse(0);
    }

    private Set<String> computeBadges(int points) {
        return switch (points / 300) {
            case 0 -> Set.of();
            case 1 -> Set.of("Bronze");
            case 2, 3 -> Set.of("Silver");
            case 4, 5, 6, 7 -> Set.of("Gold");
            default -> Set.of("Platinum");
        };
    }

    private LoyaltyAccountEntity getOrCreateAccount(UUID userId) {
        return loyaltyAccountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    LoyaltyAccountEntity entity = new LoyaltyAccountEntity(UUID.randomUUID(), userId, 0, "");
                    loyaltyAccountRepository.persist(entity);
                    return entity;
                });
    }

    private Set<String> parseBadges(String badges) {
        return Arrays.stream(badges != null ? badges.split(",") : new String[0])
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }

    private void updateCache(UUID userId, int points, String badges) {
        loyaltyCache.put(userId, new CacheEntry(points, badges != null ? badges : "", Instant.now()));
    }

    private record CacheEntry(int points, String badges, Instant createdAt) {
        boolean isExpired() {
            return Instant.now().isAfter(createdAt.plus(CACHE_TTL));
        }
    }
}
