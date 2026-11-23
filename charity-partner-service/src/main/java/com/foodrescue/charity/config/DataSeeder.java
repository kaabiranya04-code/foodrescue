package com.foodrescue.charity.config;

import com.foodrescue.charity.model.Charity;
import com.foodrescue.charity.repository.CharityRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Données de démonstration : le "système hérité" du partenaire. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CharityRepository charities;

    public DataSeeder(CharityRepository charities) {
        this.charities = charities;
    }

    @Override
    public void run(String... args) {
        if (charities.count() == 0) {
            charities.saveAll(List.of(
                    new Charity("CH-0001", "Association Amal", "Tunis", true),
                    new Charity("CH-0002", "Croissant-Rouge Sfax", "Sfax", true),
                    new Charity("CH-0003", "Association Non Agréée", "Sousse", false)));
        }
    }
}
