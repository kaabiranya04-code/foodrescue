package com.foodrescue.fooddonation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDonationRequest(@NotBlank(message = "batchId est obligatoire") String batchId) {
}
