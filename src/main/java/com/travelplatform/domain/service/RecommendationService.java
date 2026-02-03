package com.travelplatform.domain.service;

import com.travelplatform.domain.model.accommodation.Accommodation;
import com.travelplatform.domain.model.reel.TravelReel;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.model.user.UserPreferences;
import com.travelplatform.domain.valueobject.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain service for recommendations.
 * Handles recommendation algorithms for accommodations, reels, and destinations.
 */
public class RecommendationService {

    /**
     * Recommends accommodations based on user preferences.
     *
     * @param userPreferences user preferences
     * @param allAccommodations all available accommodations
     * @return list of recommended accommodations
     */
    public List<Accommodation> recommendAccommodations(UserPreferences userPreferences,
                                                         List<Accommodation> allAccommodations) {
        if (userPreferences == null) {
            throw new IllegalArgumentException("User preferences cannot be null");
        }
        if (allAccommodations == null) {
            throw new IllegalArgumentException("Accommodations cannot be null");
        }

        List<AccommodationScore> scoredAccommodations = new ArrayList<>();

        for (Accommodation accommodation : allAccommodations) {
            double score = calculateAccommodationScore(accommodation, userPreferences);
            scoredAccommodations.add(new AccommodationScore(accommodation, score));
        }

        // Sort by score (descending) and return top recommendations
        return scoredAccommodations.stream()
                .sorted(Comparator.comparingDouble(AccommodationScore::getScore).reversed())
                .limit(20)
                .map(AccommodationScore::getAccommodation)
                .collect(Collectors.toList());
    }

