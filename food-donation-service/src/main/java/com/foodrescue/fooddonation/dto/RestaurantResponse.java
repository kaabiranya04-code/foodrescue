package com.foodrescue.fooddonation.dto;

import com.foodrescue.fooddonation.model.Restaurant;
import java.time.Instant;

public record RestaurantResponse(String id, String name, String city, String contactEmail, Instant createdAt) {

    public static RestaurantResponse from(Restaurant r) {
        return new RestaurantResponse(r.getId(), r.getName(), r.getCity(), r.getContactEmail(), r.getCreatedAt());
    }
}
