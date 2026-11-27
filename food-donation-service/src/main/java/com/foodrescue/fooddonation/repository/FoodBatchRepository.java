package com.foodrescue.fooddonation.repository;

import com.foodrescue.fooddonation.model.BatchStatus;
import com.foodrescue.fooddonation.model.FoodBatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodBatchRepository extends JpaRepository<FoodBatch, String> {

    List<FoodBatch> findByStatus(BatchStatus status);
}
