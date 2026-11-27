package com.foodrescue.fooddonation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import jakarta.validation.constraints.Future;

public record CreateFoodBatchRequest(
        @NotBlank(message = "restaurantId est obligatoire") String restaurantId,
        @NotBlank(message = "foodType est obligatoire") String foodType,
        @Positive(message = "quantityKg doit etre > 0")
        @Max(value = 10000, message = "quantityKg doit etre <= 10000") double quantityKg,
        @NotNull(message = "expiresAt est obligatoire")
        @Future(message = "expiresAt doit etre dans le futur") Instant expiresAt) {
}
