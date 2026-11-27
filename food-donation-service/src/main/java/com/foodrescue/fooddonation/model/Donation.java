package com.foodrescue.fooddonation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "donation")
public class Donation {

    @Id
    private String id;
    private String batchId;
    private String charityId;
    private double quantityKg;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;

    private String receiptNumber;
    private Instant createdAt;
    private Instant updatedAt;

    protected Donation() {
        // requis par JPA
    }

    public Donation(String batchId, double quantityKg) {
        this.id = UUID.randomUUID().toString();
        this.batchId = batchId;
        this.quantityKg = quantityKg;
        this.status = DonationStatus.CREATED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void complete(String charityId, String receiptNumber) {
        this.charityId = charityId;
        this.receiptNumber = receiptNumber;
        changeStatus(DonationStatus.COMPLETED);
    }

    public void changeStatus(DonationStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getBatchId() { return batchId; }
    public String getCharityId() { return charityId; }
    public double getQuantityKg() { return quantityKg; }
    public DonationStatus getStatus() { return status; }
    public String getReceiptNumber() { return receiptNumber; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
