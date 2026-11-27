package com.foodrescue.fooddonation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Lot de nourriture invendue proposé par un restaurant. */
@Entity
@Table(name = "food_batch")
public class FoodBatch {

    @Id
    private String id;
    private String restaurantId;
    private String foodType;
    private double quantityKg;
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    private Instant createdAt;

    protected FoodBatch() {
        // requis par JPA
    }

    public FoodBatch(String restaurantId, String foodType, double quantityKg, Instant expiresAt) {
        this.id = UUID.randomUUID().toString();
        this.restaurantId = restaurantId;
        this.foodType = foodType;
        this.quantityKg = quantityKg;
        this.expiresAt = expiresAt;
        this.status = BatchStatus.AVAILABLE;
        this.createdAt = Instant.now();
    }

    public void reserve() { this.status = BatchStatus.RESERVED; }
    public void markDonated() { this.status = BatchStatus.DONATED; }
    public void release() { this.status = BatchStatus.AVAILABLE; }

    public String getId() { return id; }
    public String getRestaurantId() { return restaurantId; }
    public String getFoodType() { return foodType; }
    public double getQuantityKg() { return quantityKg; }
    public Instant getExpiresAt() { return expiresAt; }
    public BatchStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
