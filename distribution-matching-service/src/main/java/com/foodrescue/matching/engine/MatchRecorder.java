package com.foodrescue.matching.engine;

/** Garde une trace de chaque matching réalisé. */
@FunctionalInterface
public interface MatchRecorder {

    void record(String donationId, MatchResult result);
}
