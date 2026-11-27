package com.foodrescue.fooddonation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "restaurant")
public class Restaurant {

    @Id
    private String id;
    private String name;
    private String city;
    private String contactEmail;
    private Instant createdAt;

    protected Restaurant() {
        // requis par JPA
    }

    public Restaurant(String name, String city, String contactEmail) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.city = city;
        this.contactEmail = contactEmail;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getContactEmail() { return contactEmail; }
    public Instant getCreatedAt() { return createdAt; }
}
