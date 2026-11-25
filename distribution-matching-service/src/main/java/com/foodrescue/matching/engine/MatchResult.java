package com.foodrescue.matching.engine;

public record MatchResult(Candidate candidate, double score, String reason) {
}
