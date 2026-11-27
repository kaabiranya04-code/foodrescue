package com.foodrescue.fooddonation.repository;

import com.foodrescue.fooddonation.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
}
