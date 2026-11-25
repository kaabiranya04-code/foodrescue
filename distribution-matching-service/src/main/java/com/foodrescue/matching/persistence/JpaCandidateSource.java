package com.foodrescue.matching.persistence;

import com.foodrescue.matching.engine.Candidate;
import com.foodrescue.matching.engine.CandidateSource;
import com.foodrescue.matching.model.CandidateEntity;
import com.foodrescue.matching.repository.CandidateRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaCandidateSource implements CandidateSource {

    private final CandidateRepository repository;

    public JpaCandidateSource(CandidateRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Candidate> loadAll() {
        return repository.findAll().stream().map(CandidateEntity::toCandidate).toList();
    }
}
