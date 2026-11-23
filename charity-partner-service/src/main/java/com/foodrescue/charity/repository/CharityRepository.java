package com.foodrescue.charity.repository;

import com.foodrescue.charity.model.Charity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharityRepository extends JpaRepository<Charity, String> {
}
