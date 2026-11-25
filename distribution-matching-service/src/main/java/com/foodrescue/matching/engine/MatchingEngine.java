package com.foodrescue.matching.engine;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Règles de matching :
 * <ul>
 *   <li>éligible : accepte le type de nourriture ET capacité >= quantité ;</li>
 *   <li>score = 40 (même région) + 50 x min(1, quantité / capacité) + 10 (urgent, même région) ;</li>
 *   <li>urgent = expire dans 1 à 6 heures ; égalité départagée par charityId.</li>
 * </ul>
 */
@Component
public class MatchingEngine {

    public List<Candidate> eligible(MatchCriteria criteria, List<Candidate> all) {
        return all.stream()
                .filter(c -> c.accepts(criteria.foodType()))
                .filter(c -> c.capacityKg() >= criteria.quantityKg())
                .toList();
    }

    public Optional<MatchResult> best(MatchCriteria criteria, List<Candidate> all) {
        return eligible(criteria, all).stream()
                .map(c -> new MatchResult(c, score(criteria, c), explain(criteria, c)))
                .sorted(Comparator.comparingDouble(MatchResult::score).reversed()
                        .thenComparing(r -> r.candidate().charityId()))
                .findFirst();
    }

    double score(MatchCriteria criteria, Candidate candidate) {
        boolean sameRegion = isSameRegion(criteria, candidate);
        double fit = Math.min(1.0, criteria.quantityKg() / candidate.capacityKg());
        double score = (sameRegion ? 40 : 0) + 50 * fit + (sameRegion && isUrgent(criteria) ? 10 : 0);
        return Math.round(score * 100.0) / 100.0;
    }

    private String explain(MatchCriteria criteria, Candidate candidate) {
        StringBuilder sb = new StringBuilder();
        sb.append(isSameRegion(criteria, candidate) ? "meme region" : "region differente");
        sb.append(", capacite utilisee a ")
                .append(Math.round(100 * Math.min(1.0, criteria.quantityKg() / candidate.capacityKg())))
                .append("%");
        if (isSameRegion(criteria, candidate) && isUrgent(criteria)) {
            sb.append(", bonus urgence");
        }
        return sb.toString();
    }

    private boolean isSameRegion(MatchCriteria criteria, Candidate candidate) {
        return candidate.region().equalsIgnoreCase(criteria.region());
    }

    private boolean isUrgent(MatchCriteria criteria) {
        return criteria.hoursBeforeExpiry() > 0 && criteria.hoursBeforeExpiry() <= 6;
    }
}
