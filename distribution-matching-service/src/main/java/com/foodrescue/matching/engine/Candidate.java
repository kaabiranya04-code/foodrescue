package com.foodrescue.matching.engine;

import java.util.Locale;
import java.util.Set;

/** Association candidate (modèle métier, indépendant de JPA et de gRPC). */
public record Candidate(String charityId, String name, String region,
                        double capacityKg, Set<String> acceptedFoodTypes) {

    public boolean accepts(String foodType) {
        return acceptedFoodTypes.contains("*")
                || acceptedFoodTypes.contains(foodType.toLowerCase(Locale.ROOT));
    }
}
