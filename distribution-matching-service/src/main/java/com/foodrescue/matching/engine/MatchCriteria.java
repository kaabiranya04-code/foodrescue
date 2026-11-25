package com.foodrescue.matching.engine;

public record MatchCriteria(String donationId, String foodType, double quantityKg,
                            String region, int hoursBeforeExpiry) {
}
