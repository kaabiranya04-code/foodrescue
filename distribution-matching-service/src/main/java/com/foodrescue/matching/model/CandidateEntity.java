package com.foodrescue.matching.model;

import com.foodrescue.matching.engine.Candidate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "candidate")
public class CandidateEntity {

    @Id
    @Column(length = 10)
    private String charityId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private double capacityKg;

    /** Types séparés par des virgules ; "*" = tout accepté. */
    @Column(nullable = false, length = 500)
    private String acceptedFoodTypes;

    protected CandidateEntity() {
        // requis par JPA
    }

    public CandidateEntity(String charityId, String name, String region,
                           double capacityKg, String acceptedFoodTypes) {
        this.charityId = charityId;
        this.name = name;
        this.region = region;
        this.capacityKg = capacityKg;
        this.acceptedFoodTypes = acceptedFoodTypes;
    }

    public Candidate toCandidate() {
        Set<String> types = Arrays.stream(acceptedFoodTypes.split(","))
                .map(String::trim)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        return new Candidate(charityId, name, region, capacityKg, types);
    }
}
