package com.foodrescue.fooddonation.dto;

import com.foodrescue.fooddonation.model.Donation;
import com.foodrescue.fooddonation.model.DonationStatus;
import java.time.Instant;

public record DonationResponse(String id, String batchId, String charityId, double quantityKg,
                               DonationStatus status, String receiptNumber,
                               Instant createdAt, Instant updatedAt) {

    public static DonationResponse from(Donation d) {
        return new DonationResponse(d.getId(), d.getBatchId(), d.getCharityId(), d.getQuantityKg(),
                d.getStatus(), d.getReceiptNumber(), d.getCreatedAt(), d.getUpdatedAt());
    }
}
