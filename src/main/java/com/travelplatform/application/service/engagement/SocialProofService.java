package com.travelplatform.application.service.engagement;

import com.travelplatform.infrastructure.persistence.entity.SocialProofEntity;
import com.travelplatform.infrastructure.persistence.repository.JpaSocialProofRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides lightweight social proof metrics for accommodations with a small
 * near-cache to avoid hammering the database on high-traffic listings.
 */
@ApplicationScoped
public class SocialProofService {

    private static final Duration CACHE_TTL = Duration.ofSeconds(45);

    @Inject
    JpaSocialProofRepository socialProofRepository;

    private final Map<UUID, CacheEntry> cache = new ConcurrentHashMap<>();

    public SocialProofData getSocialProof(UUID accommodationId) {
        CacheEntry cached = cache.get(accommodationId);
        if (cached != null && !cached.isExpired()) {
            return cached.data();
        }

        return socialProofRepository.findByIdOptional(accommodationId)
                .map(e -> new SocialProofData(e.getCurrentViewers(), e.getRecentBookings(),
                        Duration.ofSeconds(e.getLastBookedSeconds()), e.getPopularityScore()))
                .map(data -> cacheEntry(accommodationId, data))
                .orElseGet(() -> {
                    SocialProofData fresh = new SocialProofData(0, 0, Duration.ZERO, 0);
                    cacheEntry(accommodationId, fresh);
                    return fresh;
                });
    }

    public void recordView(UUID accommodationId) {
        SocialProofEntity entity = socialProofRepository.findByIdOptional(accommodationId)
                .orElseGet(() -> new SocialProofEntity(accommodationId));
        entity.setCurrentViewers(entity.getCurrentViewers() + 1);
        entity.setPopularityScore(entity.getPopularityScore() + 1);
        socialProofRepository.persist(entity);
        updateCacheFromEntity(entity);
    }

    public void recordBooking(UUID accommodationId) {
        SocialProofEntity entity = socialProofRepository.findByIdOptional(accommodationId)
                .orElseGet(() -> new SocialProofEntity(accommodationId));
        entity.setRecentBookings(entity.getRecentBookings() + 1);
        entity.setLastBookedSeconds(0);
        entity.setPopularityScore(entity.getPopularityScore() + 5);
        socialProofRepository.persist(entity);
        updateCacheFromEntity(entity);
    }

    public void updateLastBooked(UUID accommodationId, Instant lastBookedAt) {
        SocialProofEntity entity = socialProofRepository.findByIdOptional(accommodationId)
                .orElseGet(() -> new SocialProofEntity(accommodationId));
        entity.setLastBookedSeconds(Duration.between(lastBookedAt, Instant.now()).getSeconds());
        socialProofRepository.persist(entity);
        updateCacheFromEntity(entity);
    }

    public boolean isTrending(UUID accommodationId) {
        SocialProofData data = getSocialProof(accommodationId);
        return data.recentBookings() >= 5 || data.popularityScore() >= 20;
    }

    public record SocialProofData(int currentViewers, int recentBookings, Duration lastBookedAgo, int popularityScore) {
    }

    private SocialProofData cacheEntry(UUID accommodationId, SocialProofData data) {
        cache.put(accommodationId, new CacheEntry(data, Instant.now()));
        return data;
    }

    private void updateCacheFromEntity(SocialProofEntity entity) {
        cacheEntry(entity.getAccommodationId(),
                new SocialProofData(entity.getCurrentViewers(), entity.getRecentBookings(),
                        Duration.ofSeconds(entity.getLastBookedSeconds()), entity.getPopularityScore()));
    }

    private record CacheEntry(SocialProofData data, Instant createdAt) {
        boolean isExpired() {
            return Instant.now().isAfter(createdAt.plus(CACHE_TTL));
        }
    }
}