    /**
     * Recommends accommodations near a location.
     *
     * @param location           center location
     * @param allAccommodations all available accommodations
     * @param radiusKm           search radius in kilometers
     * @return list of recommended accommodations
     */
    public List<Accommodation> recommendNearbyAccommodations(Location location,
                                                             List<Accommodation> allAccommodations,
                                                             double radiusKm) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (allAccommodations == null) {
            throw new IllegalArgumentException("Accommodations cannot be null");
        }
        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }

        List<AccommodationScore> scoredAccommodations = new ArrayList<>();

        for (Accommodation accommodation : allAccommodations) {
            if (accommodation.getLocation() == null) {
                continue;
            }

            double distance = location.distanceTo(accommodation.getLocation());

            if (distance <= radiusKm) {
                // Score based on proximity and rating
                double score = calculateProximityScore(distance, accommodation.getAverageRating());
                scoredAccommodations.add(new AccommodationScore(accommodation, score));
            }
        }

        // Sort by score (descending)
        return scoredAccommodations.stream()
                .sorted(Comparator.comparingDouble(AccommodationScore::getScore).reversed())
                .map(AccommodationScore::getAccommodation)
                .collect(Collectors.toList());
    }

    /**
     * Recommends reels based on user preferences.
     *
     * @param userPreferences user preferences
     * @param allReels       all available reels
     * @return list of recommended reels
     */
    public List<TravelReel> recommendReels(UserPreferences userPreferences,
                                           List<TravelReel> allReels) {
        if (userPreferences == null) {
            throw new IllegalArgumentException("User preferences cannot be null");
        }
        if (allReels == null) {
            throw new IllegalArgumentException("Reels cannot be null");
        }

        List<ReelScore> scoredReels = new ArrayList<>();

        for (TravelReel reel : allReels) {
            double score = calculateReelScore(reel, userPreferences);
            scoredReels.add(new ReelScore(reel, score));
        }

        // Sort by score (descending) and return top recommendations
        return scoredReels.stream()
                .sorted(Comparator.comparingDouble(ReelScore::getScore).reversed())
                .limit(20)
                .map(ReelScore::getReel)
                .collect(Collectors.toList());
    }

    /**
     * Recommends trending reels.
     *
     * @param allReels all available reels
     * @return list of trending reels
     */
    public List<TravelReel> recommendTrendingReels(List<TravelReel> allReels) {
        if (allReels == null) {
            throw new IllegalArgumentException("Reels cannot be null");
        }

        List<ReelScore> scoredReels = new ArrayList<>();

        for (TravelReel reel : allReels) {
            double score = calculateTrendingScore(reel);
            scoredReels.add(new ReelScore(reel, score));
        }

        // Sort by score (descending) and return top trending
        return scoredReels.stream()
                .sorted(Comparator.comparingDouble(ReelScore::getScore).reversed())
                .limit(20)
                .map(ReelScore::getReel)
                .collect(Collectors.toList());
    }

    /**
     * Recommends similar accommodations.
     *
     * @param referenceAccommodation reference accommodation
     * @param allAccommodations     all available accommodations
     * @return list of similar accommodations
     */
    public List<Accommodation> recommendSimilarAccommodations(Accommodation referenceAccommodation,
                                                              List<Accommodation> allAccommodations) {
        if (referenceAccommodation == null) {
            throw new IllegalArgumentException("Reference accommodation cannot be null");
        }
        if (allAccommodations == null) {
            throw new IllegalArgumentException("Accommodations cannot be null");
        }

        List<AccommodationScore> scoredAccommodations = new ArrayList<>();

        for (Accommodation accommodation : allAccommodations) {
            // Skip the reference accommodation
            if (accommodation.getId().equals(referenceAccommodation.getId())) {
                continue;
            }

            double score = calculateSimilarityScore(referenceAccommodation, accommodation);
            scoredAccommodations.add(new AccommodationScore(accommodation, score));
        }

        // Sort by score (descending) and return top similar
        return scoredAccommodations.stream()
                .sorted(Comparator.comparingDouble(AccommodationScore::getScore).reversed())
                .limit(10)
                .map(AccommodationScore::getAccommodation)
                .collect(Collectors.toList());
    }

    /**
     * Recommends accommodations based on user's booking history.
     *
     * @param user               user
     * @param userBookings       user's past bookings
     * @param allAccommodations all available accommodations
     * @return list of recommended accommodations
     */
    public List<Accommodation> recommendBasedOnHistory(User user,
                                                      List<Accommodation> userBookedAccommodations,
                                                      List<Accommodation> allAccommodations) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (userBookedAccommodations == null) {
            throw new IllegalArgumentException("User bookings cannot be null");
        }
        if (allAccommodations == null) {
            throw new IllegalArgumentException("Accommodations cannot be null");
        }

        List<AccommodationScore> scoredAccommodations = new ArrayList<>();

        for (Accommodation accommodation : allAccommodations) {
            // Skip accommodations the user has already booked
            if (userBookedAccommodations.stream()
                    .anyMatch(a -> a.getId().equals(accommodation.getId()))) {
                continue;
            }

            double score = calculateHistoryBasedScore(accommodation, userBookedAccommodations);
            scoredAccommodations.add(new AccommodationScore(accommodation, score));
        }

        // Sort by score (descending) and return top recommendations
        return scoredAccommodations.stream()
                .sorted(Comparator.comparingDouble(AccommodationScore::getScore).reversed())
                .limit(10)
                .map(AccommodationScore::getAccommodation)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the score for an accommodation based on user preferences.
     *
     * @param accommodation   accommodation to score
     * @param userPreferences user preferences
     * @return calculated score
     */
    private double calculateAccommodationScore(Accommodation accommodation,
                                             UserPreferences userPreferences) {
        double score = 0.0;

        // Rating score (0-30 points)
        if (accommodation.getAverageRating() != null) {
            score += accommodation.getAverageRating() * 6;
        }

        // Booking count score (0-20 points)
        score += Math.min(accommodation.getBookingCount() * 0.5, 20);

        // Premium score (0-10 points)
        if (accommodation.isPremium()) {
            score += 10;
        }

        // Budget match score (0-20 points)
        if (userPreferences.getBudgetRange() != null) {
            score += calculateBudgetScore(accommodation.getBasePrice().getAmount(),
                    userPreferences.getBudgetRange());
        }

        // Travel style match score (0-20 points)
        if (userPreferences.getTravelStyle() != null) {
            score += calculateTravelStyleScore(accommodation.getType(),
                    userPreferences.getTravelStyle());
        }

        return score;
    }

    /**
     * Calculates the proximity score for an accommodation.
     *
     * @param distance distance in kilometers
     * @param rating   accommodation rating
     * @return calculated score
     */
    private double calculateProximityScore(double distance, Double rating) {
        // Closer is better (max 50 points for distance)
        double distanceScore = Math.max(0, 50 - distance);

        // Higher rating is better (max 50 points for rating)
        double ratingScore = rating != null ? rating * 10 : 0;

        return distanceScore + ratingScore;
    }

    /**
     * Calculates the score for a reel based on user preferences.
     *
     * @param reel            reel to score
     * @param userPreferences user preferences
     * @return calculated score
     */
    private double calculateReelScore(TravelReel reel, UserPreferences userPreferences) {
        double score = 0.0;

        // Engagement score (0-40 points)
        score += Math.min(reel.getLikeCount() * 0.1, 20);
        score += Math.min(reel.getViewCount() * 0.01, 20);

        // Completion rate score (0-20 points)
        if (reel.getCompletionRate() != null) {
            score += reel.getCompletionRate() * 0.2;
        }

        // Recency score (0-20 points)
        long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
                reel.getCreatedAt(), java.time.LocalDateTime.now()
        );
        score += Math.max(0, 20 - daysSinceCreation * 0.5);

        // Interest match score (0-20 points)
        if (userPreferences.getInterests() != null && reel.getTags() != null) {
            score += calculateInterestMatchScore(userPreferences.getInterests(), reel.getTags());
        }

        return score;
    }

    /**
     * Calculates the trending score for a reel.
     *
     * @param reel reel to score
     * @return calculated score
     */
    private double calculateTrendingScore(TravelReel reel) {
        double score = 0.0;

        // Recent engagement (last 7 days)
        long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
                reel.getCreatedAt(), java.time.LocalDateTime.now()
        );

        if (daysSinceCreation <= 7) {
            score += reel.getLikeCount() * 0.5;
            score += reel.getViewCount() * 0.05;
            score += reel.getShareCount() * 0.3;
        } else {
            // Older content gets lower weight
            score += reel.getLikeCount() * 0.1;
            score += reel.getViewCount() * 0.01;
            score += reel.getShareCount() * 0.05;
        }

        // Completion rate bonus
        if (reel.getCompletionRate() != null && reel.getCompletionRate() > 70) {
            score += 20;
        }

        return score;
    }

    /**
     * Calculates the similarity score between two accommodations.
     *
     * @param accommodation1 first accommodation
     * @param accommodation2 second accommodation
     * @return calculated score
     */
    private double calculateSimilarityScore(Accommodation accommodation1, Accommodation accommodation2) {
        double score = 0.0;

        // Type match (0-30 points)
        if (accommodation1.getType().equals(accommodation2.getType())) {
            score += 30;
        }

        // Price similarity (0-30 points)
        double priceDiff = Math.abs(accommodation1.getBasePrice().getAmount() -
                accommodation2.getBasePrice().getAmount());
        double priceScore = Math.max(0, 30 - priceDiff * 0.1);
        score += priceScore;

        // Location proximity (0-20 points)
        if (accommodation1.getLocation() != null && accommodation2.getLocation() != null) {
            double distance = accommodation1.getLocation().distanceTo(accommodation2.getLocation());
            score += Math.max(0, 20 - distance);
        }

        // Rating similarity (0-20 points)
        if (accommodation1.getAverageRating() != null && accommodation2.getAverageRating() != null) {
            double ratingDiff = Math.abs(accommodation1.getAverageRating() -
                    accommodation2.getAverageRating());
            score += Math.max(0, 20 - ratingDiff * 5);
        }

        return score;
    }

    /**
     * Calculates the history-based score for an accommodation.
     *
     * @param accommodation            accommodation to score
     * @param userBookedAccommodations accommodations user has booked
     * @return calculated score
     */
    private double calculateHistoryBasedScore(Accommodation accommodation,
                                          List<Accommodation> userBookedAccommodations) {
        double score = 0.0;

        for (Accommodation booked : userBookedAccommodations) {
            // Type similarity
            if (accommodation.getType().equals(booked.getType())) {
                score += 10;
            }

            // Price similarity
            double priceDiff = Math.abs(accommodation.getBasePrice().getAmount() -
                    booked.getBasePrice().getAmount());
            score += Math.max(0, 10 - priceDiff * 0.05);

            // Location proximity
            if (accommodation.getLocation() != null && booked.getLocation() != null) {
                double distance = accommodation.getLocation().distanceTo(booked.getLocation());
                score += Math.max(0, 10 - distance);
            }
        }

        return score;
    }

    /**
     * Calculates the budget match score.
     *
     * @param price       accommodation price
     * @param budgetRange user's budget range
     * @return calculated score
     */
    private double calculateBudgetScore(double price, String budgetRange) {
        // Simple budget matching logic
        switch (budgetRange.toUpperCase()) {
            case "BUDGET":
                if (price <= 50) return 20;
                if (price <= 100) return 10;
                return 0;
            case "MODERATE":
                if (price >= 50 && price <= 150) return 20;
                if (price >= 30 && price <= 200) return 10;
                return 0;
            case "LUXURY":
                if (price >= 150) return 20;
                if (price >= 100) return 10;
                return 0;
            default:
                return 0;
        }
    }

    /**
     * Calculates the travel style match score.
     *
     * @param accommodationType accommodation type
     * @param travelStyle      user's travel style
     * @return calculated score
     */
    private double calculateTravelStyleScore(String accommodationType, String travelStyle) {
        // Simple travel style matching logic
        switch (travelStyle.toUpperCase()) {
            case "ADVENTURE":
                if (accommodationType.equals("HOSTEL") || accommodationType.equals("APARTMENT")) {
                    return 20;
                }
                return 10;
            case "CULTURAL":
                if (accommodationType.equals("HOTEL") || accommodationType.equals("VILLA")) {
                    return 20;
                }
                return 10;
            case "RELAXATION":
                if (accommodationType.equals("RESORT") || accommodationType.equals("VILLA")) {
                    return 20;
                }
                return 10;
            case "BUSINESS":
                if (accommodationType.equals("HOTEL") || accommodationType.equals("APARTMENT")) {
                    return 20;
                }
                return 10;
            default:
                return 10;
        }
    }

    /**
     * Calculates the interest match score.
     *
     * @param userInterests user's interests
     * @param reelTags      reel tags
     * @return calculated score
     */
    private double calculateInterestMatchScore(List<String> userInterests, List<String> reelTags) {
        if (userInterests == null || reelTags == null) {
            return 0;
        }

        int matches = 0;
        for (String interest : userInterests) {
            for (String tag : reelTags) {
                if (interest.equalsIgnoreCase(tag)) {
                    matches++;
                    break;
                }
            }
        }

        return Math.min(matches * 5, 20); // Max 20 points
    }

    // Helper classes for scoring

    private static class AccommodationScore {
        private final Accommodation accommodation;
        private final double score;

        public AccommodationScore(Accommodation accommodation, double score) {
            this.accommodation = accommodation;
            this.score = score;
        }

        public Accommodation getAccommodation() {
            return accommodation;
        }

        public double getScore() {
            return score;
        }
    }

    private static class ReelScore {
        private final TravelReel reel;
        private final double score;

        public ReelScore(TravelReel reel, double score) {
            this.reel = reel;
            this.score = score;
        }

        public TravelReel getReel() {
            return reel;
        }

        public double getScore() {
            return score;
        }
    }
}
