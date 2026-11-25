package com.foodrescue.matching.repository;

import com.foodrescue.matching.model.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, String> {
}
