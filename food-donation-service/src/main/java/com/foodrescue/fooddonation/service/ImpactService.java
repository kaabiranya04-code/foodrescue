package com.foodrescue.fooddonation.service;

import com.foodrescue.fooddonation.dto.ImpactResponse;
import com.foodrescue.fooddonation.model.DonationStatus;
import com.foodrescue.fooddonation.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Calcule l'impact à partir des dons terminés ; coefficients lus dans la configuration. */
@Service
public class ImpactService {

    private final DonationRepository donations;
    private final double mealsPerKg;
    private final double co2KgPerKg;

    public ImpactService(DonationRepository donations,
                         @Value("${impact.meals-per-kg:2.0}") double mealsPerKg,
                         @Value("${impact.co2-kg-per-kg:2.5}") double co2KgPerKg) {
        this.donations = donations;
        this.mealsPerKg = mealsPerKg;
        this.co2KgPerKg = co2KgPerKg;
    }

    @Transactional(readOnly = true)
    public ImpactResponse compute() {
        double kg = donations.sumQuantityByStatus(DonationStatus.COMPLETED);
        long count = donations.countByStatus(DonationStatus.COMPLETED);
        return new ImpactResponse(round(kg), count, round(kg * mealsPerKg), round(kg * co2KgPerKg),
                mealsPerKg, co2KgPerKg);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
