package com.foodrescue.matching.config;

import com.foodrescue.matching.model.CandidateEntity;
import com.foodrescue.matching.repository.CandidateRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Associations candidates de démonstration (mêmes identifiants que le charity-partner-service). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CandidateRepository repository;

    public DataSeeder(CandidateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                    new CandidateEntity("CH-0001", "Association Amal", "Tunis", 500,
                            "khobz,legumes,plats-prepares"),
                    new CandidateEntity("CH-0002", "Croissant-Rouge Sfax", "Sfax", 300,
                            "khobz,fruits,legumes"),
                    new CandidateEntity("CH-0003", "Association Non Agreee", "Sousse", 1000, "*")));
        }
    }
}
