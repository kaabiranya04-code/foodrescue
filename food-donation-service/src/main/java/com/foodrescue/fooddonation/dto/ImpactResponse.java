package com.foodrescue.fooddonation.dto;

/** Impact cumulé des dons terminés ; les coefficients sont des estimations configurables. */
public record ImpactResponse(double kgSaved, long completedDonations, double mealsServed,
                             double co2AvoidedKg, double mealsPerKg, double co2KgPerKg) {
}
