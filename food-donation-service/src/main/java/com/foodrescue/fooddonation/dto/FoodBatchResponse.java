package com.foodrescue.fooddonation.dto;

import com.foodrescue.fooddonation.model.BatchStatus;
import com.foodrescue.fooddonation.model.FoodBatch;
import java.time.Instant;

public record FoodBatchResponse(String id, String restaurantId, String foodType, double quantityKg,
                                Instant expiresAt, BatchStatus status, Instant createdAt) {

    public static FoodBatchResponse from(FoodBatch b) {
        return new FoodBatchResponse(b.getId(), b.getRestaurantId(), b.getFoodType(), b.getQuantityKg(),
                b.getExpiresAt(), b.getStatus(), b.getCreatedAt());
    }
}
