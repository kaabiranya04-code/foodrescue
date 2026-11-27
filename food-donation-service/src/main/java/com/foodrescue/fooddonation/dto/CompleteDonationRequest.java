package com.foodrescue.fooddonation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CompleteDonationRequest(
        @NotBlank(message = "charityId est obligatoire")
        @Pattern(regexp = "CH-[0-9]{4}", message = "charityId doit avoir le format CH-0000") String charityId,
        String receiptNumber) {
}
