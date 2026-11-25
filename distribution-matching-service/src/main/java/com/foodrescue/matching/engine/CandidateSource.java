package com.foodrescue.matching.engine;

import java.util.List;

/** Fournit la liste des associations candidates (base de données en production). */
@FunctionalInterface
public interface CandidateSource {

    List<Candidate> loadAll();
}
