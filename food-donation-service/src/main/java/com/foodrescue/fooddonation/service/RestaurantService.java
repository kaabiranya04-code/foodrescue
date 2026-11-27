package com.foodrescue.fooddonation.service;

import com.foodrescue.fooddonation.dto.CreateRestaurantRequest;
import com.foodrescue.fooddonation.dto.RestaurantResponse;
import com.foodrescue.fooddonation.exception.NotFoundException;
import com.foodrescue.fooddonation.model.Restaurant;
import com.foodrescue.fooddonation.repository.RestaurantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurants;

    public RestaurantService(RestaurantRepository restaurants) {
        this.restaurants = restaurants;
    }

    @Transactional
    public RestaurantResponse create(CreateRestaurantRequest request) {
        Restaurant saved = restaurants.save(
                new Restaurant(request.name(), request.city(), request.contactEmail()));
        return RestaurantResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> list() {
        return restaurants.findAll().stream().map(RestaurantResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse get(String id) {
        return restaurants.findById(id).map(RestaurantResponse::from)
                .orElseThrow(() -> new NotFoundException("Restaurant introuvable : " + id));
    }
}
