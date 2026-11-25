package com.foodrescue.matching.persistence;

import com.foodrescue.matching.engine.MatchRecorder;
import com.foodrescue.matching.engine.MatchResult;
import com.foodrescue.matching.model.MatchRecord;
import com.foodrescue.matching.repository.MatchRecordRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaMatchRecorder implements MatchRecorder {

    private final MatchRecordRepository repository;

    public JpaMatchRecorder(MatchRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(String donationId, MatchResult result) {
        repository.save(new MatchRecord(donationId, result.candidate().charityId(), result.score()));
    }
}
