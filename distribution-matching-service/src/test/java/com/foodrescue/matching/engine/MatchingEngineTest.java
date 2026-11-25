package com.foodrescue.matching.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests unitaires des règles de matching (sans gRPC ni base de données). */
class MatchingEngineTest {

    private final MatchingEngine engine = new MatchingEngine();

    private final Candidate tunis = new Candidate("CH-0001", "Amal", "Tunis", 500,
            Set.of("khobz", "legumes"));
    private final Candidate sfax = new Candidate("CH-0002", "Croissant-Rouge", "Sfax", 300,
            Set.of("khobz", "fruits"));
    private final List<Candidate> both = List.of(tunis, sfax);

    private MatchCriteria criteria(String foodType, double qty, String region, int hours) {
        return new MatchCriteria("D-1", foodType, qty, region, hours);
    }

    @Test
    void sameRegionCandidateIsPreferred() {
        MatchResult result = engine.best(criteria("khobz", 100, "Sfax", 24), both).orElseThrow();
        assertThat(result.candidate().charityId()).isEqualTo("CH-0002");
    }

    @Test
    void candidateTooSmallIsExcludedEvenInSameRegion() {
        // 400 kg > capacité de Sfax (300) : Tunis est choisie malgré la région
        MatchResult result = engine.best(criteria("khobz", 400, "Sfax", 24), both).orElseThrow();
        assertThat(result.candidate().charityId()).isEqualTo("CH-0001");
    }

    @Test
    void foodTypeNotAcceptedGivesNoMatch() {
        assertThat(engine.best(criteria("poisson", 10, "Tunis", 24), both)).isEmpty();
    }

    @Test
    void wildcardCandidateAcceptsAnyFoodType() {
        Candidate anything = new Candidate("CH-0003", "Banque", "Sousse", 1000, Set.of("*"));
        assertThat(engine.best(criteria("poisson", 10, "Tunis", 24), List.of(anything))).isPresent();
    }

    @Test
    void urgencyAddsTenPointsInSameRegion() {
        double urgent = engine.score(criteria("khobz", 100, "Tunis", 3), tunis);
        double relaxed = engine.score(criteria("khobz", 100, "Tunis", 24), tunis);
        assertThat(urgent - relaxed).isCloseTo(10.0, within(0.001));
    }

    @Test
    void urgencyDoesNotApplyOutsideRegion() {
        double urgent = engine.score(criteria("khobz", 100, "Sfax", 3), tunis);
        double relaxed = engine.score(criteria("khobz", 100, "Sfax", 24), tunis);
        assertThat(urgent).isEqualTo(relaxed);
    }
}
