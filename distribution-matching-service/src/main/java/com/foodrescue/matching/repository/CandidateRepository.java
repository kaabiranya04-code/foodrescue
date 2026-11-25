package com.foodrescue.matching.repository;

import com.foodrescue.matching.model.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<CandidateEntity, String> {
}
